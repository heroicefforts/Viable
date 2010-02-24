package net.heroicefforts.viable.android.rep.it.auth;
 
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
			Log.d(TAG, "perform noop sync called on account:  " + account.name + ":" + account.type + ".");
			if(GCLAccountAuthenticator.ACCT_TYPE.equals(account.type))
			{
				syncResult.stats.numUpdates++;
			}
		}
	}
 
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "Binding...");
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
 