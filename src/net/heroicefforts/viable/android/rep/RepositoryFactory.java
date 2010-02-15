package net.heroicefforts.viable.android.rep;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;

public class RepositoryFactory
{
	private static final String TAG = "RepositoryFactory";
	private Map<String, Bundle> appBundles = new TreeMap<String, Bundle>();
	private Map<String, String> appVersion = new TreeMap<String, String>();
	private Bundle viableBundle;
	
	public RepositoryFactory(Context ctx)
	{
		PackageManager pkgMgr = ctx.getPackageManager();
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
					appVersion .put(label, info.versionName);
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
	}
	
	public Set<String> getApplicationNames()
	{
		return Collections.unmodifiableSet(appBundles.keySet());
	}
	
	public String getApplicationVersion(String appName)
	{
		return appVersion.get(appName);
	}
	
	public Repository getRepository(String appName)
		throws CreateException
	{
		Bundle metaData = appBundles.get(appName);
		if(metaData != null)
		{
			String providerName = metaData.getString("viable-provider");
			return instantiateRepository(providerName, metaData);
		}
				
		Log.e(TAG, "No '" + "viable-provider" + "' meta-data field defined for application '" + appName + ".  Repository cannot be constructed.");
		return null;
	}

	@SuppressWarnings("unchecked")
	private Repository instantiateRepository(String providerName, Bundle metaData)
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
				Constructor<Repository> c = clazz.getConstructor(Bundle.class); 
				return c.newInstance(metaData);
			}
			catch (ClassNotFoundException e)
			{
				throw new CreateException("Concrete repository class '" + clazzName + "' is not in the classpath.  Is it misspelled?  Cannot instantiate repository for this provider.", e);
			}
			catch (NoSuchMethodException e)
			{
				throw new CreateException("Concrete repository class '" + clazzName + "' is expected to define a constructor of signature ConcreteClass(Bundle).  Cannot instantiate repository for this provider.", e);
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
