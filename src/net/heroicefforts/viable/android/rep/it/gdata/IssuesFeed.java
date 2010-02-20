package net.heroicefforts.viable.android.rep.it.gdata;

import java.util.List;

import net.heroicefforts.viable.android.dao.Issue;

public class IssuesFeed
{
	private List<Issue> issues;

	public IssuesFeed(List<Issue> issues)
	{
		this.issues = issues;
	}

	public List<Issue> getEntries()
	{
		return issues;
	}

}
