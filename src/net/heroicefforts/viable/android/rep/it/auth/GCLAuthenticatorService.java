package net.heroicefforts.viable.android.rep.it.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Service wrapper for the authenticator.
 * 
 * @author jevans
 *
 */
public class GCLAuthenticatorService extends Service
{
	private GCLAccountAuthenticator authenticator;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		authenticator = new GCLAccountAuthenticator(this);
	}
	
	@Override
	public IBinder onBind(Intent arg0)
	{
		return authenticator.getIBinder();
	}

}
