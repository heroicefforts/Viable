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
package net.heroicefforts.viable.android;

import net.heroicefforts.viable.android.rep.RegEntry;
import net.heroicefforts.viable.android.rep.RepositoryRegistry;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * This class receives notifications when an application has been uninstalled.  Upon receipt, Viable will prompt
 * the user to supply an uninstall comment *if* the client application is found in the Viable registry.
 * 
 * @author jevans
 *
 */
public class UninstallReceiver extends BroadcastReceiver
{
	private static final String TAG = "UninstallReceiver";

	@Override
	public void onReceive(Context ctx, Intent uninstall)
	{
		Log.d(TAG, "Uninstall initiated:  " + uninstall);
		
		if(!uninstall.getBooleanExtra(Intent.EXTRA_REPLACING, false))
		{
			
			int uid = uninstall.getIntExtra(Intent.EXTRA_UID, -1);
			Log.d(TAG, "Uninstalling uid:  " + uid);
			if(uid > -1)
			{
				RepositoryRegistry registry = new RepositoryRegistry(ctx);
				RegEntry entry = registry.getEntryForUID(uid);
				if(entry != null)
				{
					Intent uninstReport = new UninstallIntent(entry);
					ctx.startActivity(uninstReport);
				}
			}
		}
	}

}
