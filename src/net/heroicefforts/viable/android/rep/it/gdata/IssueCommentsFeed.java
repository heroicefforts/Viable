/*
 *  Copyright 2010 Heroic Efforts, LLC
 *  
 *  This file is part of Viable.
 *
 *  Viable is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Viable is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Viable.  If not, see <http://www.gnu.org/licenses/>.
 */
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
