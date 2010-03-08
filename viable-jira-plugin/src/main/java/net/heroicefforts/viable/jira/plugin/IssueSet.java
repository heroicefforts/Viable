package net.heroicefforts.viable.jira.plugin;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.jcip.annotations.Immutable;

@Immutable
@SuppressWarnings("unused")
@XmlRootElement
public class IssueSet
{
	@XmlElement
	private IssueDetail[] issues;

	@XmlElement
	private boolean more;
	
	
	// This private constructor isn't used by any code, but JAXB requires any
	// representation class to have a no-args constructor.
	private IssueSet()
	{
	}

	public IssueSet(List<IssueDetail> issues, boolean more)
	{
		this.issues = issues.toArray(new IssueDetail[issues.size()]);
		this.more = more;
	}
}
