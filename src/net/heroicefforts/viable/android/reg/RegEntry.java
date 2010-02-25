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
package net.heroicefforts.viable.android.reg;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.HashMap;

import android.os.Bundle;

/**
 * This class retains the configuration information required to instantiate a repository instance for a particular Viable
 * client application.  Data is acquired from the package manager and persisted to a database.
 *  
 * @author jevans
 *
 */
public class RegEntry implements Serializable, Externalizable
{
	private static final long serialVersionUID = 1L;
	
	private Bundle params;
	private String versionName;
	private String appName;

	private int uid;


	/**
	 * Constructor created to meet Serialization demands.  It is not intended for developer use.
	 */
	public RegEntry()
	{
		//for serialization
	}
	
	/**
	 * Constructs an entry.
	 * @param uid the application uid
	 * @param appName the application's label
	 * @param versionName the application's version name.
	 * @param params the viable specific parameters.
	 */
	public RegEntry(int uid, String appName, String versionName, Bundle params)
	{
		this.uid = uid;
		this.appName = appName;
		this.versionName = versionName;
		this.params = params;
	}

	/**
	 * Return viable specific configuration parameters.
	 * @return
	 */
	public Bundle getParams()
	{
		return params;
	}

	/**
	 * Return the application's version name.
	 * @return
	 */
	public String getVersionName()
	{
		return versionName;
	}

	/**
	 * Return the application's label.
	 * @return
	 */
	public String getAppName()
	{
		return appName;
	}


	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appName == null) ? 0 : appName.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RegEntry other = (RegEntry) obj;
		if (appName == null)
		{
			if (other.appName != null)
				return false;
		}
		else if (!appName.equals(other.appName))
			return false;
		return true;
	}

	/**
	 * Return the applications system id.
	 * @return
	 */
	public int getUID()
	{
		return uid;
	}


	@SuppressWarnings("unchecked")
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		this.uid = in.readInt();
		this.appName = (String) in.readObject();
		this.versionName = (String) in.readObject();
		HashMap<String, String> map = (HashMap<String, String>) in.readObject();
		this.params = new Bundle(map.size());
		for(String key : map.keySet())
			params.putString(key, map.get(key));
	}


	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeInt(uid);
		out.writeObject(appName);
		out.writeObject(versionName);
		HashMap<String, String> map = new HashMap<String, String>(params.size());
		for(String key : params.keySet())
			map.put(key, params.getString(key));
		out.writeObject(map);
	}
}
