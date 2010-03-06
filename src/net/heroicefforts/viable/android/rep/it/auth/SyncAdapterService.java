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
package net.heroicefforts.viable.android.rep.it.auth;
 
import net.heroicefforts.viable.android.Config;
import android.accounts.Account;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
 
/**
 * The Android accounts activity crashes if a sync adapter is not included for all accounts.  This is a no-op sync adapter
 * to work around that bug.
 */
public class SyncAdapterService extends Service 
{
	private static final String TAG = "SyncAdapterService";
 
	private SyncAdapterImpl sSyncAdapter;

	public SyncAdapterService() {
		super();
	}
 
	private static class SyncAdapterImpl extends AbstractThreadedSyncAdapter {

		public SyncAdapterImpl(Context context) {
			super(context, false);
		}
 
		@Override
		public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
			if(Config.LOGD)
				Log.d(TAG, "perform noop sync called on account:  " + account.name + ":" + account.type + ".");
			if(GCLAccountAuthenticator.ACCT_TYPE.equals(account.type))
			{
				syncResult.stats.numUpdates++;
			}
		}
	}
 
	@Override
	public IBinder onBind(Intent intent) {
		if(Config.LOGV)
			Log.v(TAG, "Binding...");
		IBinder ret = null;
		ret = getSyncAdapter().getSyncAdapterBinder();
		return ret;
	}
 
	private SyncAdapterImpl getSyncAdapter() {
		if (sSyncAdapter == null)
			sSyncAdapter = new SyncAdapterImpl(this);
		return sSyncAdapter;
	}
 
}
 