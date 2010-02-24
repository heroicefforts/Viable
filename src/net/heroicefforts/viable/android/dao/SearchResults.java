package net.heroicefforts.viable.android.dao;

import java.util.List;

/**
 * This class contains the results of the issue search.
 * 
 * @author jevans
 *
 */
public class SearchResults
{
	private List<Issue> issues;
	private boolean more;
	
	
	public SearchResults(List<Issue> issues, boolean more)
	{
		super();
		this.issues = issues;
		this.more = more;
	}
	
	/**
	 * A list of the issues found.
	 * @return non-null list of issues.
	 */
	public List<Issue> getIssues()
	{
		return issues;
	}
	
	/**
	 * Returns true if there is a subsequent page of results available.
	 * @return
	 */
	public boolean isMore()
	{
		return more;
	}
	
	
}
