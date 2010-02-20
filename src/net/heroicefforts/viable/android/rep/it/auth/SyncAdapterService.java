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
 
/**
 */
public class SyncAdapterService extends Service {
 
	private SyncAdapterImpl sSyncAdapter;

	public SyncAdapterService() {
		super();
	}
 
	private static class SyncAdapterImpl extends AbstractThreadedSyncAdapter {
		public SyncAdapterImpl(Context context) {
			super(context, true);
		}
 
		@Override
		public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
		}
	}
 
	@Override
	public IBinder onBind(Intent intent) {
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
 