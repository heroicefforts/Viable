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

import java.util.ArrayList;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;

/**
 * This class handles the persistence of Viable client applications' configuration information.  This was required to fulfill
 * the uninstall reporting requirement, as Android removes package data before the uninstall event is triggered.
 * 
 * @author jevans
 *
 */
public class RepositoryRegistry
{
	private static final String TAG = "RepositoryRegistry";
	
	private RepoDB registry;
	private Context ctx;


	/**
	 * Create an instance of the repository.
	 * 
	 * @param ctx a Viable context.
	 */
	public RepositoryRegistry(Context ctx)
	{
		this.ctx = ctx;
		this.registry = new RepoDB(ctx);
	}
	
	/**
	 * Save or update the registry entry for the specified uid.  If the supplied uid is not a
	 * Viable client application, then no action is taken.
	 * 
	 * @param uid the system assigned uid
	 */
	public void modify(int uid)
	{
		ArrayList<RegEntry> entries = getEntries(uid);
		for(RegEntry entry : entries)
			modify(entry);
	}
	
	/**
	 * Return the entry for the specified Viable client application's uid
	 * @param uid the system assigned uid
	 * @return the client app's entry, or null if the uid does not exist or represents an app
	 * that is not a Viable client application. 
	 */
	public RegEntry getEntryForUID(int uid)
	{
		return registry.getRegEntry(uid);
	}

	/**
	 * Delete any entry related to the specified uid, if it exists.
	 * @param uid the system assigned uid
	 */
	public void deleteForUID(int uid)
	{
		registry.delete(uid);
	}

	/**
	 * Retrieves an entry for the specified application label.
	 * @param appName the client application's label.
	 * @return the entry or null if no entry has been registered for the application. 
	 */
	public RegEntry getEntryForApp(String appName)
	{
		Integer uid = registry.getUIDByName(appName);
		if(uid != null)
			return registry.getRegEntry(uid);
		else
			return null;
	}
	
	private void modify(RegEntry entry)
		throws SQLException
	{
		if(isViableApp(entry))
		{
			registry.delete(entry.getUID());
			registry.save(entry);
		}
	}
		
	private boolean isViableApp(RegEntry entry)
	{
		return entry.getParams() != null && entry.getParams().getString("viable-provider") != null;
	}
	
	private ArrayList<RegEntry> getEntries(int uid)
	{
		ArrayList<RegEntry> apps = new ArrayList<RegEntry>();
		
		if(uid > -1)
		{
			PackageManager pkgMgr = ctx.getPackageManager();
			String[] pkgs = pkgMgr.getPackagesForUid(uid);
			if(pkgs != null)
			{
				for(String pkg : pkgs)
				{
					try
					{
						ApplicationInfo info = pkgMgr.getApplicationInfo(pkg, PackageManager.GET_META_DATA);
						String appName = pkgMgr.getApplicationLabel(info).toString();
						PackageInfo pkgInfo = pkgMgr.getPackageInfo(pkg, 0);
						String versionName = pkgInfo.versionName;
						apps.add(new RegEntry(uid, appName, versionName, info.metaData));
					}
					catch (NameNotFoundException e)
					{
						Log.e(TAG, "There was no application info defined for package '" + pkg + "'.");
					}
				}
			}
			else
				Log.e(TAG, "There was no package defined for newly installed application uid '" + uid + "'.");
		}
		else
			Log.e(TAG, "No uid was supplied with the installation extent!");
		
		return apps;
	}
	
	
	private class RepoDB extends SQLiteOpenHelper
	{
		private static final String DATABASE_NAME = "ViableRegistry";
		private static final int DATABASE_VERSION = 1;
		private static final String REGISTRY_TABLE_NAME = "viable_registry";
		private static final String SQL_INSERT = "insert into " + REGISTRY_TABLE_NAME + "(uid, key, value, created_date) values (?,?,?,?)";


		public RepoDB(Context ctx)
		{
			super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db)
		{
            db.execSQL("CREATE TABLE " + REGISTRY_TABLE_NAME + " ("
                    + "_id INTEGER PRIMARY KEY,"
                    + "uid INTEGER,"
                    + "key TEXT NOT NULL ON CONFLICT ROLLBACK,"
                    + "value TEXT NOT NULL ON CONFLICT ROLLBACK,"
                    + "created_date INTEGER"
                    + ");"); 
		}
		
		public void save(RegEntry entry)
			throws SQLException
		{
			Bundle bundle = entry.getParams();

			if(bundle != null)
			{
				SQLiteDatabase db = getWritableDatabase();
				db.beginTransaction();
				try {
					long now = System.currentTimeMillis();
					long uid = (long) entry.getUID();
					
					db.execSQL(SQL_INSERT, new Object[] { uid, "app-name", entry.getAppName(), now });
					db.execSQL(SQL_INSERT, new Object[] { uid, "app-version-name", entry.getVersionName(), now });
					for(String key : bundle.keySet())
					{
						if(key.startsWith("viable"))
						{
							String value = bundle.getString(key);
							db.execSQL(SQL_INSERT, new Object[] { uid, key, value, now });
						}
					}
					db.setTransactionSuccessful();
				}
				finally {
					db.endTransaction();

					if(db != null)
						db.close();
				}
			}
		}
		
		public void delete(int uid)
			throws SQLException
		{
			SQLiteDatabase db = getWritableDatabase();
			db.beginTransaction();
			try {
				db.execSQL("delete from " + REGISTRY_TABLE_NAME + " where uid = ?", 
					new Object[] { new Integer(uid).longValue() });
				db.setTransactionSuccessful();
			}
			finally {
				db.endTransaction();

				if(db != null)
					db.close();
			}
		}
			
		public RegEntry getRegEntry(int uid)
		{
			SQLiteDatabase db = getReadableDatabase();
			Cursor c = null;
			String appName = null;
			String versionName = null;
			try {
				Bundle values = new Bundle();
				c = db.query(REGISTRY_TABLE_NAME, new String[] { "key", "value" }, "uid = ?", new String[] { String.valueOf(uid) }, null, null, null);
				while(c.moveToNext())
				{
					String key = c.getString(c.getColumnIndex("key"));
					String value = c.getString(c.getColumnIndex("value"));
					
					if("app-name".equals(key))
						appName = value;
					else if("app-version-name".equals(key))
						versionName = value;
					else
						values.putString(key, value);
				}

				if(appName != null && versionName != null && values.keySet().size() > 0)
				{
					return new RegEntry(uid, appName, versionName, values);
				}
				else
					return null;
			}
			finally
			{
				if(c != null)
					c.close();
				if(db != null)
					db.close();
			}			
		}

		public Integer getUIDByName(String appName)
		{
			SQLiteDatabase db = getReadableDatabase();
			Cursor c = null;
			try {
				c = db.query(REGISTRY_TABLE_NAME, new String[] { "uid" }, "key = ? and value = ?", 
					new String[] { "app-name", appName }, null, null, null);
				if(c.moveToNext())
					return c.getInt(c.getColumnIndex("uid"));
				else
					return null;
			}
			finally
			{
				if(c != null)
					c.close();
				if(db != null)
					db.close();
			}			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			//empty for now
		}

	}

}
