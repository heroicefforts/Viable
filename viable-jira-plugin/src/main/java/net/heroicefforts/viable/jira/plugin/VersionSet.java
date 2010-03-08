package net.heroicefforts.viable.jira.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.jcip.annotations.Immutable;

import com.atlassian.jira.project.version.Version;


@Immutable
@SuppressWarnings("unused")
@XmlRootElement
public class VersionSet
{
	@XmlElement
	private VersionDetail[] versions;

	private VersionSet()
	{
		//JAXB required
	}
	
	public VersionSet(Collection<Version> versions)
	{
		List<VersionDetail> verList = new ArrayList<VersionDetail>();
		
		if(versions != null)
			for(Version version : versions)
				verList.add(new VersionDetail(version));
		Collections.sort(verList);
		this.versions = verList.toArray(new VersionDetail[versions.size()]);
	}

}
