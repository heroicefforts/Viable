package net.heroicefforts.viable.jira.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.atlassian.jira.project.version.Version;

import net.jcip.annotations.Immutable;

@Immutable
@SuppressWarnings("unused")
@XmlRootElement
public class ProjectDetail
{
	@XmlElement
	private VersionDetail[] versions;

	@XmlElement
	private String name;

	@XmlElement
	private String description;

	@XmlElement
	private String lead;

	@XmlElement
	private String url;

	@XmlElement
	private long unfixedBugs;

	@XmlElement
	private long fixedBugs;

	@XmlElement
	private long unfixedImprovements;

	@XmlElement
	private long fixedImprovements;

	@XmlElement
	private long unfixedFeatures;

	@XmlElement
	private long fixedFeatures;


	private ProjectDetail()
	{
		//JAXB required
	}
	
	public ProjectDetail(String name, String desc, String leadName, String url, Collection<Version> versions)
	{
		this.name = name;
		this.description = desc;
		this.lead = leadName;
		this.url = url;		
		setVersions(versions);
	}

	private void setVersions(Collection<Version> versions)
	{
		List<VersionDetail> verList = new ArrayList<VersionDetail>();
		
		if(versions != null)
			for(Version version : versions)
				verList.add(new VersionDetail(version));
		
		this.versions = verList.toArray(new VersionDetail[versions.size()]);
	}

	public void addBugCounts(long unfixedBugs, long fixedBugs)
	{
		this.unfixedBugs = unfixedBugs;
		this.fixedBugs = fixedBugs;
	}

	public void addImprovementCounts(long unfixedImprovements, long fixedImprovements)
	{
		this.unfixedImprovements = unfixedImprovements;
		this.fixedImprovements = fixedImprovements;
	}

	public void addFeatures(long unfixedFeatures, long fixedFeatures)
	{
		this.unfixedFeatures = unfixedFeatures;
		this.fixedFeatures = fixedFeatures;
	}
	
}
