package net.heroicefforts.viable.android.rep.it.gdata;

import java.util.List;

import net.heroicefforts.viable.android.dao.Comment;

public class IssueCommentsFeed
{

	private List<Comment> comments;
	private long total;

	
	public IssueCommentsFeed(List<Comment> comments, int total)
	{
		this.comments = comments;
		this.total = total;
	}

	public List<Comment> getEntries()
	{
		return comments;
	}

	public long getTotalResults()
	{
		return total;
	}

}
