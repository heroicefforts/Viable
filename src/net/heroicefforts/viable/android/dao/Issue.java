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
package net.heroicefforts.viable.android.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Data class for an issue.
 * 
 * @author jevans
 *
 */
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
	private boolean voted;
	private long votes;
	private Date createDate;
	private Date modifiedDate;	
	
	private String[] affectedDevices;
	private String[] affectedModels;
	private String[] affectedSDKs;
	
	
	public Issue()
	{
		//empty
	}
	
	/**
	 * Instantiate the issue state based upon the JIRA JSON format.
	 * 
	 * @param obj JIRA JSON issue object 
	 * @throws JSONException if there's an error parsing the JSON.
	 */
	public Issue(String json)
		throws JSONException
	{
		Log.v(TAG, "Parsing issue JSON:  " + json);		
		JSONObject issueObj = new JSONObject(json);
		issueId = issueObj.getString("issueId");
		type = issueObj.getString("type");
		if(issueObj.has("priority"))
			priority = issueObj.getString("priority");
		state = issueObj.getString("state");
		appName = issueObj.getString("appName");
		summary = issueObj.getString("summary");
		if(issueObj.has("votes"))
			votes = issueObj.getLong("votes");
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
	
	/**
	 * Return the JIRA JSON representation of the Issue's state.
	 * @return
	 * @throws JSONException
	 */
	public JSONObject getJSON()
		throws JSONException
	{
		JSONObject issueObj = new JSONObject();
		issueObj.put("issueId", issueId);
		issueObj.put("type", type);
		issueObj.put("priority", priority);
		issueObj.put("state", state);
		issueObj.put("appName", appName);
		issueObj.put("summary", summary);
		issueObj.put("description", description);
		if(affectedVersions != null && affectedVersions.length > 0)
		{
			JSONArray affVers = new JSONArray();
			for(String version : affectedVersions)
				affVers.put(version);
			issueObj.put("affectedVersions", affVers);
		}
		issueObj.put("hash", hash);
		issueObj.put("stacktrace", stacktrace);
//		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//		fmt.setLenient(true);
//		try
//		{
//			createDate = fmt.parse(issueObj.put("createDate"));
//			modifiedDate = fmt.parse(issueObj.put("modifiedDate"));
//		}
//		catch (ParseException e)
//		{
//			Log.e(TAG, "Error parsing JSON dates.", e);
//			throw new JSONException("Error parsing JSON dates.");
//		}
		Log.v(TAG, "Generated issue JSON:  " + issueObj.toString(4));
		return issueObj;
	}
	
	/**
	 * Return the unique issue id.
	 * @return
	 */
	public String getIssueId()
	{
		return issueId;
	}

	/**
	 * Return the type of issue e.g. defect.
	 * @return
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Return the priority of the issue.
	 * @return
	 */
	public String getPriority()
	{
		return priority;
	}

	/**
	 * Return the state of the issue e.g. open
	 * @return
	 */
	public String getState()
	{
		return state;
	}

	/**
	 * Return the label of the owning application.
	 * @return
	 */
	public String getAppName()
	{
		return appName;
	}

	/**
	 * Return the issue title.
	 * @return
	 */
	public String getSummary()
	{
		return summary;
	}

	/**
	 * Return the issue content.
	 * @return
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Return the application versions known to suffer from this issue.
	 * @return
	 */
	public String[] getAffectedVersions()
	{
		if(affectedVersions != null)
			return affectedVersions;
		else
			return new String[0];
	}

	/**
	 * Return the unique checksum for the issue.
	 * @return
	 */
	public String getHash()
	{
		return hash;
	}

	/**
	 * Return the defect's stacktrace.
	 * @return
	 */
	public String getStacktrace()
	{
		return stacktrace;
	}

	/**
	 * Return the date the issue was created.
	 * @return
	 */
	public Date getCreateDate()
	{
		return createDate;
	}

	/**
	 * Return the date that the issue was last modified.
	 * @return
	 */
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
	
	/**
	 * Copy the state from the supplied issue
	 * @param newIssue the source issue.
	 */
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

	public void setHash(String hash)
	{
		this.hash = hash;
	}

	public void setStacktrace(String stacktrace)
	{
		this.stacktrace = stacktrace;
	}

	public void setCreateDate(Date createDate)
	{
		this.createDate = createDate;
	}

	public void setModifiedDate(Date modifiedDate)
	{
		this.modifiedDate = modifiedDate;
	}

	/**
	 * Return the devices known to suffer from this issue.
	 * @return
	 */
	public String[] getAffectedDevices()
	{
		if(affectedDevices != null)
			return affectedDevices;
		else
			return new String[0];
	}
	
	public void setAffectedDevices(String[] affectedDevices)
	{
		this.affectedDevices = affectedDevices;
	}

	/**
	 * Return the device models known to suffer from this issue.
	 * @return
	 */
	public String[] getAffectedModels()
	{
		if(affectedModels != null)
			return affectedModels;
		else
			return new String[0];
	}

	public void setAffectedModels(String[] affectedModels)
	{
		this.affectedModels = affectedModels;
	}

	/**
	 * Return the Android SDK versions known to suffer from this issue.
	 * @return
	 */
	public String[] getAffectedSDKs()
	{
		if(affectedSDKs != null)
			return affectedSDKs;
		else
			return new String[0];
	}

	public void setAffectedSDKs(String[] affectedSDKs)
	{
		this.affectedSDKs = affectedSDKs;
	}

	public long getVotes()
	{
		return votes;
	}

	public void setVotes(long votes)
	{
		this.votes = votes;
	}

	public boolean isVoted()
	{
		return voted;
	}

	public void setVoted(boolean voted)
	{
		this.voted = voted;
	}
	
}
