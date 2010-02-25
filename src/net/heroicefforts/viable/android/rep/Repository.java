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
package net.heroicefforts.viable.android.rep;

import java.util.List;
import java.util.Set;

import net.heroicefforts.viable.android.dao.Comment;
import net.heroicefforts.viable.android.dao.CommentSet;
import net.heroicefforts.viable.android.dao.Issue;
import net.heroicefforts.viable.android.dao.ProjectDetail;
import net.heroicefforts.viable.android.dao.SearchParams;
import net.heroicefforts.viable.android.dao.SearchResults;
import net.heroicefforts.viable.android.dao.VersionDetail;

/**
 * This is the main service interface implemented by bridges to the various issue tracking systems.
 * 
 * @author jevans
 *
 */
public interface Repository
{
	/**
	 * The service performs a checksum lookup to determine if the issue data already exists in the repository.  If so,
	 * then the issue is returned.  Checksum calcs are to be determined by the service.
	 * @param issue the issue to check
	 * @return the located issue, or null
	 * @throws ServiceException if a service error occurs.
	 */
	public abstract Issue exists(Issue issue)
		throws ServiceException;

	/**
	 * The service attempts to locate the issue by unique id.
	 * @param issueId the issue's unique identifier as provided by the repository.
	 * @return the located issue, or null
	 * @throws ServiceException if a service error occurs.
	 */
	public abstract Issue findById(String issueId) 
		throws ServiceException;

	/**
	 * Searches for issues matching the supplied parameters.
	 * @param params the search criteria
	 * @return results matching the critieria.
	 * @throws ServiceException if a service error occurs.
	 */
	public abstract SearchResults search(SearchParams params)
		throws ServiceException;

	/**
	 * Adds a new issue to the repository.  The supplied issue should be updated with the repository supplied id
	 * and any other changed data.
	 * @param issue the issue to add
	 * @return the response code
	 * @throws ServiceException if a service error occurs.
	 */
	public abstract int postIssue(Issue issue) 
		throws ServiceException;

	/**
	 * Adds a new issue comment to the repository.  The supplied comment should be updated with the repository supplied id
	 * and any other changed data.
	 * @param issue the issue to which the comment should be posted
	 * @param comment the comment to post
	 * @return the response code
	 * @throws ServiceException if a service error occurs.
	 */
	public int postIssueComment(Issue issue, Comment comment) 
		throws ServiceException;
	
	/**
	 * Returns a page of comments belonging to the issue specified.
	 * @param issueId the id of the issue
	 * @param page the page to fetch (1-based).
	 * @param pageSize the number of results to return.
	 * @return the page of comments
	 * @throws ServiceException if a service error occurs.
	 */
    public CommentSet findCommentsForIssue(String issueId, int page, int pageSize) 
    	throws ServiceException;
    
    /**
     * Returns summary data corresponding to this repository / project.  Some of this data may be pulled from the repository
     * or from the client's application meta-data depending upon the implementation limitations.
     * 
     * @return the project summary details
     * @throws ServiceException if a service error occurs.
     */
    public ProjectDetail getApplicationStats()
    	throws ServiceException;

    /**
     * Returns the release version details associated with this repository / project.
     * @return a non-null list of release details.
     * @throws ServiceException if a service error occurs.
     */
    public List<VersionDetail> getApplicationVersions()
		throws ServiceException;

    /**
     * Submits a vote for the specified issue on behalf of the user.
     * @param issue the issue for which to vote
     * @return true if the vote was successful.
     * @throws ServiceException
     */
	public boolean vote(Issue issue)
		throws ServiceException;
    
    /**
     * Returns an IssueResource matching the state of the issue.
     * @param type the issue type
     * @param priority the issue priority
     * @param state the issue state
     * @return an issue resource
     */
	public IssueResource getState(String type, String priority, String state);

	/**
	 * Returns a set of IssueResources representing user selectable states for defect issue types.
	 * @return
	 */
	public Set<? extends IssueResource> getDefaultDefectStates();

	/**
	 * Returns a set of IssueResources representing user selectable states for all issue types. 
	 * @return
	 */
	public Set<? extends IssueResource> getDefaultStates();
    	
}