package net.heroicefforts.viable.android.rep.it.gdata;

import java.util.List;

import net.heroicefforts.viable.android.dao.Issue;

/**
 * A wrapper around issues.
 * 
 * @author jevans
 *
 */
public class IssuesFeed
{
	private List<Issue> issues;

	public IssuesFeed(List<Issue> issues)
	{
		this.issues = issues;
	}

	/**
	 * Returns a page is issues.
	 * @return
	 */
	public List<Issue> getEntries()
	{
		return issues;
	}

}
