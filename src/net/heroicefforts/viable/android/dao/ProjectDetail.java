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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Data class containing summary information for an issue project.
 * 
 * @author jevans
 *
 */
public class ProjectDetail
{
	private String name;
	private String description;
	private String lead;
	private String url;
	private long unfixedBugs;
	private long fixedBugs;
	private long unfixedImprovements;
	private long fixedImprovements;
	private long unfixedFeatures;
	private long fixedFeatures;
	private List<VersionDetail> versions = new ArrayList<VersionDetail>();

	public ProjectDetail()
	{
		//empty
	}
	
	/**
	 * Instantiate the project state based upon the JIRA JSON format.
	 * 
	 * @param obj JIRA JSON project object 
	 * @throws JSONException if there's an error parsing the JSON.
	 */
	public ProjectDetail(JSONObject obj)
		throws JSONException
	{
		this.name = obj.getString("name");
		this.description = obj.getString("description");
		this.lead = obj.getString("lead");
		this.url = obj.getString("url");
		this.unfixedBugs = obj.getLong("unfixedBugs");
		this.fixedBugs = obj.getLong("fixedBugs");
		this.unfixedImprovements = obj.getLong("unfixedImprovements");
		this.fixedImprovements = obj.getLong("fixedImprovements");
		this.unfixedFeatures = obj.getLong("unfixedFeatures");
		this.fixedFeatures = obj.getLong("fixedFeatures");
		
		if(obj.has("versions"))
		{
			JSONArray vers = obj.getJSONArray("versions");
			for(int i = 0; i < vers.length(); i++)
				versions.add(new VersionDetail(vers.getJSONObject(i)));
			Collections.sort(versions);
		}
	}

	/**
	 * Return the user friendly name of the project.
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
	 * Return a project summary.
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
	 * Return the project's lead developer / contact.
	 * @return
	 */
	public String getLead()
	{
		return lead;
	}

	public void setLead(String lead)
	{
		this.lead = lead;
	}

	/**
	 * Return the project's main site URL.
	 * @return
	 */
	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	/**
	 * Return the number of defects that have not been closed.
	 * @return
	 */
	public long getUnfixedBugs()
	{
		return unfixedBugs;
	}

	public void setUnfixedBugs(long unfixedBugs)
	{
		this.unfixedBugs = unfixedBugs;
	}

	/**
	 * Return the number of defects that have been closed.
	 * @return
	 */
	public long getFixedBugs()
	{
		return fixedBugs;
	}

	public void setFixedBugs(long fixedBugs)
	{
		this.fixedBugs = fixedBugs;
	}

	/**
	 * Return the number of improvements that have not been closed.
	 * @return
	 */
	public long getUnfixedImprovements()
	{
		return unfixedImprovements;
	}

	public void setUnfixedImprovements(long unfixedImprovements)
	{
		this.unfixedImprovements = unfixedImprovements;
	}

	/**
	 * Return the number of improvements that have been closed.
	 * @return
	 */
	public long getFixedImprovements()
	{
		return fixedImprovements;
	}

	public void setFixedImprovements(long fixedImprovements)
	{
		this.fixedImprovements = fixedImprovements;
	}

	/**
	 * Return the number of new features that have not been closed.
	 * @return
	 */
	public long getUnfixedFeatures()
	{
		return unfixedFeatures;
	}

	public void setUnfixedFeatures(long unfixedFeatures)
	{
		this.unfixedFeatures = unfixedFeatures;
	}

	/**
	 * Return the number of new features that have been closed.
	 * @return
	 */
	public long getFixedFeatures()
	{
		return fixedFeatures;
	}

	public void setFixedFeatures(long fixedFeatures)
	{
		this.fixedFeatures = fixedFeatures;
	}

	/**
	 * Return a list of releases:  past, present, and future.
	 * @return a non-null list of release data.
	 */
	public List<VersionDetail> getVersions()
	{
		return versions;
	}

	public void setVersions(List<VersionDetail> versions)
	{
		this.versions = versions;
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
		ProjectDetail other = (ProjectDetail) obj;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

}
