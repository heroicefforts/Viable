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
package net.heroicefforts.viable.android.dist;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import android.content.Intent;

/**
 * Decorates an intent with the necessary information so that it is processed correctly by {@link net.heroicefforts.viable.android.CrashReceiver}.
 *
 * @author jevans
 *
 */
public class BugReportIntent extends Intent
{
	/**
	 * Creates a defect intent.
	 * 
	 * @param appName the label of the application that crashed. 
	 * @param stacktrace the stacktrace of the exception.
	 */
	public BugReportIntent(String appName, Throwable stacktrace)
	{
		super("android.intent.action.MAIN");
		addCategory("android.intent.category.BUGREPORT_JIRA");
		putExtra("stacktrace", toString(stacktrace));
		putExtra("app_name", appName);
	}
	
	/**
	 * Used internally by Viable.
	 * 
	 * @param intent
	 */
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

	/**
	 * Returns true if the intent has been properly configured.
	 * 
	 * @return
	 */
	public boolean isValid()
	{
		return getAppName() != null;
	}
	
}
