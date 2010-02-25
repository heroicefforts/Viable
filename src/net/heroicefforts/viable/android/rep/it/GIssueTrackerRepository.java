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
package net.heroicefforts.viable.android.rep.it;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.heroicefforts.viable.android.dao.Comment;
import net.heroicefforts.viable.android.dao.CommentSet;
import net.heroicefforts.viable.android.dao.Issue;
import net.heroicefforts.viable.android.dao.ProjectDetail;
import net.heroicefforts.viable.android.dao.SearchParams;
import net.heroicefforts.viable.android.dao.SearchResults;
import net.heroicefforts.viable.android.dao.VersionDetail;
import net.heroicefforts.viable.android.rep.CreateException;
import net.heroicefforts.viable.android.rep.IssueResource;
import net.heroicefforts.viable.android.rep.Repository;
import net.heroicefforts.viable.android.rep.ServiceException;
import net.heroicefforts.viable.android.rep.it.auth.Authenticate;
import net.heroicefforts.viable.android.rep.it.auth.AuthenticationException;
import net.heroicefforts.viable.android.rep.it.auth.GCLAccountAuthenticator;
import net.heroicefforts.viable.android.rep.it.auth.NetworkException;
import net.heroicefforts.viable.android.rep.it.gdata.IssueCommentsFeed;
import net.heroicefforts.viable.android.rep.it.gdata.IssuesFeed;
import net.heroicefforts.viable.android.rep.it.gdata.IssuesQuery;
import net.heroicefforts.viable.android.rep.it.gdata.ProjectHostingService;

import org.apache.http.HttpStatus;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * This is the repository implementation that bridges to the Google Issue Tracker.  Viable client applications that
 * wish to register their I.T. project should add lines similar to those found below:<br/>
 * <br/><pre>
   &lt;meta-data android:name="viable-provider" android:value="Google" /&gt;
   &lt;meta-data android:name="viable-project-name" android:value="&lt;your project name, defined after the /p/ in Google's URL&gt;" /&gt;
   &lt;meta-data android:name="viable-project-description" android:value="A description of your project." /&gt;
   &lt;meta-data android:name="viable-project-lead" android:value="&lt;Your Name&gt;" /&gt;
   &lt;meta-data android:name="viable-project-versions" android:value="1.0.0, 1.1.0" /&gt;
 * </pre><br/>
 * 
 * @author jevans
 * 
 */
public class GIssueTrackerRepository implements Repository
{
	private static final String PARAM_PROJECT_NAME = "viable-project-name";
	private static final String PARAM_VERSIONS = "viable-project-versions";
	protected static final String TAG = "GIssueTrackerRepository";
	private static final String PARAM_PROJECT_DESC = "viable-project-description";
	private static final String PARAM_PROJECT_ADMIN = "viable-project-lead";

	private Activity act;
	private static ProjectHostingService host;
	private String projectName;
	private String appName;
	private String description;
	private String lead;

	private List<VersionDetail> versions = new ArrayList<VersionDetail>();
	private AccountManagerCallback<Bundle> callback = new AccountManagerCallback<Bundle>() {

		public void run(AccountManagerFuture<Bundle> future)
		{
			try
			{
				String token = future.getResult().getString(AccountManager.KEY_AUTHTOKEN);
				Log.v(TAG, "New account token:  " + token);
				host.setAuthSubToken(token);
			}
			catch (Exception e)
			{
				Log.e(TAG, "Error adding account.", e);
			}
		}
	
	};
	
	public GIssueTrackerRepository(String appName, Activity act, Bundle metaData)
		throws CreateException
	{
		this.appName = appName;
		this.act = act;
		this.projectName = metaData.getString(PARAM_PROJECT_NAME);
		if(projectName == null)
			throw new CreateException("No '" + "viable-project-name" + "' meta-data field defined for application.  Google Isusue Tracker Repository cannot be constructed.");
		this.description = metaData.getString(PARAM_PROJECT_DESC);
		this.lead = metaData.getString(PARAM_PROJECT_ADMIN);
		String versionStr = metaData.getString(PARAM_VERSIONS);
		if(versionStr != null)
		{
			String[] versions = versionStr.split("[ ]*,[ ]*");
			for(String version : versions)
				this.versions.add(new VersionDetail(version));
		}
		
		try
		{				
			host = new ProjectHostingService();
			String token = Authenticate.authenticate(act, GCLAccountAuthenticator.TOKEN_TYPE_ISSUE_TRACKER);
			if(token != null)
				host.setAuthSubToken(token);
			else
			{
				//TODO add popup.
				AccountManager acct = (AccountManager) act.getSystemService(Context.ACCOUNT_SERVICE);
				Log.d(TAG, "Requesting account creation.");
				acct.addAccount(GCLAccountAuthenticator.ACCT_TYPE, "code", null, null, act, callback  , null);
			}
		}
		catch (AuthenticatorException e)
		{
			throw new CreateException("Exception authenticating Google account for Issue Tracker repository access.");
		}
		catch (Exception e)
		{
			throw new CreateException("Exception creating Google Issue Tracker repository.", e);
		}
	}

	GIssueTrackerRepository(String appName, String username, String password)
	 	throws AuthenticationException, NetworkException
	{
		this.projectName = appName;
		host = new ProjectHostingService();
		host.setUserCredentials(username, password);
	}

	public Issue exists(Issue issue)
		throws ServiceException
	{
		String hash = createHash(issue);
		return findByHash(hash);
	}

	private Issue findByHash(String hash) 
		throws ServiceException
	{
		try
		{
			Issue retVal = null;

			if(hash != null)
			{
				URL feedUrl = new URL("http://code.google.com/feeds/issues/p/" + projectName + "/issues/full");
				IssuesQuery myQuery = new IssuesQuery(feedUrl);
				myQuery.setLabel("Hash-" + hash);
				IssuesFeed resultFeed = host.query(myQuery);
	
				if (resultFeed.getEntries().size() > 0)
				{
					retVal = resultFeed.getEntries().get(0);
					retVal.setAppName(appName);
				}
			}
			
			return retVal;
		}
		catch (MalformedURLException e)
		{
			throw new ServiceException("Error retrieving issue by hash '" + hash + "'.", e);
		}
	}

	public Issue findById(String issueId)
		throws ServiceException
	{
		try
		{
			URL feedUrl = new URL("http://code.google.com/feeds/issues/p/" + projectName + "/issues/full/" + issueId);
			Issue found = host.getEntry(feedUrl);
			if(found != null)
				found.setAppName(appName);
			
			return found;
		}
		catch (MalformedURLException e)
		{
			throw new ServiceException("Error searching for issue '" + issueId + "'.", e);
		}
	}

	public CommentSet findCommentsForIssue(String issueId, int page, int pageSize)
		throws ServiceException
	{
		try
		{
			int startIndex = ((page - 1) * pageSize) + 1; // 1 based
			int maxResults = pageSize + 1;
			URL feedUrl = new URL("http://code.google.com/feeds/issues/p/" + projectName + "/issues/" + issueId
					+ "/comments/full" + "?start-index=" + startIndex + "&max-results=" + maxResults);
			IssueCommentsFeed resultFeed = host.getFeed(feedUrl);
			List<Comment> comments = resultFeed.getEntries();
			CommentSet set = null;
			if (comments.size() > pageSize)
				set = new CommentSet(comments.subList(0, pageSize), true);
			else
				set = new CommentSet(comments, false);

			return set;
		}
		catch (MalformedURLException e)
		{
			throw new ServiceException("Error retrieving comments for issue '" + issueId + "'.", e);
		}
	}

	public ProjectDetail getApplicationStats()
		throws ServiceException
	{
		String name = appName;
		String url = "http://code.google.com/p/" + projectName;
		long unfixedBugs = count("http://code.google.com/feeds/issues/p/" + projectName + "/issues/full?can=open&label=Type-Defect");
		long fixedBugs = count("http://code.google.com/feeds/issues/p/" + projectName + "/issues/full?label=Type-Defect") - unfixedBugs;
		long unfixedImprovements = 0;
		long fixedImprovements = 0;
		long unfixedFeatures = count("http://code.google.com/feeds/issues/p/" + projectName + "/issues/full?can=open&label=Type-Enhancement");
		long fixedFeatures = count("http://code.google.com/feeds/issues/p/" + projectName + "/issues/full?label=Type-Enhancement") - unfixedFeatures;
		List<VersionDetail> versions = new ArrayList<VersionDetail>();

		ProjectDetail project = new ProjectDetail();
		project.setName(name);
		project.setDescription(description);
		project.setLead(lead);
		project.setUrl(url);
		project.setUnfixedBugs(unfixedBugs);
		project.setFixedBugs(fixedBugs);
		project.setUnfixedImprovements(unfixedImprovements);
		project.setFixedImprovements(fixedImprovements);
		project.setUnfixedFeatures(unfixedFeatures);
		project.setFixedFeatures(fixedFeatures);
		project.setVersions(versions);

		return project;
	}

	private long count(String query)
		throws ServiceException
	{
		try
		{
			if(query.indexOf("?") == -1)
				query += "?";
			else
				query += "&";
			URL feedUrl = new URL(query + "start-index=" + 1 + "&max-results=" + 0);
			IssueCommentsFeed resultFeed = host.getFeed(feedUrl);
			return resultFeed.getTotalResults();
		}
		catch (MalformedURLException e)
		{
			throw new ServiceException("Error counting query '" + query + "'.", e);
		}
	}

	public int postIssue(Issue issue) 
		throws ServiceException
	{
		try
		{
			URL postUrl = new URL("http://code.google.com/feeds/issues/p/" + projectName + "/issues/full");
			issue.setHash(createHash(issue));			
			host.insert(postUrl, issue);
			issue.setAppName(appName);
			return HttpStatus.SC_CREATED;
		}
		catch (AuthenticationException e)
		{
			Log.i(TAG, "Authorization failed.", e);
			manageAccount();
			return HttpStatus.SC_UNAUTHORIZED;
		}
		catch (MalformedURLException e)
		{
			throw new ServiceException("Error posting issue.", e);
		}
	}

	private int updateIssue(Issue issue) 
		throws ServiceException
	{
		try
		{
			URL putUrl = new URL("http://code.google.com/feeds/issues/p/" + projectName + "/issues/" + issue.getIssueId());
			issue.setHash(createHash(issue));			
			host.update(putUrl, issue);
			issue.setAppName(appName);
			return HttpStatus.SC_CREATED;
		}
		catch (AuthenticationException e)
		{
			Log.i(TAG, "Authorization failed.", e);
			manageAccount();
			return HttpStatus.SC_UNAUTHORIZED;
		}
		catch (MalformedURLException e)
		{
			throw new ServiceException("Error posting issue.", e);
		}
	}

	
	
	private void manageAccount()
	{
		AccountManager accMgr = (AccountManager) act.getSystemService(Context.ACCOUNT_SERVICE);
		Log.d(TAG, "Requesting account creation.");
		Account[] accts = accMgr.getAccountsByType(GCLAccountAuthenticator.ACCT_TYPE);
		if(accts.length > 0)
			accMgr.confirmCredentials(accts[0], null, act, callback, null);
		else
			accMgr.addAccount(GCLAccountAuthenticator.ACCT_TYPE, GCLAccountAuthenticator.TOKEN_TYPE_ISSUE_TRACKER, null, null, act, callback, null);
	}

	public int postIssueComment(Issue issue, Comment comment)
		throws ServiceException
	{
		try
		{
			URL postUrl = new URL("http://code.google.com/feeds/issues/p/" + projectName + "/issues/" + issue.getIssueId() + "/comments/full");
			comment = host.insert(postUrl, comment);
			return HttpStatus.SC_CREATED;
		}
		catch (AuthenticationException e)
		{
			Log.i(TAG, "Authorization failed.", e);
			manageAccount();
			return HttpStatus.SC_UNAUTHORIZED;
		}
		catch (MalformedURLException e)
		{
			throw new ServiceException("Error posting comment.", e);
		}
	}
	
//	private void appendEnvValue(StringBuilder envBuf)
//	{
//		envBuf.append("Phone - Device:  ").append(Build.DEVICE).append(", Model:  ")
//		      .append(Build.MODEL).append(", SDK:  ").append(Build.VERSION.SDK_INT);
//	}
		
	private String createHash(Issue issue)
	{
		String hash = null;

		if (issue.getStacktrace() != null)
		{
			String projectKey = projectName;
			try
			{
				MessageDigest md = MessageDigest.getInstance("SHA");

				try
				{
					md.update(projectKey.getBytes("UTF8"));
					md.update(issue.getStacktrace().getBytes("UTF8"));
					byte[] hashBytes = md.digest();
					String hex = new BigInteger(1, hashBytes).toString(16);
					if (hex.length() % 2 != 0)
						hex = "0" + hex;
					hash = new String(hex);
					issue.setHash(hash);
				} 
				catch (UnsupportedEncodingException e)
				{
					Log.e(TAG, "Error generating bug hash using UTF8 encoding.  Hash disabled.", e);
				}
			}
			catch (NoSuchAlgorithmException e)
			{
				Log.e(TAG, "Error generating bug hash.  Failed to find SHA digest.  Hash disabled.", e);
			}
		}

		return hash;
	}

	public SearchResults search(SearchParams params) 
		throws ServiceException
	{
		final int startIdx = (params.getPage() - 1) * params.getPageSize();
		final int pageSize = params.getPageSize();
		
		LinkedHashSet<Issue> issues = new LinkedHashSet<Issue>();		
		for(int i = startIdx; i < params.getIds().size() && i < startIdx + pageSize + i; i++)
			issues.add(findById(params.getIds().get(i)));
		
		if(issues.size() <= pageSize)
		{
			Issue issue = findByHash(params.getHash());
			if(issue != null)
				issues.add(issue);
		}	
		
		if(issues.size() <= pageSize)
		{
			long skip = startIdx;
			long skipped = params.getIds().size() + (params.getHash() != null ? 1 : 0);
			for(int i = 0; i < params.getAffectedVersions().size() && issues.size() <= pageSize; i++)
			{
				long count = countIssuesByVersion(params.getAffectedVersions().get(i));
				if(skipped + count > skip)
				{
					int needed = pageSize - issues.size() + 1;
					long startAt = skip - skipped;
					if(startAt < 0)
						startAt = 0;
					issues.addAll(findIssuesByVersion(params.getAffectedVersions().get(i), startAt, needed));
					skipped = skip;
				}
				else
				{
					skipped += count;
				}
			}
		}
		
		setAppName(issues);
		
		if(issues.size() > pageSize)
			return new SearchResults(new ArrayList<Issue>(issues).subList(0, pageSize), true);
		else
			return new SearchResults(new ArrayList<Issue>(issues), false);
	}

	public boolean vote(Issue issue) throws ServiceException
	{
//		issue.setVotes(issue.getVotes() + 1);
//		return HttpStatus.SC_CREATED == updateIssue(issue);
		return false;
	}
	
	private void setAppName(LinkedHashSet<Issue> issues)
	{
		for(Issue issue : issues)
			issue.setAppName(appName);
	}

	private Collection<? extends Issue> findIssuesByVersion(String version, long startAt, int max) 
		throws ServiceException
	{
		startAt += 1; //1 based
			
		try
		{
			String feedStr;
			if(!"all".equals(version))
				feedStr = "http://code.google.com/feeds/issues/p/" + projectName + "/issues/full?label=AffectedVersion-" + version + "&start-index=" + startAt + "&max-results=" + max;
			else
				feedStr = "http://code.google.com/feeds/issues/p/" + projectName + "/issues/full?start-index=" + startAt + "&max-results=" + max;
			URL feedUrl = new URL(feedStr);
			IssuesQuery myQuery = new IssuesQuery(feedUrl);
			IssuesFeed resultFeed = host.query(myQuery);			
			return resultFeed.getEntries();
		}
		catch (MalformedURLException e)
		{
			throw new ServiceException("Error retrieving issues for version '" + version + "'.", e);
		}

	}

	private long countIssuesByVersion(String version)
		throws ServiceException
	{
		if(!"all".equals(version))
			return count("http://code.google.com/feeds/issues/p/" + projectName + "/issues/full?label=AffectedVersion-" + version);
		else
			return count("http://code.google.com/feeds/issues/p/" + projectName + "/issues/full");
	}

	public Set<? extends IssueResource> getDefaultStates()
	{
		return ITResourceFlyweight.getDefaultStates();
	}

	public Set<? extends IssueResource> getDefaultDefectStates()
	{
		return ITResourceFlyweight.getDefaultDefectStates();
	}

	public IssueResource getState(String type, String priority, String state)
	{
		return ITResourceFlyweight.getState(type, priority, state);
	}

	public List<VersionDetail> getApplicationVersions()
	{
		return versions ;
	}


}
