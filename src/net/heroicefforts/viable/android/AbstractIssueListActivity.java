package net.heroicefforts.viable.android;

import java.util.List;

import net.heroicefforts.viable.android.rep.CreateException;
import net.heroicefforts.viable.android.rep.RepositoryFactory;
import net.heroicefforts.viable.android.rep.ServiceException;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public abstract class AbstractIssueListActivity extends Activity
{
	protected ListView listView;
	private RepositoryFactory factory;
	private Spinner appNameSpinner;
	private Spinner versionSpinner;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
        setContentView(R.layout.issue_list);		
		
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

	protected abstract List<String> getAppNames();
	protected abstract OnItemClickListener getIssueListClickListener();
	protected abstract SimpleCursorAdapter getIssueCursorAdapter(String appName, String version) 
		throws ServiceException;
	protected abstract List<String> getVersionList(int position, String appName) 
		throws CreateException, ServiceException;

    private OnItemSelectedListener appChosen = new OnItemSelectedListener()
    {
		public void onItemSelected(AdapterView<?> l, View v, int position, long id)
		{
			try
			{
				String appName = (String) l.getItemAtPosition(position);

				List<String> versions = getVersionList(position, appName);
				ArrayAdapter<String> vAdapter = new ArrayAdapter<String>(AbstractIssueListActivity.this, android.R.layout.simple_spinner_item, versions);
				versionSpinner.setAdapter(vAdapter);
				
				if(appName.equals(getString(R.string.all)))
					appName = null;

				SimpleCursorAdapter adapter = getIssueCursorAdapter(appName, null);
				adapter.setViewBinder(new IssuesListViewBinder(AbstractIssueListActivity.this, factory));
				listView.setAdapter(adapter);
			}
			catch (CreateException e)
			{
				Error.handle(AbstractIssueListActivity.this, e);
			}
			catch (ServiceException e)
			{
				Error.handle(AbstractIssueListActivity.this, e);
			}			
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
			try
			{
				String appName = (String) appNameSpinner.getItemAtPosition(appNameSpinner.getLastVisiblePosition());
				String version = (String) l.getItemAtPosition(position);
				
				if(appName.equals(getString(R.string.all)))
					appName = null;
				if(position == 0)
					version = null;
				
				SimpleCursorAdapter adapter = getIssueCursorAdapter(appName, version);
				adapter.setViewBinder(new IssuesListViewBinder(AbstractIssueListActivity.this, factory));
				listView.setAdapter(adapter);
			}
			catch (ServiceException e)
			{
				Error.handle(AbstractIssueListActivity.this, e);
			}			
		}

		public void onNothingSelected(AdapterView<?> arg0)
		{
			//empty
		}
    	
    };

}
