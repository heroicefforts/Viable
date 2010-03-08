package net.heroicefforts.viable.jira.plugin;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.jcip.annotations.Immutable;

import com.atlassian.jira.project.version.Version;

@Immutable
@SuppressWarnings("unused")
@XmlRootElement
public class VersionDetail implements Comparable<VersionDetail>
{
	@XmlElement
	private String description;

	@XmlElement
	private String name;

	@XmlElement
	private Date releaseDate;


	private VersionDetail()
	{
		//JAXB required
	}
	
	public VersionDetail(Version version)
	{
		this.description = version.getDescription();
		this.name = version.getName();
		this.releaseDate = version.getReleaseDate();
	}

	public int compareTo(VersionDetail version)
	{
		if(releaseDate != null && version.releaseDate != null)
			return releaseDate.compareTo(version.releaseDate);
		else
			return this.name.compareTo(version.name);
	}

}
