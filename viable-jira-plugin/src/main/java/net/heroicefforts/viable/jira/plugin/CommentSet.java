package net.heroicefforts.viable.jira.plugin;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.jcip.annotations.Immutable;

@Immutable
@SuppressWarnings("unused")
@XmlRootElement
public class CommentSet
{
	@XmlElement
	private CommentDetail[] comments;

	@XmlElement
	private boolean more;

	
	private CommentSet()
	{
		//required by JAXB
	}
	
	public CommentSet(List<CommentDetail> details, boolean more)
	{
		this.comments = details.toArray(new CommentDetail[details.size()]);
		this.more = more;
	}

}
