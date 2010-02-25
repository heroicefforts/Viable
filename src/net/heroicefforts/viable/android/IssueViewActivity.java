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

import java.text.DateFormat;

import net.heroicefforts.viable.android.content.Comments;
import net.heroicefforts.viable.android.content.IssueContentAdapter;
import net.heroicefforts.viable.android.content.Issues;
import net.heroicefforts.viable.android.content.RemoteCommentCursor;
import net.heroicefforts.viable.android.dao.Issue;
import net.heroicefforts.viable.android.rep.CreateException;
import net.heroicefforts.viable.android.rep.IssueResource;
import net.heroicefforts.viable.android.rep.Repository;
import net.heroicefforts.viable.android.rep.RepositoryFactory;
import net.heroicefforts.viable.android.rep.ServiceException;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * This activity displays the Issue details source from either the local content repository or the issue application's remote repository.
 * 
 * @author jevans
 *
 */
public class IssueViewActivity extends Activity
{
	private static final String TAG = "IssueViewActivity";
	
	/**
	 * Supply the name of the application to which the issue id belongs.
	 */
	public static final String EXTRA_APP_NAME = Issues.APP_NAME;
	/**
	 * Supply the id of the issue that should be retrieve from the remote repository.
	 */
	public static String EXTRA_ISSUE_ID = Issues.ISSUE_ID;
	
	private String appName;
	private RepositoryFactory factory;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.issue_view);
        
    	try
		{
    		factory = new RepositoryFactory(this);        

    		if(getIntent().getData() != null)
	        {
	        	Cursor cursor = managedQuery(getIntent().getData(), Issues.ISSUE_PROJECTION, null, null, Issues.DEFAULT_SORT_ORDER);
	            if(cursor.moveToFirst())
	            {
	            	Issue issue = new IssueContentAdapter(cursor).toIssue();
	            	appName = issue.getAppName();
	            	presentIssue(issue);
	            }
	            else
	            {
	            	Log.e(TAG, "Invalid intent data.  Closing activity.");
	            	finish();
	            }
	        }
	        else if(getIntent().getStringExtra(EXTRA_ISSUE_ID) != null)
	        {
	        	String issueId = getIntent().getStringExtra(EXTRA_ISSUE_ID);
	        	appName = getIntent().getStringExtra(EXTRA_APP_NAME);
				Issue issue = factory.getRepository(appName).findById(issueId);
				if(issue != null)
					presentIssue(issue);
				else
				{
					Log.e(TAG, "Issue '" + issueId + "' does not exist.  Closing activity.");
					finish();
				}
	        }
	        else
	        {
	        	Log.e(TAG, "No intent data.  Closing activity.");
	        	finish();        	
	        }
	        
	        ImageButton refreshButton = (ImageButton) findViewById(R.id.RefreshButton);
	        refreshButton.setOnClickListener(refreshClicked);
	        refreshButton.setImageResource(android.R.drawable.ic_popup_sync);
		}
		catch (CreateException e)
		{
			Error.handle(this, e);
			finish();
		}
		catch (ServiceException e)
		{
			Error.handle(this, e);
			finish();
		}	        
    }

    /**
     * Handler for menu refresh.  Pulls the latest info from the remote repository and updates the local content store.
     */
    private OnClickListener refreshClicked = new OnClickListener()
    {
		public void onClick(View v)
		{
			String issueId = getIssueId();
			try
			{
				Issue issue = factory.getRepository(appName).findById(issueId);
				if(issue != null)
				{
					presentIssue(issue);			
					getContentResolver().update(Issues.CONTENT_URI, new IssueContentAdapter(issue).toContentValues(), Issues.ISSUE_ID + " = ?", new String[] { issueId });
				}
			}
			catch (CreateException e)
			{
				Error.handle(IssueViewActivity.this, e);
			}
			catch (ServiceException e)
			{
				Error.handle(IssueViewActivity.this, e);
			}
		}
    	
    };

	private String getIssueId()
	{
		return ((TextView) findViewById(R.id.IssueIdTextView)).getText().toString();
	}
    
    /**
     * Binds the issue data to the view
     * @param issue the issue to display
     * @throws ServiceException if there is an error connecting to the remote repository configuration.
     */
    private void presentIssue(Issue issue) 
    	throws ServiceException
	{
    	IssueResource issueState = factory.getRepository(issue.getAppName()).getState(issue.getType(), issue.getPriority(), issue.getState());
		TextView issueView = (TextView) findViewById(R.id.IssueIdTextView);
		issueView.setText(issue.getIssueId());
		issueView.setTextColor(Color.WHITE);
		((ImageView) findViewById(R.id.TypeImageView)).setImageDrawable(issueState.getIcon(this));
		
		TextView typeView = (TextView) findViewById(R.id.TypeTextView);
		typeView.setText(issueState.getTypeText(issue));
		
		TextView priorityView = (TextView) findViewById(R.id.PriorityTextView);
		priorityView.setText(issueState.getPriorityText(issue));

		TextView stateView = (TextView) findViewById(R.id.StateTextView);
		stateView.setText(issueState.getStateText(issue));
		
		SpannableStringBuilder affectedVersions = new SpannableStringBuilder();
		affectedVersions.append(getString(R.string.affected_versions_label));
		String currVersion = factory.getApplicationVersion(issue.getAppName());
		Log.d(TAG, "Current version:  " + currVersion);
		for(String version : issue.getAffectedVersions())
		{
			if(version.equals(currVersion))
			{
				int idxStart = affectedVersions.length();
				affectedVersions.append(version);
				affectedVersions.setSpan(new ForegroundColorSpan(Color.RED), idxStart, affectedVersions.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				affectedVersions.append(", ");
			}
			else
				affectedVersions.append(version).append(", ");
		}
		affectedVersions.delete(affectedVersions.length() - 2, affectedVersions.length() - 1);
		
		((TextView) findViewById(R.id.AffectedVersionsTextView)).setText(affectedVersions);

		DateFormat fmt = DateFormat.getDateInstance();
		((TextView) findViewById(R.id.CreatedDateTextView)).setText(getString(R.string.created_date_label) + fmt.format(issue.getCreateDate()));
		((TextView) findViewById(R.id.ModifiedDateTextView)).setText(getString(R.string.modified_date_label) + fmt.format(issue.getModifiedDate()));
		((TextView) findViewById(R.id.SummaryTextView)).setText(issue.getSummary());
		((TextView) findViewById(R.id.DescriptonTextView)).setText(issue.getDescription());
		
		RemoteCommentCursor cursor = new RemoteCommentCursor(factory.getRepository(issue.getAppName()), issue.getIssueId());
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.comments_list_item, cursor,
		        new String[] { Comments.AUTHOR, Comments.BODY, Comments.CREATED_DATE }, 
		        new int[] { R.id.AuthorTextView, R.id.BodyTextView, R.id.CreateDateTextView });
		((ListView) findViewById(R.id.CommentsListView)).setAdapter(adapter);
		
		Cursor c = getContentResolver().query(Issues.CONTENT_URI, Issues.ISSUE_PROJECTION, Issues.ISSUE_ID + " = ?", new String[] { issue.getIssueId() }, null);
		Boolean voted = null;
		if(c.moveToFirst())
		{
			Issue localIssue = new IssueContentAdapter(c).toIssue();
			voted = localIssue.isVoted();
		}
		
		if(voted == null)
			voted = issue.isVoted();
		
		ImageView iv = (ImageView) findViewById(R.id.VotedImageView);
		if(voted)
			iv.setImageResource(android.R.drawable.star_big_on);
		else
		{
			iv.setImageResource(android.R.drawable.star_big_off);
			iv.setOnClickListener(starClicked);
		}
		
		TextView votesView = (TextView) findViewById(R.id.VotesTextView);
		votesView.setText(String.valueOf(issue.getVotes()));
		

		((TextView) findViewById(R.id.VotesTextView)).setText(String.valueOf(issue.getVotes()));

	}    

	private OnClickListener starClicked = new OnClickListener() {

		public void onClick(View v)
		{
			new AsyncVoteTask().execute(((TextView) findViewById(R.id.IssueIdTextView)).getText().toString());
		}
		
	};

	/**
     * Results class for returning asynchronous result values.
     */
	private class VoteHolder
	{
		public Issue issue;
		public Boolean success;
		public ServiceException exc;
	}
    
	public class AsyncVoteTask extends AsyncTask<String, Void, VoteHolder>
	{

		@Override
		protected VoteHolder doInBackground(String...args)
		{
			VoteHolder holder = new VoteHolder();
			try
			{
				String issueId = args[0];
				Repository rep = factory.getRepository(appName);
				holder.issue = rep.findById(issueId);
				holder.success = rep.vote(holder.issue);
			}
			catch (ServiceException e)
			{
				holder.exc = e;
			}
			
			return holder;
		}

		@Override
		protected void onPostExecute(VoteHolder holder)
		{
			if(holder.exc == null)
			{
				String issueId = IssueViewActivity.this.getIssueId();
				if(holder.success && issueId.equals(holder.issue.getIssueId()))
				{
					updateIssue(holder.issue);

					TextView votesView = ((TextView) findViewById(R.id.VotesTextView));
					votesView.setText(String.valueOf(holder.issue.getVotes()));
					ImageView iv = ((ImageView) findViewById(R.id.VotedImageView));
					iv.setImageResource(android.R.drawable.star_big_on);
					iv.setOnClickListener(null);
				}
			}
			else
				Error.handle(IssueViewActivity.this, holder.exc);
		}

	}

	private void updateIssue(Issue issue)
	{
		ContentValues values = new IssueContentAdapter(issue).toContentValues();
		getContentResolver().update(Issues.CONTENT_URI, values, Issues.ISSUE_ID + " = ?", new String[] { issue.getIssueId() });			
	}		
	
}
