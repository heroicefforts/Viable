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
