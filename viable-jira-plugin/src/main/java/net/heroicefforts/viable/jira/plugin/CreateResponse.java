package net.heroicefforts.viable.jira.plugin;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.jcip.annotations.Immutable;

@Immutable
@SuppressWarnings("unused")
@XmlRootElement
public class CreateResponse
{
	@XmlElement
	private String errorMsg;
	
	@XmlElement
	private boolean created;
	
	@XmlElement
	private IssueDetail issue;
	
	
	private CreateResponse()
	{
		//empty
	}
	
	public CreateResponse(IssueDetail issue, boolean created)
	{
		this.issue = issue;
		this.created = created;
	}
	
	public CreateResponse(String errorMsg)
	{
		this.errorMsg = errorMsg;
	}
}
