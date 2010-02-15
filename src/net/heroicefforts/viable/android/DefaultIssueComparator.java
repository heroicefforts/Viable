package net.heroicefforts.viable.android;

import java.util.Comparator;

public class DefaultIssueComparator implements Comparator<Issue>
{

	public int compare(Issue issue1, Issue issue2)
	{
		int retVal = issue1.getAppName().compareTo(issue2.getAppName());
		if(retVal == 0)
			retVal = issue1.getIssueId().compareTo(issue2.getIssueId());
		
		return retVal;
	}

}
