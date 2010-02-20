package net.heroicefforts.viable.android.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	
	private String[] affectedDevices;
	private String[] affectedModels;
	private String[] affectedSDKs;
	
	public Issue()
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
		Log.d(TAG, "Generated issue JSON:  " + issueObj.toString(4));
		return issueObj;
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
	
}
