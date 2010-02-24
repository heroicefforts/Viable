package net.heroicefforts.viable.android.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Data class containing information for a project release.
 * 
 * @author jevans
 *
 */
public class VersionDetail implements Comparable<VersionDetail>
{
	private static final String TAG = "VersionDetail";
	
	private String name;
	private String description;
	private Date releaseDate;

	public VersionDetail(String name)
	{
		this.name = name;
	}
		
	/**
	 * Instantiate the release state based upon the JIRA JSON format.
	 * 
	 * @param obj JIRA JSON release object 
	 * @throws JSONException if there's an error parsing the JSON.
	 */
	public VersionDetail(JSONObject obj)
		throws JSONException
	{
		this.name = obj.getString("name");
		this.description = obj.getString("description");

		if(obj.has("releaseDate"))
		{
			try
			{
				SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				fmt.setLenient(true);
				this.releaseDate = fmt.parse(obj.getString("releaseDate"));
			}
			catch (ParseException e)
			{
				Log.e(TAG, "Error parsing JSON dates.", e);
				throw new JSONException("Error parsing JSON dates.");
			}
		}
	}

	/**
	 * The version name of the release e.g. 1.0.0.
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * A summary of the release.
	 * @return
	 */
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * The date that the release was (or will be) made public.
	 * @return
	 */
	public Date getReleaseDate()
	{
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate)
	{
		this.releaseDate = releaseDate;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		VersionDetail other = (VersionDetail) obj;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

	public int compareTo(VersionDetail version)
	{
		if(releaseDate != null && version.releaseDate != null)
			return releaseDate.compareTo(version.releaseDate);
		else
			return this.name.compareTo(version.name);
	}	
}
