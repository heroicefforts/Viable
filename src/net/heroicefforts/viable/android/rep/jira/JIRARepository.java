package net.heroicefforts.viable.android.rep.jira;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import net.heroicefforts.viable.android.dao.BugContext;
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

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class JIRARepository implements Repository
{
	private static final String TAG = "JIRARepository";

	private static final int CONN_TIMEOUT = 5000;
	
	private String rootURL;
	private HttpClient httpclient;
	private String appName;  
	


	public JIRARepository(String appName, Activity act, Bundle metaData)
		throws CreateException
	{
		this(appName, metaData.getString("viable-provider-location"));
	}

    public JIRARepository(String appName, String location)
    	throws CreateException
	{
		if(location == null)
			throw new CreateException("No '" + "viable-provider-location" + "' meta-data field defined for application.  JIRA Repository cannot be constructed.");

		this.rootURL = location;
		this.appName = appName;
		httpclient = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), CONN_TIMEOUT);
	}

	/* (non-Javadoc)
	 * @see net.heroicefforts.viable.android.Repository#exists(net.heroicefforts.viable.android.Issue)
	 */
	public Issue exists(Issue issue) 
		throws ServiceException
    {
    	String hash = createHash(issue);
    	
    	SearchParams params = new SearchParams();
    	params.setHash(hash);
    	List<Issue> issues = search(params).getIssues();
    	
    	
    	if(issues.size() > 0)
    	{
    		Log.d(TAG, "Hashed bug exists:  '" + hash + "'");
    		return issues.get(0);
    	}
    	else
    	{
    		Log.d(TAG, "Hashed bug does not exist:  '" + hash + "'");
    		return null;
    	}
    }

	
	public IssueResource getState(String type, String priority, String state)
	{
		return JIRAResourceFlyweight.getState(type, priority, state);
	}
	
	public Set<? extends IssueResource> getDefaultDefectStates()
	{
		return JIRAResourceFlyweight.getDefaultDefectStates();
	}
	
	public Set<? extends IssueResource> getDefaultStates()
	{
		return JIRAResourceFlyweight.getDefaultStates();
	}

	
    public Issue findById(String issueId) 
    	throws ServiceException
    {
		Issue issue = null; 
		SearchParams params = new SearchParams();
		params.setIds(Arrays.asList(new String[] { issueId }));
		List<Issue> issues = search(params).getIssues();
		if(issues.size() > 0)
			issue = issues.get(0);
		
		return issue;    	
    }
    
    public CommentSet findCommentsForIssue(String issueId, int page, int pageSize)
    	throws ServiceException
    {
		try
		{
			StringBuilder url = new StringBuilder();
			url.append(rootURL).append("/issue/").append(issueId).append("/comments");
			if(page > 0)
				url.append("?page=").append(page).append("&pageSize=").append(pageSize);

			HttpGet get = new HttpGet(url.toString());			
			HttpResponse response = execute(get);
			int code = response.getStatusLine().getStatusCode();
			if(HttpStatus.SC_NOT_FOUND == code)
			{
				Log.d(TAG, "No comments for issue '" + issueId + "'.");
				List<Comment> comments = Collections.emptyList();
				return new CommentSet(comments, false);
			}
			else if(HttpStatus.SC_OK == code)
			{
	        	JSONObject obj = readJSON(response);
				Log.d(TAG, "Comments JSON:  " + obj.toString(4));
	        	List<Comment> issues = loadComments(obj);
	        	boolean more = obj.getBoolean("more");
				Log.d(TAG, "Searched returned " + issues.size() + " results.");
				return new CommentSet(issues, more);
			}
			else
				throw new ServiceException("Remote error occurred.  Response code was :  " + code);
		}
		catch (JSONException e)
		{
			throw new ServiceException("Error parsing JIRA response.", e);
		}
		catch (IOException e)
		{
			throw new ServiceException("Error connecting to JIRA repository.", e);
		}

    }
    
    /* (non-Javadoc)
	 * @see net.heroicefforts.viable.android.Repository#search(net.heroicefforts.viable.android.SearchParams)
	 */
    public SearchResults search(SearchParams params) 
    	throws ServiceException
    {
		try
		{
			HttpPost post = new HttpPost(rootURL + "/search");		
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			
			if(params.getHash() != null)
				nameValuePairs.add(new BasicNameValuePair("hash", params.getHash()));
			
			if(params.getIds() != null)
				for(String id : params.getIds())
					nameValuePairs.add(new BasicNameValuePair("issue_id", id));
			
			if(params.getAffectedVersions() != null)
				for(String version : params.getAffectedVersions())
					nameValuePairs.add(new BasicNameValuePair("affected_version", version));
			
			if(params.getAppName() != null)
				nameValuePairs.add(new BasicNameValuePair("app_name", params.getAppName()));
			
			if(params.getPage() > 0)
				nameValuePairs.add(new BasicNameValuePair("page", String.valueOf(params.getPage())));
			
			if(params.getPageSize() > 0)
				nameValuePairs.add(new BasicNameValuePair("page_size", String.valueOf(params.getPageSize())));
			
			Log.d(TAG, "Posting search paramters:  " + nameValuePairs.toString());
			
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));  		   
			HttpResponse response = execute(post);
			int code = response.getStatusLine().getStatusCode();
			if(HttpStatus.SC_NOT_FOUND == code)
			{
				Log.d(TAG, "Search returned no results:  '" + params + "'");
				List<Issue> empty = Collections.emptyList();
				return new SearchResults(empty, false);
			}
			else if(HttpStatus.SC_OK == code)
			{
	        	JSONObject obj = readJSON(response);
				Log.d(TAG, "Search JSON:  " + obj.toString(4));
	        	List<Issue> issues = loadIssues(obj);
	        	boolean more = obj.getBoolean("more");
				Log.d(TAG, "Searched returned " + issues.size() + " results:  " + params);
				return new SearchResults(issues, more);
			}
			else
				throw new ServiceException("Remote error occurred.  Response code was :  " + code);
		}
		catch (JSONException e)
		{
			throw new ServiceException("Error parsing JIRA response.", e);
		}
		catch (IOException e)
		{
			throw new ServiceException("Error connecting to JIRA repository.", e);
		}
    }
    
    private List<Issue> loadIssues(JSONObject obj)
    	throws JSONException
    {
    	List<Issue> issues = new ArrayList<Issue>();
    	if(obj.has("issues"))
    	{
    		JSONArray arr = obj.getJSONArray("issues");
    		for(int i = 0; i < arr.length(); i++)
    			issues.add(new Issue(arr.getJSONObject(i).toString(4)));
    	}
    	
    	return issues;
    }
    
    private List<Comment> loadComments(JSONObject obj)
		throws JSONException
	{
		List<Comment> comments = new ArrayList<Comment>();
		if(obj.has("comments"))
		{
			JSONArray arr = obj.getJSONArray("comments");
			for(int i = 0; i < arr.length(); i++)
				comments.add(new Comment(arr.getJSONObject(i)));
		}
		
		return comments;
	}

    public List<VersionDetail> getApplicationVersions() 
    	throws ServiceException
    {
		try
		{
			StringBuilder url = new StringBuilder();
			url.append(rootURL).append("/app/").append(appName).append("/versions");

			HttpGet get = new HttpGet(url.toString());			
			HttpResponse response = execute(get);
			int code = response.getStatusLine().getStatusCode();
			if(HttpStatus.SC_NOT_FOUND == code)
			{
				Log.d(TAG, "No project versions found for '" + appName + "'.");
				return Collections.emptyList();
			}
			else if(HttpStatus.SC_OK == code)
			{
	        	JSONObject obj = readJSON(response);
				Log.d(TAG, "Project stats JSON:  " + obj.toString(4));
				ArrayList<VersionDetail> details = new ArrayList<VersionDetail>();
				if(obj.has("versions"))
				{
					JSONArray vers = obj.getJSONArray("versions");
					for(int i = 0; i < vers.length(); i++)
						details.add(new VersionDetail(vers.getJSONObject(i)));
				}
				
				return details;
			}
			else
				throw new ServiceException("Remote error occurred.  Response code was :  " + code);
		}
		catch (JSONException e)
		{
			throw new ServiceException("Error parsing JIRA response.", e);
		}
		catch (IOException e)
		{
			throw new ServiceException("Error connecting to JIRA repository.", e);
		}
    }
    
    public ProjectDetail getApplicationStats(String appName) 
    	throws ServiceException
    {
		try
		{
			StringBuilder url = new StringBuilder();
			url.append(rootURL).append("/app/").append(appName).append("/stats");

			HttpGet get = new HttpGet(url.toString());			
			HttpResponse response = execute(get);
			int code = response.getStatusLine().getStatusCode();
			if(HttpStatus.SC_NOT_FOUND == code)
			{
				Log.d(TAG, "No project stats found for '" + appName + "'.");
				return null;
			}
			else if(HttpStatus.SC_OK == code)
			{
	        	JSONObject obj = readJSON(response);
				Log.d(TAG, "Project stats JSON:  " + obj.toString(4));
				return new ProjectDetail(obj);
			}
			else
				throw new ServiceException("Remote error occurred.  Response code was :  " + code);
		}
		catch (JSONException e)
		{
			throw new ServiceException("Error parsing JIRA response.", e);
		}
		catch (IOException e)
		{
			throw new ServiceException("Error connecting to JIRA repository.", e);
		}
		//TODO unwrap
    }
    
	/* (non-Javadoc)
	 * @see net.heroicefforts.viable.android.Repository#postIssue(net.heroicefforts.viable.android.Issue)
	 */
	public int postIssue(Issue issue) 
		throws ServiceException
	{
		try
		{
			HttpPost post = new HttpPost(rootURL + "/issue.json");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(12);
			nameValuePairs.add(new BasicNameValuePair("issue_type", issue.getType()));
			nameValuePairs.add(new BasicNameValuePair("priority", issue.getPriority()));				        
			nameValuePairs.add(new BasicNameValuePair("app_name", issue.getAppName()));
			nameValuePairs.add(new BasicNameValuePair("summary", issue.getSummary()));				        
			nameValuePairs.add(new BasicNameValuePair("description", issue.getDescription()));
			for(String version : issue.getAffectedVersions())
				nameValuePairs.add(new BasicNameValuePair("app_version_name", version));
			if(issue.getStacktrace() != null)
				nameValuePairs.add(new BasicNameValuePair("stacktrace", issue.getStacktrace()));

			if(issue instanceof BugContext)
			{
				nameValuePairs.add(new BasicNameValuePair("phone_model", Build.MODEL));
				nameValuePairs.add(new BasicNameValuePair("phone_device", Build.DEVICE));				        
				nameValuePairs.add(new BasicNameValuePair("phone_version", String.valueOf(Build.VERSION.SDK_INT)));
			}

			Log.d(TAG, "post params:  " + nameValuePairs.toString());
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));  

			// Execute HTTP Post Request  
			HttpResponse response = execute(post);
			int responseCode = response.getStatusLine().getStatusCode();
			if(HttpStatus.SC_OK == responseCode || HttpStatus.SC_CREATED == responseCode)
			{			
				JSONObject obj = readJSON(response);
				Log.d(TAG, "postIssue response:  \n" + obj.toString(4));		
				//TODO pull dates from response
				Issue newIssue = new Issue(obj.getJSONObject("issue").toString(4));
				issue.copy(newIssue);
			}
			else
				throw new ServiceException("Remote error occurred.  Response code was :  " + responseCode);
			
			Log.d(TAG, "postIssue code " + responseCode);
			
			return responseCode;
		}
		catch (UnsupportedEncodingException e)
		{
			throw new ServiceException("Encoding error occurred parsing JIRA response", e);
		}
		catch (IOException e)
		{
			throw new ServiceException("Error connecting to JIRA repository.", e);
		}
		catch (JSONException e)
		{
			throw new ServiceException("Error parsing JIRA response.", e);
		}
	}

	
	private String createHash(Issue issue)
	{
		String hash = null;
		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA");

			try
			{
				md.update(issue.getAppName().getBytes("UTF8"));
				md.update(issue.getStacktrace().getBytes("UTF8"));
				byte[] hashBytes = md.digest();
				String hex = new BigInteger(1, hashBytes).toString(16);
				if(hex.length() % 2 != 0)
					hex = "0" + hex;
				hash = new String(hex);
			}
			catch (UnsupportedEncodingException e)
			{
				Log.e(TAG, "Error generating bug hash using UTF8 encoding.", e);
			}
		}
		catch (NoSuchAlgorithmException e)
		{
			Log.e(TAG, "Error generating bug hash.  Failed to find SHA digest.", e);
		}
		return hash;
	}	
	
	private HttpResponse execute(HttpUriRequest request)
		throws IOException
	{
		request.addHeader("Accept-Encoding", "gzip");
		return httpclient.execute(request);
	}
	
	private JSONObject readJSON(HttpResponse response) 
		throws IOException, JSONException
	{
		InputStream instream = response.getEntity().getContent();
		Header contentEncoding = response.getFirstHeader("Content-Encoding");
		if(contentEncoding != null)
			Log.d(TAG, "Response content encoding was '" + contentEncoding.getValue() + "'");
		if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
			Log.d(TAG, "Handling GZIP response.");
		    instream = new GZIPInputStream(instream);
		}		

		BufferedInputStream bis = new BufferedInputStream(instream);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int read = 0;
		while((read = bis.read(buf)) > 0)
			baos.write(buf, 0, read);
		
		JSONObject obj = new JSONObject(baos.toString());
		return obj;
	}

	public int postIssueComment(Issue issue, Comment comment)
		throws ServiceException
	{
		try
		{
			HttpPost post = new HttpPost(rootURL + "/issue/" + issue.getIssueId() + "/comments.json");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(12);
			nameValuePairs.add(new BasicNameValuePair("app_name", issue.getAppName()));
			nameValuePairs.add(new BasicNameValuePair("body", comment.getBody()));				        

			if(issue.getStacktrace() != null)
			{
				nameValuePairs.add(new BasicNameValuePair("phone_model", Build.MODEL));
				nameValuePairs.add(new BasicNameValuePair("phone_device", Build.DEVICE));				        
				nameValuePairs.add(new BasicNameValuePair("phone_version", String.valueOf(Build.VERSION.SDK_INT)));
			}

			Log.d(TAG, "post params:  " + nameValuePairs.toString());
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));  

			// Execute HTTP Post Request  
			HttpResponse response = execute(post);
			int responseCode = response.getStatusLine().getStatusCode();
			if(HttpStatus.SC_OK == responseCode || HttpStatus.SC_CREATED == responseCode)
			{			
				JSONObject obj = readJSON(response);
				Log.d(TAG, "postIssueComment response:  \n" + obj.toString(4));		
				//TODO pull dates from response
				Comment newComment = new Comment(obj);
				comment.copy(newComment);
			}
			else
				throw new ServiceException("Remote error occurred.  Response code was :  " + responseCode);
			
			Log.d(TAG, "postIssueComment code " + responseCode);
			
			return responseCode;
		}
		catch (UnsupportedEncodingException e)
		{
			throw new ServiceException("Encoding error occurred parsing JIRA response", e);
		}
		catch (IOException e)
		{
			throw new ServiceException("Error connecting to JIRA repository.", e);
		}
		catch (JSONException e)
		{
			throw new ServiceException("Error parsing JIRA response.", e);
		}
	}

}