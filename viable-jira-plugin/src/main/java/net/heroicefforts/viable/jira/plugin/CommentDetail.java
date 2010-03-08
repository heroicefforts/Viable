package net.heroicefforts.viable.jira.plugin;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.jcip.annotations.Immutable;

import com.atlassian.jira.issue.comments.Comment;

@Immutable
@SuppressWarnings("unused")
@XmlRootElement
public class CommentDetail
{
	@XmlElement	
	private Long id;
	
	@XmlElement
	private String body;
	
	@XmlElement
	private String author;

	@XmlElement
	private Date createDate;

	
	private CommentDetail()
	{
		//required by JAXB
	}
	
	public CommentDetail(Comment comment)
	{
		this.id = comment.getId();
		this.body = comment.getBody();
		this.author = comment.getAuthor();
		this.createDate = comment.getCreated();
	}

}
