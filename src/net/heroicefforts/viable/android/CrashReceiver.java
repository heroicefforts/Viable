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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This class receives crash report intentions from other applications and invokes Viable, if necessary.
 * 
 * @see net.heroicefforts.viable.android.dist.BugReportIntent
 * @see net.heroicefforts.viable.android.dist.ViableExceptionHandler
 * 
 * @author jevans
 *
 */
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
 