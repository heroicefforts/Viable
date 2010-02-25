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

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

/**
 * The main activity for Viable.  It is invoked by the launcher and {@link CrashReceiver}.  It handles the instantiation 
 * of all the subactivities.
 *  
 * @author jevans
 *
 */
public class IssueTabsActivity extends TabActivity implements NetworkDependentActivity
{
	public static final String TAB_APP_STATS = "apps";
	public static final String TAB_ALL_ISSUES = "all_issues";
	public static final String TAB_MY_ISSUES = "my_issues";
	public static final String TAB_REPORT_ISSUE = "report_issue";

	/**
	 * May be included with an intent to set the default tab view using one of the TAB_* constants.
	 */
	public static final String EXTRA_DEFAULT_TAB = "default_tab";

	private NetworkDependentDialog dialog;

	@Override
	public void onCreate(Bundle savedStateInstance)
	{
		super.onCreate(savedStateInstance);

		dialog = new NetworkDependentDialog(this);
        Eula.show(this);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();		
	}

	@Override
	public void finish()
	{
		super.finish();
		dialog.finish();
	}

	/**
	 * Set the default tab using the override EXTRA from the intent, if necessary.
	 * @param intent
	 */
	private void setDefaultTab(Intent intent)
	{
		String defaultTab = intent.getStringExtra(EXTRA_DEFAULT_TAB);
		Log.d("IssueTabsActivity", "Received intent:  " + intent.toString());
		Log.d("IssueTabsActivity", "Setting tab to '" + defaultTab + "'.");
		if (defaultTab == null)
			defaultTab = TAB_APP_STATS;
		getTabHost().setCurrentTabByTag(defaultTab);
	}

	/**
	 * Selects the "My Issues" tab.
	 */
	public void selectMyIssuesTab()
	{
		getTabHost().setCurrentTabByTag(TAB_MY_ISSUES);
	}

	/**
	 * Delays instantiation of the dependent tab activities until networking is available.  This eliminates network errors from
	 * bubbling up to the UI unnecessarily. 
	 */
	public void initOnNetworkAvailability()
	{
		TabHost mTabHost = getTabHost();
		mTabHost.addTab(mTabHost.newTabSpec(TAB_APP_STATS).setIndicator(getString(R.string.apps), getResources().getDrawable(R.drawable.apps)).setContent(
				new Intent(this, AppStatsActivity.class)));
		mTabHost.addTab(mTabHost.newTabSpec(TAB_MY_ISSUES).setIndicator(getString(R.string.my_issues), null)
				.setContent(new Intent(this, LocalIssueListActivity.class)));
		mTabHost.addTab(mTabHost.newTabSpec(TAB_ALL_ISSUES).setIndicator(getString(R.string.all_issues), null)
				.setContent(new Intent(this, RemoteIssueListActivity.class)));
		mTabHost.addTab(mTabHost.newTabSpec(TAB_REPORT_ISSUE).setIndicator(getString(R.string.report_issue), getResources().getDrawable(R.drawable.pen))
				.setContent(new Intent(this, BugReporterActivity.class)));
		setDefaultTab(getIntent());		
	}
	
	/**
	 * Closes this activity.
	 */
	public void giveUp()
	{
		finish();
	}	

}
