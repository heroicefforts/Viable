package net.heroicefforts.viable.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log; 
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class BugReporterActivity extends Activity
{
protected static final String TAG = "BugReporterActivity";
//Bug - http://bugs.heroicefforts.net/secure/CreateIssueDetails!init.jspa?pid=10000&issuetype=1&customfield_10001=XXXXX
//New Feature - http://bugs.heroicefforts.net/secure/CreateIssueDetails!init.jspa?pid=10000&issuetype=2&customfield_10001=XXXXX
//Task - http://bugs.heroicefforts.net/secure/CreateIssueDetails!init.jspa?pid=10000&issuetype=3&customfield_10001=XXXXX
//Improvement - http://bugs.heroicefforts.net/secure/CreateIssueDetails!init.jspa?pid=10000&issuetype=4&customfield_10001=XXXXX

//search http://bugs.heroicefforts.net/sr/jira.issueviews:searchrequest-printable/temp/SearchRequest.html?jqlQuery=project%3D%22OUTLAST%22+AND+name~"XXXXXX"&tempMax=1000
	
	private BugContext ctx;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.bug_report_activity);
        
        ((Button) findViewById(R.id.ReportBugButton)).setOnClickListener(clickListener);
        ((Button) findViewById(R.id.BugSearchButton)).setOnClickListener(clickListener);
        ((Button) findViewById(R.id.DetailsButton)).setOnClickListener(clickListener);
    }    
    
    @Override
    public void onStart()
    {
    	super.onStart();
    	
    	ctx = BugContext.load(getIntent());
    	if(ctx != null)
    	{
    		((TextView) findViewById(R.id.BugDetailsTextView)).setText(formatDetails(ctx));
    		((Button) findViewById(R.id.DetailsButton)).setVisibility(View.VISIBLE);
    	}
    	else
    	{
			ctx = getSelfContext();
    		((Button) findViewById(R.id.DetailsButton)).setVisibility(View.INVISIBLE);
    	}
    	
    	((TextView) findViewById(R.id.AppNameTextView)).setText(ctx.getAppName());
    	
    	Spinner typeSpinner = loadSpinner(ctx, R.id.IssueTypeSpinner, R.array.issue_type_value);
        if(ctx.getStacktrace() != null)
        	typeSpinner.setEnabled(false);

    	Spinner prioritySpinner = loadSpinner(ctx, R.id.IssuePrioritySpinner, R.array.issue_priority_value);
    	prioritySpinner.setSelection(2);
    }
 
    private Spinner loadSpinner(BugContext ctx, int spinnerId, int valuesId)
    {
        Spinner s = (Spinner) findViewById(spinnerId);        
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, valuesId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        return s;
    }
    
    protected String formatDetails(BugContext ctx)
    {
    	final String EOL = System.getProperty("line.separator");
    	StringBuilder buf = new StringBuilder();
    	buf.append(getString(R.string.app_label)).append(ctx.getAppName()).append(EOL);
    	buf.append(getString(R.string.pkg_name)).append(ctx.getPkgName()).append(EOL);
    	buf.append(getString(R.string.version_name)).append(ctx.getVersionName()).append(EOL);
    	buf.append(getString(R.string.version_code)).append(ctx.getVersionCode()).append(EOL);
    	buf.append(getString(R.string.phone_model)).append(ctx.getPhoneModel()).append(EOL);
    	buf.append(getString(R.string.phone_device)).append(ctx.getPhoneDevice()).append(EOL);
    	buf.append(getString(R.string.phone_sdk)).append(ctx.getPhoneVersion()).append(EOL);
    	buf.append(getString(R.string.error)).append(ctx.getStacktrace());
    	
    	return buf.toString();
    }
    
	private OnClickListener clickListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			if(v.getId() == R.id.ReportBugButton)
			{
				TextView summaryText = (TextView) findViewById(R.id.BugSummaryEditText);
				String summary = (summaryText).getText().toString();
				if(summary != null && summary.trim().length() > 0)
				{
					HttpClient httpclient = new DefaultHttpClient();  
					HttpPost post = new HttpPost("http://192.168.1.101:2990/jira/rest/tutorial-rest/1.0/android-issue/create");
				  
				    try {  
				        // Add your data  
				        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				        
				        int position = ((Spinner) findViewById(R.id.IssueTypeSpinner)).getLastVisiblePosition();
				        String issueType = BugReporterActivity.this.getResources().getStringArray(R.array.issue_type_key)[position];
				        nameValuePairs.add(new BasicNameValuePair("issue_type", issueType));

				        int priorityPosition = ((Spinner) findViewById(R.id.IssuePrioritySpinner)).getLastVisiblePosition();
				        String issuePriority = BugReporterActivity.this.getResources().getStringArray(R.array.issue_priority_key)[priorityPosition];
				        nameValuePairs.add(new BasicNameValuePair("priority", issuePriority));
				        
				        nameValuePairs.add(new BasicNameValuePair("app_name", ctx.getAppName()));
				        nameValuePairs.add(new BasicNameValuePair("app_version_name", ctx.getVersionName()));
				        if(ctx.getStacktrace() != null)
				        	nameValuePairs.add(new BasicNameValuePair("stacktrace", ctx.getStacktrace()));
				        nameValuePairs.add(new BasicNameValuePair("phone_model", ctx.getPhoneModel()));
				        nameValuePairs.add(new BasicNameValuePair("phone_device", ctx.getPhoneDevice()));
				        nameValuePairs.add(new BasicNameValuePair("phone_version", String.valueOf(ctx.getPhoneVersion())));
				        nameValuePairs.add(new BasicNameValuePair("summary", ((TextView) findViewById(R.id.BugSummaryEditText)).getText().toString()));
				        nameValuePairs.add(new BasicNameValuePair("description", ((TextView) findViewById(R.id.BugDescriptionEditText)).getText().toString()));
				        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
				   
				        // Execute HTTP Post Request  
				        HttpResponse response = httpclient.execute(post);  
				          
				    } 
				    catch (ClientProtocolException e) 
				    {
				    	Log.e(TAG, "Error posting issue.", e);
				    } 
				    catch (IOException e) 
				    {  
				    	Log.e(TAG, "Error posting issue.", e);
				    }
				}
				else
				{
					summaryText.requestFocus();
				}
				
//				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://bugs.heroicefforts.net/secure/CreateIssueDetails!init.jspa?pid=10000&issuetype=1")));
			}
			else if(v.getId() == R.id.BugSearchButton)
			{
				String name = "Joe Test";
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://bugs.heroicefforts.net/sr/jira.issueviews:searchrequest-printable/temp/SearchRequest.html?jqlQuery=project%3D%22OUTLAST%22+AND+name~\"" + name + "\"&tempMax=1000")));
			}
			else if(v.getId() == R.id.DetailsButton)
			{
				Button button = (Button) v;
				View layout = (View) findViewById(R.id.BugDetailsScrollView);
				if(layout.getVisibility() == View.VISIBLE)
				{
					button.setText(getText(R.string.show_details));
					layout.setVisibility(View.INVISIBLE);
				}
				else
				{
					button.setText(getText(R.string.hide_details));
					layout.setVisibility(View.VISIBLE);
				}
				
			}
		}
		
	};
	
	private BugContext getSelfContext()
	{
		BugContext ctx = new BugContext();
		ctx.setAppData(BugReporterActivity.this);
		return ctx;
	}
	
}
