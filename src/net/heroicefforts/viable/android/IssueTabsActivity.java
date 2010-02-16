package net.heroicefforts.viable.android;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public class IssueTabsActivity extends TabActivity
{
	public static final String TAB_REPORT_ISSUE = "report_issue";
	public static final String TAB_ALL_ISSUES = "all_issues";
	public static final String TAB_MY_ISSUES = "my_issues";
	public static final String TAB_APPS = "apps";

	public static final String EXTRA_DEFAULT_TAB = "default_tab";
	
	
	@Override
	public void onCreate(Bundle savedStateInstance)
	{
		super.onCreate(savedStateInstance);
		
        TabHost mTabHost = getTabHost();         
        mTabHost.addTab(mTabHost.newTabSpec(TAB_APPS).setIndicator(getString(R.string.apps), null).setContent(new Intent(this, AppStatsActivity.class)));
        mTabHost.addTab(mTabHost.newTabSpec(TAB_MY_ISSUES).setIndicator(getString(R.string.my_issues), null).setContent(new Intent(this, LocalIssueListActivity.class)));
        mTabHost.addTab(mTabHost.newTabSpec(TAB_ALL_ISSUES).setIndicator(getString(R.string.all_issues), null).setContent(new Intent(this, RemoteIssueListActivity.class)));
        mTabHost.addTab(mTabHost.newTabSpec(TAB_REPORT_ISSUE).setIndicator(getString(R.string.report_issue), null).setContent(new Intent(this, BugReporterActivity.class)));        
        setDefaultTab(getIntent());
	}	

	private void setDefaultTab(Intent intent)
	{
		String defaultTab = intent.getStringExtra(EXTRA_DEFAULT_TAB);
		Log.d("IssueTabsActivity", "Received intent:  " + intent.toString());
        Log.d("IssueTabsActivity", "Setting tab to '" + defaultTab + "'.");
        if(defaultTab == null)
        	defaultTab = TAB_APPS;
        getTabHost().setCurrentTabByTag(defaultTab);
	}
}
 