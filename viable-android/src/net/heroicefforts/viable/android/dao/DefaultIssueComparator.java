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
