package net.heroicefforts.viable.android;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;


public interface Repository
{

	public abstract Issue exists(Issue issue);

	public abstract Issue findById(String issueId);

	public abstract SearchResults search(SearchParams params);

	public abstract int postIssue(Issue issue) throws UnsupportedEncodingException, IOException,
			ClientProtocolException, JSONException;

    public CommentSet findCommentsForIssue(String issueId, int page, int pageSize);
    
    public ProjectDetail getApplicationStats(String appName);
    
}