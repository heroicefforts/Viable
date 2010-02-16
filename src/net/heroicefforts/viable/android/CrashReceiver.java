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
		mine.setClass(context, IssueTabsActivity.class);
		mine.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mine.putExtra(IssueTabsActivity.EXTRA_DEFAULT_TAB, IssueTabsActivity.TAB_REPORT_ISSUE);
		context.startActivity(mine);
	}

}
 