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
