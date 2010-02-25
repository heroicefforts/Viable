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

import java.util.List;

import net.heroicefforts.viable.android.content.Issues;
import net.heroicefforts.viable.android.content.NullCursor;
import net.heroicefforts.viable.android.rep.RepositoryFactory;
import net.heroicefforts.viable.android.rep.ServiceException;
import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * This is the abstract template class for handling the display of a list of Issues.
 * 
 * @author jevans
 *
 */
public abstract class AbstractIssueListActivity extends Activity
{
	protected ListView listView;
	private RepositoryFactory factory;
	private Spinner appNameSpinner;
	private Spinner versionSpinner;
	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
        setContentView(R.layout.issue_list);		
		
        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);
		appNameSpinner = (Spinner) findViewById(R.id.AppNameSpinner);
		
		this.factory = new RepositoryFactory(this);		
		
		List<String> appNames = getAppNames();
    	appNameSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, appNames));
		appNameSpinner.setOnItemSelectedListener(appChosen);    	
    	
		listView = (ListView) findViewById(R.id.IssueListView);
        listView.setOnCreateContextMenuListener(this);
        listView.setOnItemClickListener(getIssueListClickListener());
        
        versionSpinner = (Spinner) findViewById(R.id.AppVersionSpinner);
        versionSpinner.setOnItemSelectedListener(versionChosen);
	}

	/**
	 * Returns a list of application names to display in the application spinner.
	 * 
	 * @return a non-null list of application names.
	 */
	protected abstract List<String> getAppNames();
	
	/**
	 * Returns a click event listener that defines the behavior that occurs when a user clicks on an Issue in the list.
	 *  
	 * @return a non-null listener.
	 */
	protected abstract OnItemClickListener getIssueListClickListener();
	
	/**
	 * Returns a cursor to the issues that will be displayed for the supplied application and version.
	 * @param appName the name of the application selected by the user
	 * @param version the version of the application selected by the user
	 * @return a non-null cursor of Issues.
	 * 
	 * @throws ServiceException if an error occurs fetching the cursor.
	 */
	protected abstract Cursor getIssueCursor(String appName, String version) 
		throws ServiceException;
	
	/**
	 * Returns the versions available for the specified application.
	 * 
	 * @param position the position of the application spinner.
	 * @param appName the application name
	 * @return a non-null list of version names.
	 * 
	 * @throws ServiceException if an error occurs loading the version names.
	 */
	protected abstract List<String> getVersionList(int position, String appName) 
		throws ServiceException;

    private OnItemSelectedListener appChosen = new OnItemSelectedListener()
    {
		public void onItemSelected(AdapterView<?> l, View v, int position, long id)
		{
			String appName = (String) l.getItemAtPosition(position);
			if(appName.equals(getString(R.string.all)))
				appName = null;

			new LoadIssuesTask().execute(String.valueOf(position), appName, null);
		}

		public void onNothingSelected(AdapterView<?> arg0)
		{
			//empty
		}
    	
    };

    private OnItemSelectedListener versionChosen = new OnItemSelectedListener()
    {
		public void onItemSelected(AdapterView<?> l, View v, int position, long id)
		{
			String appName = (String) appNameSpinner.getItemAtPosition(appNameSpinner.getLastVisiblePosition());
			String version = (String) l.getItemAtPosition(position);
			
			if(appName.equals(getString(R.string.all)))
				appName = null;
			if(position == 0)
				version = null;
			
			new LoadIssuesTask().execute(String.valueOf(-1), appName, version);
		}

		public void onNothingSelected(AdapterView<?> arg0)
		{
			//empty
		}
    	
    };
    
    /**
     * Results class for returning asynchronous result values.
     */
	private class IssueHolder
	{
		public List<String> versionList;
		public Cursor issueCursor;
		public ServiceException exc;
	}

	/**
	 * Asynchronously loads the versions and issues for the specified application. 
	 */
	private class LoadIssuesTask extends AsyncTask<String, Void, IssueHolder> 
	{
		@Override
		protected void onPreExecute()
		{
			progressBar.setVisibility(View.VISIBLE);
		}
		
		/**
		 * @param position the application spinner position
		 * @param appName the application name
		 * @param version the version name
		 * @return a result with a list of version names and a list of issues.
		 */
		@Override
		protected IssueHolder doInBackground(String... params)
		{
			IssueHolder holder = new IssueHolder();
			try
			{
				int position = Integer.parseInt(params[0]);
				String appName = params[1];
				String version = params[2];

				if(position >= 0)
					holder.versionList = getVersionList(position, appName);
				
				holder.issueCursor = getIssueCursor(appName, version);
			}
			catch (ServiceException e)
			{
				holder.exc = e;
			}			
				
			return holder;
		}
		
		@Override
		protected void onPostExecute(IssueHolder holder)
		{
			if(holder.exc == null)
			{
				if(holder.versionList != null)
				{
					ArrayAdapter<String> vAdapter = new ArrayAdapter<String>(AbstractIssueListActivity.this, android.R.layout.simple_spinner_item, holder.versionList);
					versionSpinner.setAdapter(vAdapter);
				}
				
				SimpleCursorAdapter adapter = new SimpleCursorAdapter(AbstractIssueListActivity.this, R.layout.issue_list_item, holder.issueCursor,
					    new String[] { Issues.SUMMARY, Issues.ISSUE_ID, Issues.ISSUE_TYPE, Issues.ISSUE_PRIORITY, Issues.ISSUE_STATE }, 
					    new int[] { R.id.SummaryTextView, R.id.IssueIdTextView, R.id.TypeImageView, R.id.PriorityImageView, R.id.StateImageView });

				adapter.setViewBinder(new IssuesListViewBinder(AbstractIssueListActivity.this, factory));
				listView.setAdapter(adapter);				
			}
			else
			{
				Error.handle(AbstractIssueListActivity.this, holder.exc);
				SimpleCursorAdapter adapter = new SimpleCursorAdapter(AbstractIssueListActivity.this, R.layout.issue_list_item, new NullCursor(), new String[] {}, new int[] {});
				listView.setAdapter(adapter);								
			}
			
			progressBar.setVisibility(View.GONE);
		}
		
	}	    

}
