package net.heroicefforts.viable.android.dist;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import android.content.Intent;

public class BugReportIntent extends Intent
{
	public BugReportIntent(String appName, Throwable stacktrace)
	{
		super("android.intent.action.MAIN");
		addCategory("android.intent.category.BUGREPORT_JIRA");
		putExtra("stacktrace", toString(stacktrace));
		putExtra("app_name", appName);
	}
	
	public BugReportIntent(Intent intent)
	{
		super(intent);
	}

	public String getStacktrace()
	{
		return getStringExtra("stacktrace");
	}
	
	public String getAppName()
	{
		return getStringExtra("app_name");
	}
	
	private static final String toString(Throwable t)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		t.printStackTrace(ps);
		ps.flush();
		return baos.toString(); 
	}

	public boolean isValid()
	{
		return getAppName() != null;
	}
	
}
