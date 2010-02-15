package net.heroicefforts.viable.android;

import java.util.List;

public class CommentSet
{
	private List<Comment> comments;
	private boolean more;

	
	public CommentSet(List<Comment> comments, boolean more)
	{
		super();
		this.comments = comments;
		this.more = more;
	}

	public List<Comment> getComments()
	{
		return comments;
	}

	public boolean isMore()
	{
		return more;
	}
	
	
}
