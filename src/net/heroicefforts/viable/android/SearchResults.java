package net.heroicefforts.viable.android;

import java.util.List;

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
	public List<Issue> getIssues()
	{
		return issues;
	}
	public boolean isMore()
	{
		return more;
	}
	
	
}
