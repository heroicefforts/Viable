package net.heroicefforts.viable.android.dao;

import java.util.Comparator;

/**
 * Sorts issues in descending order by application and id.
 * 
 * @author jevans
 *
 */
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
