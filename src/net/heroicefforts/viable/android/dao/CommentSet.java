package net.heroicefforts.viable.android.dao;

import java.util.List;

/**
 * This class contains a page of comments.
 * 
 * @author jevans
 *
 */
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

	/**
	 * A list of comments.
	 * @return
	 */
	public List<Comment> getComments()
	{
		return comments;
	}

	/**
	 * Return true if there are subsequent comments available.
	 * @return
	 */
	public boolean isMore()
	{
		return more;
	}
	
	
}
