package net.heroicefforts.viable.android.rep.it.gdata;

import java.util.List;

import net.heroicefforts.viable.android.dao.Comment;

/**
 * A wrapper around comments.
 * 
 * @author jevans
 *
 */
public class IssueCommentsFeed
{

	private List<Comment> comments;
	private long total;

	
	public IssueCommentsFeed(List<Comment> comments, int total)
	{
		this.comments = comments;
		this.total = total;
	}

	/**
	 * A page of comments.
	 * @return
	 */
	public List<Comment> getEntries()
	{
		return comments;
	}

	/**
	 * The total number of comments available in the feed.
	 * @return
	 */
	public long getTotalResults()
	{
		return total;
	}

}
