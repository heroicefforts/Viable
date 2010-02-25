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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import net.heroicefforts.viable.android.dao.BugContext;
import net.heroicefforts.viable.android.dao.Comment;
import net.heroicefforts.viable.android.dao.CommentSet;
import net.heroicefforts.viable.android.dao.Issue;
import net.heroicefforts.viable.android.dao.ProjectDetail;
import net.heroicefforts.viable.android.dao.SearchResults;
import net.heroicefforts.viable.android.rep.Repository;
import net.heroicefforts.viable.android.rep.ServiceException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

public class Main
{
	private static final String PASSWORD = "viableTest123";
	private static final String USERNAME = "viable-it-test@heroicefforts.net";

	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
//			String date = "2010-02-17T06:19:07.000Z";
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//			System.out.println(sdf.parse(date));
//			System.exit(1);
				
			
			Repository rep = new GIssueTrackerRepository("viable-it-test", USERNAME, PASSWORD);
			
//			printAppStats(rep, "viable-it-test");
//
//			printFindById(rep, "100");
//			
//
//			printIssueComments(rep, "1");
//			
			Issue issue = new Issue();
//			issue.setAppName("Crashy");
//			issue.setStacktrace("java.lang.Exception:  Simulated stacktrace");
//			issue.setAffectedVersions(new String[] { "1.0.0" });
//			issue.setPriority("Critical");
//			issue.setType("Defect");
//			issue.setSummary("Test");
//			issue.setState("open");
//			issue.setDescription("Desc");
//			rep.postIssue(issue);
//			issue = new AtomIssue("<?xml version='1.0' encoding='UTF-8'?><entry xmlns='http://www.w3.org/2005/Atom' xmlns:gd='http://schemas.google.com/g/2005' xmlns:issues='http://schemas.google.com/projecthosting/issues/2009' gd:etag='W/&quot;AkABRn47eCl7ImA9WxBVFU8.&quot;'><id>http://code.google.com/feeds/issues/p/viable-it-test/issues/full/15</id><published>2010-02-18T21:32:37.000Z</published><updated>2010-02-18T21:32:37.000Z</updated><title>Test</title><content type='html'>&lt;p&gt;Desc&lt;/p&gt;&lt;p&gt;Affected Versions:  []&lt;/p&gt;&lt;p&gt;Stacktrace:  java.lang.Exception:  Simulated stacktrace&lt;/p&gt;</content><link rel='replies' type='application/atom+xml' href='http://code.google.com/feeds/issues/p/viable-it-test/issues/15/comments/full'/><link rel='alternate' type='text/html' href='http://code.google.com/p/viable-it-test/issues/detail?id=15'/><link rel='self' type='application/atom+xml' href='http://code.google.com/feeds/issues/p/viable-it-test/issues/full/15'/><author><name>viable-i...@heroicefforts.net</name><uri>/u/@VRFXR1xQBRNCXQM%3D/</uri></author><issues:id>15</issues:id><issues:label>Type-Defect</issues:label><issues:label>Priority-Critical</issues:label><issues:label>Hash-bb5add6b34e7aa3b29ec34de132fca6dfd16a2c1</issues:label><issues:stars>0</issues:stars><issues:state>open</issues:state><issues:status>New</issues:status></entry>");
//			printIssue(issue);
//			
//			checkExistence(rep, issue);
//
//			issue.setIssueId("0");
//			checkExistence(rep, issue);
//			
//			postIssue(rep);
//
//			System.out.println("Posting comment:");
//			issue = rep.findById("1");
			issue.setIssueId("1");
			BugContext ctx = new BugContext();
			ctx.copy(issue);
			Comment comment = new Comment("A user comment here.");
			rep.postIssueComment(ctx, comment);
			print(comment);
//		
//			print(rep.findCommentsForIssue("1", 1, 3));
//			SearchParams params = new SearchParams();
//			params.getAffectedVersions().add("1.0.0");
//			params.getAffectedVersions().add("1.1.0");
//			params.setPage(1);
//			params.setPageSize(10);
//			
//			SearchResults results = rep.search(params);
//			print(results);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void print(SearchResults results)
	{
		System.out.println("SearchResults { more:  " + results.isMore() + ", Comments {");
		for(Issue issue : results.getIssues())
			printIssue(issue);
		System.out.println("} }");
	}

	private static void printFindById(Repository rep, String issueId) throws ServiceException
	{
		Issue retVal = rep.findById(issueId);
		printIssue(retVal);
	}

	private static void printAppStats(Repository rep, String projectName) throws ServiceException
	{
		ProjectDetail detail = rep.getApplicationStats();
		print(detail);
	}

	private static void checkExistence(Repository rep, Issue issue) throws ServiceException
	{
		Issue retVal;
		retVal = rep.exists(issue);
		printIssue(retVal);
	}

	private static void printIssueComments(Repository rep, String issueId) throws ServiceException
	{
		boolean more = true;
		int page = 1;
		while(more)
		{
			CommentSet comments = rep.findCommentsForIssue(issueId, page++, 1);
			print(comments);
			more = comments.isMore();
		}
	}

	private static void print(ProjectDetail project)
	{
		System.out.println("ProjectDetail {");
		System.out.println("Name = '" + project.getName() + "'");
		System.out.println("Description = '" + project.getDescription() + "'");
		System.out.println("Lead = '" + project.getLead() + "'");
		System.out.println("Url = '" + project.getUrl() + "'");
		System.out.println("UnfixedBugs = '" + project.getUnfixedBugs() + "'");
		System.out.println("FixedBugs = '" + project.getFixedBugs() + "'");
		System.out.println("UnfixedImprovements = '" + project.getUnfixedImprovements() + "'");
		System.out.println("FixedImprovements = '" + project.getFixedImprovements() + "'");
		System.out.println("UnfixedFeatures = '" + project.getUnfixedFeatures() + "'");
		System.out.println("FixedFeatures = '" + project.getFixedFeatures() + "'");
		System.out.println("Versions = '" + project.getVersions() + "'");
		System.out.println("}");
	}

	private static void print(CommentSet comments)
	{
		System.out.println("CommentSet { more:  " + comments.isMore() + ", Comments {");
		for(Comment comment : comments.getComments())
			print(comment);
		System.out.println("} }");
	}

	private static void print(Comment comment)
	{
		System.out.println("Comment(" + comment.getId() + ", " + comment.getAuthor() + ", " + comment.getBody() + ", " + comment.getCreateDate() + ")");
	}

	private static void postIssue(Repository rep) throws UnsupportedEncodingException, IOException,
			ClientProtocolException, JSONException, ServiceException
	{
		Issue issue;
		issue = new Issue();
		issue.setSummary("Post 1");
		issue.setDescription("Description 1");
		issue.setAffectedVersions(new String[] { "1.0.0" });
		issue.setAppName("viable-it-test");
		issue.setPriority("Critical");
		issue.setType("Defect");
		issue.setState("open");
		issue.setStacktrace("java.lang.Exception:  Simulated stacktrace");
		rep.postIssue(issue);
		System.out.println("Created issue:  " + issue.getIssueId());
	}

	private static final String toString(Throwable t)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		t.printStackTrace(ps);
		ps.flush();
		return baos.toString(); 
	}
	
	private static void printIssue(Issue retVal)
	{
		if(retVal != null)
		{
			System.out.println("Id:  " + retVal.getIssueId());
			System.out.println("Summary:  " + retVal.getSummary());
			System.out.println("Description:  " + retVal.getDescription());
			System.out.println("Aff Versions:  " + Arrays.asList(retVal.getAffectedVersions()));
			System.out.println("Aff Devices:  " + Arrays.asList(retVal.getAffectedDevices()));
			System.out.println("Aff Models:  " + Arrays.asList(retVal.getAffectedModels()));
			System.out.println("Aff SDKs:  " + Arrays.asList(retVal.getAffectedSDKs()));			
			System.out.println("Type:  " + retVal.getType());
			System.out.println("State:  " + retVal.getState());
			System.out.println("Priority:  " + retVal.getPriority());
			System.out.println("Hash:  " + retVal.getHash());
			System.out.println("Stacktrace:  " + retVal.getStacktrace());
			System.out.println("Create date:  " + retVal.getCreateDate());
			System.out.println("Modified date:  " + retVal.getModifiedDate());
		}
		else
		{
			System.out.println("Issue:  null");
		}
	}

}
