package net.heroicefforts.viable.jira.plugin;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.exception.DataAccessException;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueFactory;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.priority.Priority;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.issue.vote.VoteManager;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.project.version.VersionManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.EasyList;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.query.Query;
import com.atlassian.query.operand.SingleValueOperand;
import com.atlassian.query.operator.Operator;

 
/**
 * REST resource that provides a list of projects in JSON format.
 */
@Path("/issue")
public class CreateAndroidIssueResource
{
	private static final int DEFAULT_PAGE_SIZE = 10;

	private static final Logger log = Logger.getLogger(CreateAndroidIssueResource.class);
	
	private ProjectManager projMgr;
	private IssueFactory issueFactory;
	private ConstantsManager constMgr;
	private VersionManager verMgr;
	private IssueManager issueMgr;
	private JiraAuthenticationContext authCtx;
	private CustomFieldManager fieldMgr;

	private SearchProvider srchMgr;

	private CommentManager commentMgr;

	private VoteManager voteMgr;

	/**
	 * Constructor.
	 * 
	 * @param userManager
	 *            a SAL object used to find remote usernames in Atlassian
	 *            products
	 * @param userUtil
	 *            a JIRA object to resolve usernames to JIRA's internal {@code
	 *            com.opensymphony.os.User} objects
	 * @param permissionManager
	 *            the JIRA object which manages permissions for users and
	 *            projects
	 */
	public CreateAndroidIssueResource(ProjectManager projMgr, IssueFactory issueFactory, ConstantsManager constMgr, VersionManager verMgr, 
		IssueManager issueMgr, JiraAuthenticationContext authCtx, CustomFieldManager fieldMgr, SearchProvider srchMgr,
		CommentManager commentMgr, VoteManager voteMgr)
	{
		this.projMgr = projMgr;
		this.issueFactory = issueFactory;		
		this.constMgr = constMgr;
		this.verMgr = verMgr;
		this.issueMgr = issueMgr;
		this.authCtx = authCtx;
		this.fieldMgr = fieldMgr;
		this.srchMgr = srchMgr;
		this.commentMgr = commentMgr;
		this.voteMgr = voteMgr;
	}

	@POST
	@Path("/issue/{issueId}/vote")
	@AnonymousAllowed
	@Consumes("application/x-www-form-urlencoded")
	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response vote(@PathParam("issueId")String issueId)
	{
		//TODO probably need some asyc buffering
		MutableIssue issue = issueMgr.getIssueObject(issueId);
		if(issue != null)
		{
//			voteMgr.addVote(authCtx.getUser(), issue.getGenericValue());
			issue.setVotes(issue.getVotes().longValue() + 1);
			issue.store();
			return Response.ok().build();
		}
		else
			return Response.status(404).build();
	}
	
	@POST
	@Path("/issue/{issueId}/comments")
	@AnonymousAllowed
	@Consumes("application/x-www-form-urlencoded")
	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response createComment(@Context UriInfo uriInfo, @FormParam("app_name") String appName, @PathParam("issueId")String issueId,
		@FormParam("body") String body, @FormParam("phone_model") String phoneModel, @FormParam("phone_version") String phoneVersion,
		@FormParam("phone_device") String phoneDevice)
	{
		log.debug("Adding comment to existing issue.");
		MutableIssue issue = issueMgr.getIssueObject(issueId);
		if(issue != null)
		{
			StringBuilder bodyStr = new StringBuilder();
			final String EOL = System.getProperty("line.separator");
			bodyStr.append(createEnvValue(phoneModel, phoneVersion, phoneDevice)).append(EOL);
			bodyStr.append(EOL);
			bodyStr.append(body).append(EOL);
			String name = null;
			if(authCtx.getUser() != null)
				name = authCtx.getUser().getName();
			
			Comment comment = commentMgr.create(issue, name, bodyStr.toString(), true);
			CommentDetail detail = new CommentDetail(comment);
			
			URI issueUri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(comment.getId())).build();			
			return Response.created(issueUri).entity(detail).build();
		}
		else
			return Response.status(404).build();
	}
	
	/**
	 * Returns the list of projects browsable by the user in the specified
	 * request.
	 * 
	 * @param request
	 *            the context-injected {@code HttpServletRequest}
	 * @return a {@code Response} with the marshalled projects
	 */
	@POST
	@Path("/search")
	@AnonymousAllowed
	@Consumes("application/x-www-form-urlencoded")
	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response searchIssue(@Context HttpServletRequest request, @FormParam("hash") String hashParam, @FormParam("issue_id") List<String> keys,
		@FormParam("app_name") String appName, @FormParam("affected_version") List<String> affectedVersions, 
		@FormParam("page") int page, 
		@FormParam("start") int start, @FormParam("page_size") int pageSize) 
	{
		Response retval = null;
		log.error("Hash search:  '" + hashParam + "'");
		log.error("Search keys:  " + keys);
		log.error("Search app:  " + appName);
		log.error("Search versions:  " + affectedVersions);
		log.error("Search pages:  " + page + "[" + pageSize + "]");
		log.error("Search start:  " + start + "[" + pageSize + "]");
		log.error("Accept-encoding:  " + request.getHeader("accept-encoding"));
		try
		{
			if(start == 0)
				start = (page - 1) * pageSize;
			//TODO handle empty paging params
			List<Issue> issues = new ArrayList<Issue>();

			CustomField hashField = fieldMgr.getCustomFieldObjectByName("Hash");
			CustomField straceField = fieldMgr.getCustomFieldObjectByName("Stacktrace");
			if(hashField != null)
			{
				Issue issue = searchByHash("Hash", hashParam);
				if(issue != null)
					issues.add(issue);
			}

			if(keys != null)
			{
				for(String key : keys)
				{
					MutableIssue issue = issueMgr.getIssueObject(key);
					if(issue != null)
						issues.add(issue);
				}
			}

			if(pageSize == 0)
				pageSize = DEFAULT_PAGE_SIZE;
			
			if(appName != null && issues.size() < start + pageSize)
			{
				Project project = projMgr.getProjectObjByName(appName);
				if(project != null)
					issues.addAll(searchByVersions(issues.size(), start, pageSize, project.getKey(), affectedVersions));
			}
			
			List<IssueDetail> detailed = new ArrayList<IssueDetail>();
			for(Issue issue : issues)
			{
				String stacktrace = null;
				if(straceField != null)
				{
					Object value = issue.getCustomFieldValue(straceField);
					if(value != null)
						stacktrace = value.toString();
				}
				
				String hash = null;
				if(hashField != null)
				{
					Object value = issue.getCustomFieldValue(hashField);
					if(value != null)
						hash = value.toString();
				}
					
				
				detailed.add(new IssueDetail(issue, stacktrace, hash));
			}

			boolean more = false;
			
			int begin = 0;
			int end = (begin + pageSize > detailed.size() ? detailed.size() : begin + pageSize);
			if(end != detailed.size())
				more = true;
			detailed = detailed.subList(begin, end);
			log.error("Sublist[" + begin + "," + end + "]");
			
			if(issues.size() > 0)
				retval = Response.ok(new IssueSet(detailed, more)).build(); 
			else
				retval = Response.status(404).build();
		}
		catch (SearchException e)	
		{
			log.error("Error searching by hash for user issue search.", e);
			retval = Response.serverError().build();
		}
		
		return retval;
	}

	@GET
	@Path("/app/{appName}/versions")
	@AnonymousAllowed
	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getVersions(@PathParam("appName") String appName)
	{
		Project project = projMgr.getProjectObjByName(appName);
		if(project != null)
		{
			Collection<Version> versions = project.getVersions();
			VersionSet set = new VersionSet(versions);
			return Response.ok(set).build();
		}
		else
			return Response.status(404).build();
	}	
	
	@GET
	@Path("/app/{appName}/stats")
	@AnonymousAllowed
	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getAppStats(@PathParam("appName") String appName)
	{
		Project project = projMgr.getProjectObjByName(appName);
		if(project != null)
		{
			String name = project.getName();
			String url = project.getUrl();
			String desc = project.getDescription();
			String leadName = project.getLead().getFullName();
			
			long fixedBugs;
			long fixedImprovements;
			long fixedFeatures;
			long unfixedBugs;
			long unfixedImprovements;
			long unfixedFeatures;
			try
			{
				JqlClauseBuilder builder = JqlQueryBuilder.newClauseBuilder();
				Query query = builder.project(project.getName()).and().issueType("Bug").and().status("Resolved", "Closed").buildQuery();
				fixedBugs = srchMgr.searchCount(query, authCtx.getUser());

				builder.clear();
				query = builder.project(project.getName()).and().issueType("Improvement").and().status("Resolved", "Closed").buildQuery();
				fixedImprovements = srchMgr.searchCount(query, authCtx.getUser());

				builder.clear();
				query = builder.project(project.getName()).and().issueType("New Feature").and().status("Resolved", "Closed").buildQuery();
				fixedFeatures = srchMgr.searchCount(query, authCtx.getUser());
				
				builder.clear();
				query = builder.project(project.getName()).and().issueType("Bug").and().not().status("Resolved", "Closed").buildQuery();
				unfixedBugs = srchMgr.searchCount(query, authCtx.getUser());

				builder.clear();
				query = builder.project(project.getName()).and().issueType("Improvement").and().not().status("Resolved", "Closed").buildQuery();
				unfixedImprovements = srchMgr.searchCount(query, authCtx.getUser());

				builder.clear();
				query = builder.project(project.getName()).and().issueType("New Feature").and().not().status("Resolved", "Closed").buildQuery();
				unfixedFeatures = srchMgr.searchCount(query, authCtx.getUser());
			}
			catch (SearchException e)
			{
				return Response.serverError().build();
			}

			Collection<Version> versions = project.getVersions();
			
			ProjectDetail pd = new ProjectDetail(name, desc, leadName, url, versions);
			pd.addBugCounts(unfixedBugs, fixedBugs);
			pd.addImprovementCounts(unfixedImprovements, fixedImprovements);
			pd.addFeatures(unfixedFeatures, fixedFeatures);
			
			return Response.ok(pd).build();
		}		
		else
		{
			log.error("Project '" + appName + "' could not be found.");		
			return Response.status(404).build();
		}
	}
	
	@GET
	@Path("/issue/{issueId}/comments")
	@AnonymousAllowed
	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getComments(@PathParam("issueId") String issueId, @QueryParam("page") int page, 
			@DefaultValue("10") @QueryParam("page_size") int pageSize)
	{
		MutableIssue issue = issueMgr.getIssueObject(issueId);
		if(issue != null)
		{
			List<Comment> comments = commentMgr.getComments(issue);
			if(comments.size() > 0)
			{
				boolean more = false;
				if(page > 0)
				{
					int start = (page - 1) * pageSize;
					int end = ((start + pageSize) > comments.size()) ? comments.size() : (start + pageSize);
					if(comments.size() > end)
						more = true;
					comments = comments.subList(start, end);
				}
				
				List<CommentDetail> details = new ArrayList<CommentDetail>(comments.size());
				for(Comment comment : comments)
					details.add(new CommentDetail(comment));
				
				CommentSet commentSet = new CommentSet(details, more);
				log.error("Returning " + details.size() + " comments for issue '" + issueId + "'.");
				return Response.ok(commentSet).build();
			}
			else
				log.error("No comments found for issue '" + issueId + "'.");
		}
		else
			log.error("Issue '" + issueId + "' could not be found.");
		
		return Response.status(404).build();		
	}
	
	/**
	 * Returns the list of projects browsable by the user in the specified
	 * request.
	 * 
	 * @param request
	 *            the context-injected {@code HttpServletRequest}
	 * @return a {@code Response} with the marshalled projects
	 */
	@POST
	@Path("/issue")
	@AnonymousAllowed
	@Consumes("application/x-www-form-urlencoded")
	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response createIssue(@Context UriInfo uriInfo, @Context HttpServletRequest request, @FormParam("stacktrace") String stacktrace, 
		@FormParam("app_name") String appName, @FormParam("issue_type") String issueType, @FormParam("priority") String priority,
		@FormParam("summary") String summary, @FormParam("description") String description, @FormParam("app_version_name") String appVersionName,
		@FormParam("app_version_code") String appCode, @FormParam("phone_model") String phoneModel, @FormParam("phone_version") String phoneVersion,
		@FormParam("phone_device") String phoneDevice)
	{
		try
		{
			String error = null;
			if(appName == null)
				log.error(error = "Could not post android issue.  Application name parameter 'app_name' does not exist.");
			else if(issueType == null)
				log.error(error = "Could not post android issue.  Issue type parameter 'issue_type' was not supplied in the post.");
			else if(summary == null)
				log.error(error = "Could not post android issue.  Issue summary parameter 'summary' was not supplied in the post.");
	
			CreateResponse response = null;
			
			if(error == null)
			{
				String issueId = translateIssueType(issueType);
				if(issueId != null)
				{
					Project project = projMgr.getProjectObjByName(appName);
					if(project != null)
					{
						Issue existingIssue = null;						
						CustomField hashField = fieldMgr.getCustomFieldObjectByName("Hash");
						String hash = null;

						if("bug".equals(issueType) && stacktrace != null)
						{
							if(hashField != null)
							{
								log.debug("Hash field exists for project '" + project.getKey() + "'.  Commencing with hash lookup.");
								hash = createHash(stacktrace, project);													
								existingIssue = searchByHash("Hash", hash);
							}
							else
								log.debug("No hash field 'Hash' defined for project '" + project.getKey() + "'.  Skipping hash lookup.");
						}
						else
							log.debug("Issue is not a bug or does not include a stacktrace.  Skipping hash search.");
						
						if(existingIssue == null)
						{
							log.error("Creating issue.");
							MutableIssue issueObject = issueFactory.getIssue();
							issueObject.setProject(project.getGenericValue());
							issueObject.setIssueType(constMgr.getIssueTypeObject(issueId).getGenericValue());
							issueObject.setSummary(summary);
							
							StringBuilder envBuf = createEnvValue(phoneModel, phoneVersion, phoneDevice);
							issueObject.setEnvironment(envBuf.toString());
							if(description != null)
								issueObject.setDescription(description);
							Priority p = constMgr.getPriorityObject(translatePriority(priority));
							if(p != null)
								issueObject.setPriority(p.getGenericValue());
							Version version = verMgr.getVersion(project.getId(), appVersionName);
							if(version != null)
								issueObject.setAffectedVersions(EasyList.build(version));
		
							if(stacktrace != null)
							{
								CustomField straceField = fieldMgr.getCustomFieldObjectByName("Stacktrace");
								if(straceField != null)
									issueObject.setCustomFieldValue(straceField, stacktrace);
								else
									log.debug("No '" + "Stacktrace" + "' field specified for gnoring stacktrace information");
							}
							
							if(hashField != null)
							{
								if(hash != null)
									issueObject.setCustomFieldValue(hashField, new String(hash));
							}
							
							Map<String, Object> params = new HashMap<String, Object>();
							params.put("issue", issueObject);
							GenericValue issue = issueMgr.createIssue(authCtx.getUser(), params);
							issueObject.setKey(issue.getString("key"));
							IssueDetail detail = new IssueDetail(issueObject, stacktrace, hash);
							response = new CreateResponse(detail, true);
							URI issueUri = uriInfo.getAbsolutePathBuilder().path(issueObject.getKey()).build();
							log.error("Created issue:  " + issueUri.toASCIIString());
							
							return Response.created(issueUri).entity(response).build();
						}
						else
						{
							log.debug("Issue already exists.  Ignoring.");
//							MutableIssue issue = issueMgr.getIssueObject(existingIssue.getId());
//							StringBuilder comment = new StringBuilder();
//							final String EOL = System.getProperty("line.separator");
//							comment.append(createEnvValue(phoneModel, phoneVersion, phoneDevice)).append(EOL);
//							comment.append(EOL);
//							comment.append(summary).append(EOL);
//							comment.append(EOL);
//							if(description != null)
//								comment.append(description).append(EOL);
//							String name = null;
//							if(authCtx.getUser() != null)
//								name = authCtx.getUser().getName();
//							commentMgr.create(issue, name, comment.toString(), true);

							IssueDetail detail = new IssueDetail(existingIssue, stacktrace, hash);
							response = new CreateResponse(detail, false);

							return Response.ok(response).build();							
						}
					}
					else
						log.error(error = "Could not post android issue.  Project named '" + appName + "' does not exist.");
				}
				else
					log.error(error = "Could not post android issue.  Could not decode issue type '" + issueType + "'.");

//			issueObject.setFixVersions(EasyList.build(verMgr.getVersion(new Long(10002))));
//			issueObject.setComponents(EasyList.build(projMgr.getComponent(new Long(10000)), projMgr.getComponent(new Long(10001))));
				
			}

			response = new CreateResponse(error);
			return Response.ok(response).status(500).build();
		}
		catch (SearchException se)
		{
			log.error("Error while searching by hash.", se);
			return Response.serverError().build();
		}
		catch (CreateException e)
		{
			log.error(e);
			return Response.serverError().build();
		}
		catch (DataAccessException e)
		{
			log.error(e);
			return Response.serverError().build();
		}
	}

	private StringBuilder createEnvValue(String phoneModel, String phoneVersion, String phoneDevice)
	{
		StringBuilder envBuf = new StringBuilder();
		envBuf.append("Phone - Device:  ").append(phoneDevice).append(", Model:  ")
		      .append(phoneModel).append(", SDK:  ").append(phoneVersion);
		return envBuf;
	}
	
	private String createHash(String stacktrace, Project project)
	{
		String hash = null;
		String projectKey = project.getName();
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA");

			try
			{
				md.update(projectKey.getBytes("UTF8"));
				md.update(stacktrace.getBytes("UTF8"));
				byte[] hashBytes = md.digest();
				String hex = new BigInteger(1, hashBytes).toString(16);
				if(hex.length() % 2 != 0)
					hex = "0" + hex;
				hash = new String(hex);
				log.error("Creating hash:  '" + hash + "'.");
				log.error("Questions?  " + hash.contains("?"));
			}
			catch (UnsupportedEncodingException e)
			{
				log.error("Error generating bug hash using UTF8 encoding.", e);
			}
		}
		catch (NoSuchAlgorithmException e)
		{
			log.error("Error generating bug hash.  Failed to find SHA digest.", e);
		}
		return hash;
	}

	private List<Issue> searchByVersions(int found, int start, int pageSize, String projectName, List<String> affectedVersions)
		throws SearchException
	{
		if(affectedVersions == null)
			return Collections.emptyList();
		
		if(start + pageSize <= found)
			return Collections.emptyList();
		
		JqlClauseBuilder builder = JqlQueryBuilder.newClauseBuilder();
		builder = builder.project(projectName);
		if(!affectedVersions.contains("all"))
			builder.and().addStringCondition("affectedVersion", Operator.IN, affectedVersions).buildQuery();
		Query query = builder.buildQuery();
		PagerFilter pager = new PagerFilter(pageSize - found + 1);
		pager.setStart(start);
		SearchResults searchResults = srchMgr.search(query, authCtx.getUser(), pager);
		log.error("Search returned " + searchResults.getIssues().size() + " issues.");
		return searchResults.getIssues();		
	}

	private Issue searchByHash(String keyName, String hash)
		throws SearchException
	{
		Issue retVal = null;
		if(hash != null)
		{
			JqlClauseBuilder builder = JqlQueryBuilder.newClauseBuilder();
			Query query = builder.addCondition(keyName, Operator.LIKE, new SingleValueOperand(hash)).buildQuery();
			SearchResults searchResults = srchMgr.search(query, authCtx.getUser(), new PagerFilter(1));
			if(searchResults.getTotal() > 0)
			{
				retVal = searchResults.getIssues().get(0);
				log.debug("Issue exists for hash.");
			}
			else
				log.debug("No bug found for hash.");
		}
		
		return retVal;
	}
	
	private String translatePriority(String priority)
	{
		if(priority.equals("blocker"))
			return "1";
		else if(priority.equals("critical"))
			return "2";
		else if(priority.equals("major"))
			return "3";
		else if(priority.equals("minor"))
			return "4";
		else if(priority.equals("trivial"))
			return "5";
		else
			return null;
	}

	private String translateIssueType(String issueType)
	{
		if(issueType.equals("bug"))
			return "1";
		else if(issueType.equals("feature"))
			return "2";
		else if(issueType.equals("task"))
			return "3";
		else if(issueType.equals("improvement"))
			return "4";
		else
			return null;
	}
	
}