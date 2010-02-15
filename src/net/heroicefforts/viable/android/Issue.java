package net.heroicefforts.viable.android;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

public class Issue
{
	private static final String TAG = "Issue";

	private String issueId;
	private String type;
	private String priority;
	private String state;
	protected String appName;
	private String summary;
	private String description;
	protected String[] affectedVersions;
	private String hash;
	protected String stacktrace;
	private Date createDate;
	private Date modifiedDate;	
	
	protected Issue()
	{
		//empty
	}
	
	public Issue(String json)
		throws JSONException
	{
		Log.d(TAG, "Parsing issue JSON:  " + json);		
		JSONObject issueObj = new JSONObject(json);
		issueId = issueObj.getString("issueId");
		type = issueObj.getString("type");
		if(issueObj.has("priority"))
			priority = issueObj.getString("priority");
		state = issueObj.getString("state");
		appName = issueObj.getString("appName");
		summary = issueObj.getString("summary");
		if(issueObj.has("description"))
			description = issueObj.getString("description");
		if(issueObj.has("affectedVersions"))
		{
			JSONArray affVers = issueObj.getJSONArray("affectedVersions");
			affectedVersions = new String[affVers.length()];
			for(int i = 0; i < affVers.length(); i++)
				affectedVersions[i] = affVers.getString(i);
		}
		if(issueObj.has("hash"))
			hash = issueObj.getString("hash");
		if(issueObj.has("stacktrace"))
			stacktrace = issueObj.getString("stacktrace");
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		fmt.setLenient(true);
		try
		{
			createDate = fmt.parse(issueObj.getString("createDate"));
			modifiedDate = fmt.parse(issueObj.getString("modifiedDate"));
		}
		catch (ParseException e)
		{
			Log.e(TAG, "Error parsing JSON dates.", e);
			throw new JSONException("Error parsing JSON dates.");
		}
	}

	public Issue(Cursor cursor)
	{
		this.issueId = cursor.getString(cursor.getColumnIndex(Issues.ISSUE_ID));
		this.type = cursor.getString(cursor.getColumnIndex(Issues.ISSUE_TYPE));
		this.priority = cursor.getString(cursor.getColumnIndex(Issues.ISSUE_PRIORITY));
		this.state = cursor.getString(cursor.getColumnIndex(Issues.ISSUE_STATE));
		this.appName = cursor.getString(cursor.getColumnIndex(Issues.APP_NAME));
		this.summary = cursor.getString(cursor.getColumnIndex(Issues.SUMMARY));
		this.description = cursor.getString(cursor.getColumnIndex(Issues.DESCRIPTION));
		String versionString = cursor.getString(cursor.getColumnIndex(Issues.APP_VERSION));
		versionString = versionString.substring(1, versionString.length() - 1);		
		this.affectedVersions = versionString.split("[ ]*,[ ]*");
		this.hash = cursor.getString(cursor.getColumnIndex(Issues.HASH));
		this.stacktrace = cursor.getString(cursor.getColumnIndex(Issues.STACKTRACE));
		this.createDate = new Date(cursor.getLong(cursor.getColumnIndex(Issues.CREATED_DATE)));
		this.modifiedDate = new Date(cursor.getLong(cursor.getColumnIndex(Issues.MODIFIED_DATE)));
	}

	public String getIssueId()
	{
		return issueId;
	}

	public String getType()
	{
		return type;
	}

	public String getPriority()
	{
		return priority;
	}

	public String getState()
	{
		return state;
	}

	public String getAppName()
	{
		return appName;
	}

	public String getSummary()
	{
		return summary;
	}

	public String getDescription()
	{
		return description;
	}

	public String[] getAffectedVersions()
	{
		if(affectedVersions != null)
			return affectedVersions;
		else
			return new String[0];
	}

	public String getHash()
	{
		return hash;
	}

	public String getStacktrace()
	{
		return stacktrace;
	}

	public Date getCreateDate()
	{
		return createDate;
	}

	public Date getModifiedDate()
	{
		return modifiedDate;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appName == null) ? 0 : appName.hashCode());
		result = prime * result + ((issueId == null) ? 0 : issueId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Issue other = (Issue) obj;
		if (appName == null)
		{
			if (other.appName != null)
				return false;
		}
		else if (!appName.equals(other.appName))
			return false;
		if (issueId == null)
		{
			if (other.issueId != null)
				return false;
		}
		else if (!issueId.equals(other.issueId))
			return false;
		return true;
	}

	public void setAppName(String appName)
	{
		this.appName = appName;
	}

	public void setSummary(String summary)
	{
		this.summary = summary;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public void setPriority(String priority)
	{
		this.priority = priority;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public void setIssueId(String issueId)
	{
		this.issueId = issueId;
	}
	
	public void setState(IssueState state)
	{
		this.type = state.getType();
		this.priority = state.getPriority();
	}
	
	public ContentValues getContentValues()
	{
		ContentValues values = new ContentValues();
		values.put(Issues.ISSUE_ID, issueId);
		values.put(Issues.APP_NAME, appName);
		values.put(Issues.ISSUE_TYPE, type);
		values.put(Issues.ISSUE_PRIORITY, priority);
		values.put(Issues.ISSUE_STATE, state);
		values.put(Issues.STACKTRACE, stacktrace);
		values.put(Issues.SUMMARY, summary);
		values.put(Issues.DESCRIPTION, description);
		values.put(Issues.HASH, hash);
		values.put(Issues.CREATED_DATE, createDate.getTime());
		values.put(Issues.MODIFIED_DATE, modifiedDate.getTime());
		values.put(Issues.APP_VERSION, Arrays.asList(getAffectedVersions()).toString());
		
		return values;
	}

	public void copy(Issue newIssue)
	{
		this.issueId = newIssue.issueId;
		this.type = newIssue.type;
		this.priority = newIssue.priority;
		this.state = newIssue.state;
		this.appName = newIssue.appName;
		this.summary = newIssue.summary;
		this.description = newIssue.description;
		if(newIssue.affectedVersions != null)
			this.affectedVersions = newIssue.affectedVersions;
		this.hash = newIssue.hash;
		this.stacktrace = newIssue.stacktrace;
		this.createDate = newIssue.createDate;
		this.modifiedDate = newIssue.modifiedDate;
	}

	public void setAffectedVersions(String[] affectedVersions)
	{
		this.affectedVersions = affectedVersions;
	}
	
}
