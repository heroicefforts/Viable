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

import net.heroicefforts.viable.android.Config;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * This listener receives application (re)installation events.  If the application is
 * a Viable client application, then their Viable configuration is added to the registry.
 * 
 * @author jevans
 *
 */
public class InstallReceiver extends BroadcastReceiver
{
	private static final String TAG = "InstallReceiver";

	@Override
	public void onReceive(Context context, Intent intent)
	{
		if(Config.LOGV)
			Log.v(TAG, "Received installation event notification.");
		int uid = intent.getIntExtra(Intent.EXTRA_UID, -1);
		RepositoryRegistry registry = new RepositoryRegistry(context);
		registry.modify(uid);						
	}

}
