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
package net.heroicefforts.viable.android.content;

import java.util.Arrays;
import java.util.Date;

import net.heroicefforts.viable.android.dao.Issue;
import android.content.ContentValues;
import android.database.Cursor;

/**
 * This class converts issues between object type and cursor type.
 * 
 * @author jevans
 *
 */
public class IssueContentAdapter
{
	private Issue issue;

	
	public IssueContentAdapter(Issue issue)
	{
		this.issue = issue;
	}
	
	public IssueContentAdapter(Cursor cursor)
	{
		issue = new Issue();
		issue.setIssueId(cursor.getString(cursor.getColumnIndex(Issues.ISSUE_ID)));
		issue.setType(cursor.getString(cursor.getColumnIndex(Issues.ISSUE_TYPE)));
		issue.setPriority(cursor.getString(cursor.getColumnIndex(Issues.ISSUE_PRIORITY)));
		issue.setState(cursor.getString(cursor.getColumnIndex(Issues.ISSUE_STATE)));
		issue.setAppName(cursor.getString(cursor.getColumnIndex(Issues.APP_NAME)));
		issue.setSummary(cursor.getString(cursor.getColumnIndex(Issues.SUMMARY)));
		issue.setDescription(cursor.getString(cursor.getColumnIndex(Issues.DESCRIPTION)));
		String versionString = cursor.getString(cursor.getColumnIndex(Issues.APP_VERSION));
		versionString = versionString.substring(1, versionString.length() - 1);		
		issue.setAffectedVersions(versionString.split("[ ]*,[ ]*"));
		issue.setHash(cursor.getString(cursor.getColumnIndex(Issues.HASH)));
		issue.setStacktrace(cursor.getString(cursor.getColumnIndex(Issues.STACKTRACE)));
		issue.setCreateDate(new Date(cursor.getLong(cursor.getColumnIndex(Issues.CREATED_DATE))));
		issue.setModifiedDate(new Date(cursor.getLong(cursor.getColumnIndex(Issues.MODIFIED_DATE))));		
	}
	
	/**
	 * Convert the issue state to content persistable values.
	 * @return content values for the adapter's issue state.
	 */
	public ContentValues toContentValues()
	{
		ContentValues values = new ContentValues();
		values.put(Issues.ISSUE_ID, issue.getIssueId());
		values.put(Issues.APP_NAME, issue.getAppName());
		values.put(Issues.ISSUE_TYPE, issue.getType());
		values.put(Issues.ISSUE_PRIORITY, issue.getPriority());
		values.put(Issues.ISSUE_STATE, issue.getState());
		values.put(Issues.STACKTRACE, issue.getStacktrace());
		values.put(Issues.SUMMARY, issue.getSummary());
		values.put(Issues.DESCRIPTION, issue.getDescription());
		values.put(Issues.HASH, issue.getHash());
		values.put(Issues.CREATED_DATE, issue.getCreateDate().getTime());
		values.put(Issues.MODIFIED_DATE, issue.getModifiedDate().getTime());
		values.put(Issues.APP_VERSION, Arrays.asList(issue.getAffectedVersions()).toString());
		
		return values;
	}
	
	/**
	 * Convert the issue state to an issue
	 * @return a populated issue
	 */
	public Issue toIssue()
	{
		return issue;		
	}
}
