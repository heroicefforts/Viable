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
package net.heroicefforts.viable.android;

import java.util.ArrayList;
import java.util.List;

import net.heroicefforts.viable.android.dao.Comment;
import net.heroicefforts.viable.android.dao.Issue;
import net.heroicefforts.viable.android.reg.RegEntry;
import net.heroicefforts.viable.android.reg.UninstallIntent;
import net.heroicefforts.viable.android.rep.Repository;
import net.heroicefforts.viable.android.rep.RepositoryFactory;
import net.heroicefforts.viable.android.rep.ServiceException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * This Activity class handles user uninstallation comments and submits them to the appropriate issue tracker belonging to the
 * uninstalled application.  Comments are accumulated under one well-defined issue.
 * 
 * @author jevans
 *
 */
public class UninstallActivity extends Activity
{
	private static final String TAG = "BugReporterActivity";
	private static final String EOL = System.getProperty("line.separator");
	
	private Spinner typeSpinner;
	private Spinner appNameSpinner;
	private TextView summaryText;
	private TextView descriptionText;
	private Button detailsButton;
	private View detailsScroll;
	
	private Repository repository;
	private Issue issue;
	private RegEntry entry;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        UninstallIntent uninstall = new UninstallIntent(getIntent());
        if(!uninstall.isValid())
        {
        	Log.e(TAG, "Uninstall activity did not contain sufficient intent data.  Was it invoked from the uninstall receiver?");
        	finish();
        }

        entry = uninstall.getAppEntry();
        repository = new RepositoryFactory(this).instantiateRepository(entry);
		
        try
		{
			initializeIssue();
		}
		catch (ServiceException e)
		{
			Log.e(TAG, "Error contacting repository.  Will not prompt for uninstall comments.");
			finish();
		}    		
        
        setContentView(R.layout.bug_report_activity);
            	
        ((Button) findViewById(R.id.ReportBugButton)).setOnClickListener(clickListener);
        
        appNameSpinner = (Spinner) findViewById(R.id.AppNameSpinner);    	
    	typeSpinner = (Spinner) findViewById(R.id.IssueTypeSpinner); 
		summaryText = (TextView) findViewById(R.id.BugSummaryEditText);
		descriptionText = (TextView) findViewById(R.id.BugDescriptionEditText);
        detailsButton = (Button) findViewById(R.id.DetailsButton);
        detailsScroll = (View) findViewById(R.id.BugDetailsScrollView);
        
		List<String> appNames = new ArrayList<String>();
		appNames.add(entry.getAppName());
    	appNameSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, appNames));
    	appNameSpinner.setEnabled(false);
    	
    	typeSpinner.setVisibility(View.GONE);    	
		detailsButton.setVisibility(View.GONE);
		detailsScroll.setVisibility(View.GONE);
    }

    @Override
    public void onStart()
    {
    	super.onStart();
    	
        promptToComment();    	
    }

	private void initializeIssue() 
		throws ServiceException
	{
		issue = new Issue();
		issue.setAppName(entry.getAppName());
		issue.setStacktrace("uninstall");
		issue.setAffectedVersions(new String[] { entry.getVersionName() });
		Issue existant = repository.exists(issue);
    	if(existant != null) //issue recorded remotely
    		issue = existant;    	
	}        
    	
	/**
	 * Prompts the user to submit an uninstallation reason.
	 */
	private void promptToComment()
	{
		if(Config.LOGD)
			Log.d(TAG, "Prompting user to comment on uninstall of '" + entry.getAppName() + "'.");
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.uninstall_comment);
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
		builder.setMessage(getString(R.string.uninstall_comment_prompt).replaceAll("\\{appName\\}", entry.getAppName()));
		builder.create().show();
	}
 
	private OnClickListener clickListener = new OnClickListener()
	{

		public void onClick(View v)
		{
			if(v.getId() == R.id.ReportBugButton)
			{
				String summary = summaryText.getText().toString();
		        String desc = descriptionText.getText().toString();

		        if(TextUtils.isEmpty(summary))
		        {
					summaryText.requestFocus();
					return;
		        }

		        if(issue.getIssueId() != null)
		        	reportComment(summary, desc);
		        else
		        {
		        	issue.setSummary(getString(R.string.uninstall_reasons));
		        	issue.setDescription(summary + EOL + EOL + desc);
		        	reportBug(issue);
		        }			        
			}
		}

	};

	private void reportComment(String summary, String desc)
	{			
		Comment comment = new Comment(summary + EOL + EOL + desc);
		new ReportCommentTask().execute(comment);
	}

	private void reportBug(Issue issue)
	{		
		new ReportBugTask().execute(issue);  						
	}

	/**
	 * Asynchronous task for submitting the issue comment to the remote repository. 
	 */
	private class ReportCommentTask extends AsyncTask<Comment, Void, ServiceException>
	{
		@Override
		protected ServiceException doInBackground(Comment... params)
		{
			try
			{
				Comment comment = params[0];
				repository.postIssueComment(issue, comment);
				return null;
			}
			catch (ServiceException e)
			{
				return e;
			}			
		}
		
		protected void onPostExecute(ServiceException e)
		{
			if(e == null)
				finish();
			else
				Error.handle(UninstallActivity.this, e);				
		}
		
	}	
	
	/**
	 * Asynchronous task for submitting the issue to the remote repository. 
	 */
	private class ReportBugTask extends AsyncTask<Issue, Void, ServiceException> 
	{
		@Override
		protected ServiceException doInBackground(Issue... params)
		{
			try {
				Issue curIssue = params[0];
				repository.postIssue(curIssue);				
				return null;
			}
			catch(ServiceException e)
			{
				return e;
			}
		}

		protected void onPostExecute(ServiceException e)
		{
			if(e == null)
				finish();				
			else
				Error.handle(UninstallActivity.this, e);				
		}

	}
	
}
