package net.heroicefforts.viable.android;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
		}
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getLead()
	{
		return lead;
	}

	public void setLead(String lead)
	{
		this.lead = lead;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public long getUnfixedBugs()
	{
		return unfixedBugs;
	}

	public void setUnfixedBugs(long unfixedBugs)
	{
		this.unfixedBugs = unfixedBugs;
	}

	public long getFixedBugs()
	{
		return fixedBugs;
	}

	public void setFixedBugs(long fixedBugs)
	{
		this.fixedBugs = fixedBugs;
	}

	public long getUnfixedImprovements()
	{
		return unfixedImprovements;
	}

	public void setUnfixedImprovements(long unfixedImprovements)
	{
		this.unfixedImprovements = unfixedImprovements;
	}

	public long getFixedImprovements()
	{
		return fixedImprovements;
	}

	public void setFixedImprovements(long fixedImprovements)
	{
		this.fixedImprovements = fixedImprovements;
	}

	public long getUnfixedFeatures()
	{
		return unfixedFeatures;
	}

	public void setUnfixedFeatures(long unfixedFeatures)
	{
		this.unfixedFeatures = unfixedFeatures;
	}

	public long getFixedFeatures()
	{
		return fixedFeatures;
	}

	public void setFixedFeatures(long fixedFeatures)
	{
		this.fixedFeatures = fixedFeatures;
	}

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
