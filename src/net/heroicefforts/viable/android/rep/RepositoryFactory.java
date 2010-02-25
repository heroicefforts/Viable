/*
 *  Copyright 2010 Heroic Efforts, LLC
 *  
 *  This file is part of Viable.
 *
 *  Viable is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Viable is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Viable.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.heroicefforts.viable.android.rep;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;

/**
 * A factory class for generating repositories associated with Viable client application.  The factory reads through the
 * manifests of the installed applications.  Any containing Viable meta-data are prepared for instantiation.  This should be
 * considered a heavy object.  It is thread-safe.<br/>
 * <br/>
 * Concrete repository implementations are expected to implement a public constructor matching the signature
 * ConcreteClass(String, Activity, Bundle).<br/>
 * <br/>
 * New repository classes must be registered with Viable by entering a meta-data entry in the Viable's manifest.  The key should
 * define the repository type name and the value should specify the fully qualified concrete repository class name.<br/>
 * <br/>
 * Client applications may reference repository types with the key "viable-provider" and value matching the repository type name.  Other
 * information may be required, but will be specific to the repository implementation.
 * 
 * @see net.heroicefforts.viable.android.rep.it.GIssueTrackerRepository
 * @see net.heroicefforts.viable.android.rep.jira.JIRARepository
 * 
 * @author jevans
 *
 */
public class RepositoryFactory
{
	private static final String TAG = "RepositoryFactory";
	private Map<String, Bundle> appBundles = new TreeMap<String, Bundle>();
	private Map<String, String> appVersion = new TreeMap<String, String>();
	private Bundle viableBundle;
	private Activity act;
	private HashMap<String, Repository> repMap = new HashMap<String, Repository>();
	private RepositoryRegistry registry;
	
	/**
	 * Instantiate the factory.
	 * 
	 * @param act the main activity
	 * @throws CreateException if Viable is misconfigured.
	 */
	public RepositoryFactory(Activity act)
		throws CreateException
	{
		this.act = act;
		PackageManager pkgMgr = act.getPackageManager();
		List<ApplicationInfo> infos = pkgMgr.getInstalledApplications(PackageManager.GET_META_DATA);
		for(ApplicationInfo app : infos)
		{
			String label = pkgMgr.getApplicationLabel(app).toString();
			if(app.metaData != null && app.metaData.getString("viable-provider") != null)
			{
				try
				{
					appBundles.put(label, app.metaData);
					PackageInfo info = pkgMgr.getPackageInfo(app.packageName, 0);
					appVersion.put(label, info.versionName);
				}
				catch (NameNotFoundException e)
				{
					//should never occur
					Log.e(TAG, "Couldn't find package info for package '" + app.packageName + "'?");
				}
			}

			if("Viable".equals(label))
				this.viableBundle = app.metaData;
		}
		
		if(viableBundle == null)
			throw new CreateException("Cannot create factory.  Could not locate 'Viable' application info.  Was the app renamed?");
		
		registry = new RepositoryRegistry(act);
	}
	
	/**
	 * Returns the names of the Viable client applications.
	 * @return a non-null set of application labels.
	 */
	public Set<String> getApplicationNames()
	{
		return Collections.unmodifiableSet(appBundles.keySet());
	}
	
	/**
	 * Returns the current version of the specified application installed on this device.
	 * @param appName the application label.
	 * @return the version name
	 */
	public String getApplicationVersion(String appName)
	{
		return appVersion.get(appName);
	}
	
	/**
	 * Instantiates a remote repository for this application. 
	 * @param appName the application label.
	 * @return a remote issue repository.
	 * @throws CreateException if the application clients Viable configuration is incorrect.
	 */
	public Repository getRepository(String appName)
		throws CreateException
	{
		synchronized(repMap)
		{
			Repository rep = repMap.get(appName);
			
			if(rep == null)
			{
				Bundle metaData = appBundles.get(appName);
				if(metaData != null)
				{
					String providerName = metaData.getString("viable-provider");
					if(providerName != null)
					{
						rep = instantiateRepository(appName, providerName, act, metaData);
						repMap.put(appName, rep);					
					}
					else
						Log.e(TAG, "No '" + "viable-provider" + "' meta-data field defined for application '" + appName + "'.  Repository cannot be constructed.");
						
				}
				else
				{
					RegEntry entry = registry.getEntryForApp(appName);
					if(entry != null)
					{
						Log.d(TAG, "Application is no longer installed.  Resorting to registry.");
						rep = instantiateRepository(entry);
					}
					else
						Log.e(TAG, "No meta-data bundle defined for application '" + appName + "'.  Repository cannot be constructed.");
				}
			}
			
			return rep;
		}		
	}

	public Repository instantiateRepository(RegEntry entry)
	{
		Bundle metaData = entry.getParams();
		String providerName = metaData.getString("viable-provider"); 
		return instantiateRepository(entry.getAppName(), providerName, act, metaData);
	}
	
	@SuppressWarnings("unchecked")
	private Repository instantiateRepository(String appName, String providerName, Activity act, Bundle metaData)
		throws CreateException
	{
		String clazzName = viableBundle.getString(providerName);
		if(clazzName != null)
		{
			try
			{
				ClassLoader cl = null; //Thread.currentThread().getContextClassLoader(); //Android doesn't use proper CL structure.
				if(cl == null)
					cl = getClass().getClassLoader();
				Class clazz = Class.forName(clazzName, true, cl);
				Constructor<Repository> c = clazz.getConstructor(String.class, Activity.class, Bundle.class); 
				return c.newInstance(appName, act, metaData);
			}
			catch (ClassNotFoundException e)
			{
				throw new CreateException("Concrete repository class '" + clazzName + "' is not in the classpath.  Is it misspelled?  Cannot instantiate repository for this provider.", e);
			}
			catch (NoSuchMethodException e)
			{
				throw new CreateException("Concrete repository class '" + clazzName + "' is expected to define a constructor of signature ConcreteClass(String, Activity, Bundle).  Cannot instantiate repository for this provider.", e);
			}
			catch (Exception e)
			{
				throw new CreateException("Error instantiating repository.", e);
			}
		}
		else
			throw new CreateException("No repository class definition, '" + providerName + "', in Viable's meta-data.  Cannot instantiate repository for this provider.");
	}
}
