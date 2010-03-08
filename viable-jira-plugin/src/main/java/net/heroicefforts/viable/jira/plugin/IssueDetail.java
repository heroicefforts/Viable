package net.heroicefforts.viable.jira.plugin;

import java.util.Collection;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.jcip.annotations.Immutable;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.project.version.Version;


@Immutable
@SuppressWarnings("unused")
@XmlRootElement
public class IssueDetail
{
	@XmlElement
	private String issueId;

	@XmlElement
	private String type;

	@XmlElement
	private String priority;

	@XmlElement
	private String state;

	@XmlElement
	private String appName;

	@XmlElement
	private String summary;
	
	@XmlElement
	private String description;
	
	@XmlElement
	private String[] affectedVersions;

	@XmlElement
	private String hash;
	
	@XmlElement
	private String stacktrace;
	
	@XmlElement
	private Date createDate;
		
	@XmlElement
	private Date modifiedDate;

	@XmlElement
	private Long votes;
	
	
	// This private constructor isn't used by any code, but JAXB requires any
	// representation class to have a no-args constructor.
	private IssueDetail()
	{
	}

	/**
	 * Initializes the representation's values to those in the specified {@code
	 * Project}.
	 * 
	 * @param project
	 *            the project to use for initialization
	 */
	public IssueDetail(Issue issue, String stacktrace, String hash)
	{
		this.issueId = issue.getKey();
		this.type = issue.getIssueTypeObject().getName().toLowerCase();
		if(issue.getPriorityObject() != null)
			this.priority = issue.getPriorityObject().getName().toLowerCase();
		if(issue.getStatusObject().getName() != null)
			this.state = issue.getStatusObject().getName().toLowerCase();
		else
			this.state = "open";
		this.appName = issue.getProjectObject().getName();
		this.summary = issue.getSummary();
		this.description = issue.getDescription();
		Collection<Version> versions = issue.getAffectedVersions();
		if(versions != null)
		{
			this.affectedVersions = new String[versions.size()];
			int i = 0;
			for(Version ver : versions)
				this.affectedVersions[i++] = ver.getName();
		}
		else
			this.affectedVersions = new String[0];
	
		this.stacktrace = stacktrace;
		this.hash = hash;
		
		this.votes = issue.getVotes();
		
		this.createDate = issue.getCreated();
		this.modifiedDate = issue.getUpdated();
		
	}

}