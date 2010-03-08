package net.heroicefforts.viable.crashy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class CrashingService extends Service
{

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)	
	{
		throw new RuntimeException("Simulated service exception.  [" + System.currentTimeMillis() + "]");
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

}
