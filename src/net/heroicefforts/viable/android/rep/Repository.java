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


public interface Repository
{
	public abstract Issue exists(Issue issue)
		throws ServiceException;

	public abstract Issue findById(String issueId) 
		throws ServiceException;

	public abstract SearchResults search(SearchParams params)
		throws ServiceException;

	public abstract int postIssue(Issue issue) 
		throws ServiceException;

	public int postIssueComment(Issue issue, Comment comment) 
		throws ServiceException;
	
    public CommentSet findCommentsForIssue(String issueId, int page, int pageSize) 
    	throws ServiceException;
    
    public ProjectDetail getApplicationStats(String appName)
    	throws ServiceException;

    public List<VersionDetail> getApplicationVersions()
		throws ServiceException;

	public IssueResource getState(String type, String priority, String state);

	public Set<? extends IssueResource> getDefaultDefectStates();

	public Set<? extends IssueResource> getDefaultStates();
    
}