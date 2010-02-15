package net.heroicefforts.viable.android;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class IssueTabsActivity extends TabActivity
{
	public void onCreate(Bundle savedStateInstance)
	{
		super.onCreate(savedStateInstance);
		
        TabHost mTabHost = getTabHost();         
        mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator(getString(R.string.apps), null).setContent(new Intent(this, AppStatsActivity.class)));
        mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator(getString(R.string.my_issues), null).setContent(new Intent(this, IssuesListActivity.class)));
        mTabHost.addTab(mTabHost.newTabSpec("tab3").setIndicator(getString(R.string.all_issues), null).setContent(new Intent(this, RemoteIssuesListActivity.class)));
        mTabHost.addTab(mTabHost.newTabSpec("tab4").setIndicator(getString(R.string.report_issue), null).setContent(new Intent(this, BugReporterActivity.class)));
        mTabHost.setCurrentTab(0);
	}
}
 