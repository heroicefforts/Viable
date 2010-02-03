package net.heroicefforts.viable.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CrashReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		Intent mine = new Intent(intent);
		mine.setClass(context, BugReporterActivity.class);
		mine.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(mine); 
	}

}
