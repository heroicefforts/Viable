package net.heroicefforts.viable.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
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
	
	private Issue ctx;
	private RepositoryFactory factory;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.bug_report_activity);
        
    	factory = new RepositoryFactory(this);
    	
        ((Button) findViewById(R.id.ReportBugButton)).setOnClickListener(clickListener);
        ((Button) findViewById(R.id.BugSearchButton)).setOnClickListener(clickListener);
        ((Button) findViewById(R.id.DetailsButton)).setOnClickListener(clickListener);
    }    
    
    private Repository getRemoteRepository()
    {
    	return factory.getRepository(ctx.getAppName());
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
    		
    		Issue issue = getRemoteRepository().exists(ctx);
        	if(issue != null)
        	{
        		Cursor cur = this.getContentResolver().query(Issues.CONTENT_URI, new String[] { Issues.APP_NAME, Issues.ISSUE_ID, Issues.SUMMARY }, Issues.APP_NAME + " = ? and " + Issues.ISSUE_ID + " = ?", new String[] { ctx.getAppName(), issue.getIssueId() }, Issues.DEFAULT_SORT_ORDER);
        		if(cur.moveToFirst())
        		{
        			Log.d(TAG, "The user has already encountered issue '" + issue.getIssueId() + "'.  Closing the activity.");
            		this.finish();
        		}
        		else
        		{
        			Log.d(TAG, "Prompting user to comment on existing issue '" + issue.getIssueId() + "'.");
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.bug_comment);
                    builder.setCancelable(true);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        	finish();
                        }
                    });
                    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        public void onCancel(DialogInterface dialog) {
                        	finish();
                        }
                    });
                    builder.setMessage(R.string.bug_comment_prompt);
                    builder.create().show();        			
        		}
        	}    		
    	}
    	else
    	{
			ctx = getSelfContext();
    		((Button) findViewById(R.id.DetailsButton)).setVisibility(View.INVISIBLE);
    	}
    	    	
    	Spinner typeSpinner = (Spinner) findViewById(R.id.IssueTypeSpinner);        
		typeSpinner.setAdapter(new IssueSelectionAdapter(this, ctx.getStacktrace() != null));

    	Spinner appNameSpinner = (Spinner) findViewById(R.id.AppNameSpinner);
		
//		if(ctx.getStacktrace() != null) 
//		{
		List<String> appNames = new ArrayList<String>(factory.getApplicationNames());
    	appNameSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, appNames.toArray(new String[appNames.size()])));
    	
    	if(ctx != null)
    	{
    		typeSpinner.setSelection(appNames.indexOf(ctx.getAppName()));
    		if(ctx.getStacktrace() != null)
    	    	appNameSpinner.setEnabled(false);
    	}
//		}
//		else
//		{
//	    	Cursor appNameCursor = managedQuery(Issues.APP_CONTENT_URI, new String[] { Issues._ID, Issues.APP_NAME }, null, null, null); 
//			appNameSpinner.setAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, appNameCursor, new String[] { Issues.APP_NAME }, new int[] { android.R.id.text1 }));			
//		}

    }
 
    protected String formatDetails(Issue issue)
    {
    	final String EOL = System.getProperty("line.separator");
    	StringBuilder buf = new StringBuilder();
    	buf.append(getString(R.string.app_label)).append(issue.getAppName()).append(EOL);
    	if(issue instanceof BugContext)
    	{
    		BugContext ctx = (BugContext) issue;
    		//TODO inline
	    	buf.append(getString(R.string.pkg_name)).append(ctx.getPkgName()).append(EOL);
	    	buf.append(getString(R.string.version_name)).append(ctx.getVersionName()).append(EOL);
	    	buf.append(getString(R.string.version_code)).append(ctx.getVersionCode()).append(EOL);
	    	buf.append(getString(R.string.phone_model)).append(ctx.getPhoneModel()).append(EOL);
	    	buf.append(getString(R.string.phone_device)).append(ctx.getPhoneDevice()).append(EOL);
	    	buf.append(getString(R.string.phone_sdk)).append(ctx.getPhoneVersion()).append(EOL);
    	}
    	buf.append(getString(R.string.error)).append(issue.getStacktrace());
    	
    	return buf.toString();
    }
    
	private OnClickListener clickListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			if(v.getId() == R.id.ReportBugButton)
			{
				TextView summaryText = (TextView) findViewById(R.id.BugSummaryEditText);
				String appName = getAppName();
				String summary = summaryText.getText().toString();
		        String desc = ((TextView) findViewById(R.id.BugDescriptionEditText)).getText().toString();

		        if(TextUtils.isEmpty(summary))
		        {
					summaryText.requestFocus();
					return;
		        }

		        try {  
			        int position = ((Spinner) findViewById(R.id.IssueTypeSpinner)).getLastVisiblePosition();
			        IssueState state = (IssueState) ((Spinner) findViewById(R.id.IssueTypeSpinner)).getAdapter().getItem(position);
			        ctx.setState(state);
			        ctx.setAppName(appName);
			        ctx.setSummary(summary);
			        ctx.setDescription(desc);
		        	ctx.setAffectedVersions(new String[] { factory.getApplicationVersion(appName) });
			        
					int responseCode = getRemoteRepository().postIssue(ctx);  
			        
			        if(HttpStatus.SC_CREATED == responseCode || HttpStatus.SC_OK == responseCode)
			        {			
			        	ContentValues values = ctx.getContentValues();
			        	BugReporterActivity.this.getContentResolver().insert(Issues.CONTENT_URI, values);
			        }
			          
			    } 
			    catch (JSONException e)
			    {
			    	Log.e(TAG, "Error parsing modify response.", e);
			    }
			    catch (ClientProtocolException e) 
			    {
			    	Log.e(TAG, "Error posting issue.", e);
			    } 
			    catch (IOException e) 
			    {  
			    	Log.e(TAG, "Error posting issue.", e);
			    }
				
//				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://bugs.heroicefforts.net/secure/CreateIssueDetails!init.jspa?pid=10000&issuetype=1")));
			}
			else if(v.getId() == R.id.BugSearchButton)
			{
				Intent intent = new Intent(BugReporterActivity.this, IssuesList.class);
				startActivity(intent);

//				String name = "Joe Test";
//				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://bugs.heroicefforts.net/sr/jira.issueviews:searchrequest-printable/temp/SearchRequest.html?jqlQuery=project%3D%22OUTLAST%22+AND+name~\"" + name + "\"&tempMax=1000")));
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

	private String getAppName()
	{
		Spinner s = (Spinner) findViewById(R.id.AppNameSpinner);
		Adapter a = s.getAdapter();
		int position = s.getLastVisiblePosition();
		if(a instanceof CursorAdapter)
		{
			Cursor c = ((CursorAdapter)a).getCursor();
			return c.getString(c.getColumnIndex(Issues.APP_NAME));
		}
		else
			return (String) a.getItem(position);
	}		
	
	private BugContext getSelfContext()
	{
		BugContext ctx = new BugContext();
		ctx.setAppData(BugReporterActivity.this);
		return ctx;
	}
	
}
