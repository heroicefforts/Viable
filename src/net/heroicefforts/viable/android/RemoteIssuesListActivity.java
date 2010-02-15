package net.heroicefforts.viable.android;

import java.util.Arrays;
import java.util.Set;

import net.heroicefforts.viable.android.content.Issues;
import net.heroicefforts.viable.android.content.RemoteIssueCursor;
import net.heroicefforts.viable.android.dao.SearchParams;
import net.heroicefforts.viable.android.rep.RepositoryFactory;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class RemoteIssuesListActivity extends Activity
{
    public static final int MENU_ITEM_REFRESH = Menu.FIRST;
	
    private RepositoryFactory factory;
    private SimpleCursorAdapter adapter;
    
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

        setContentView(R.layout.issuelist);		
		
		factory = new RepositoryFactory(this);
		Set<String> appNames = factory.getApplicationNames();
    	Spinner appNameSpinner = (Spinner) findViewById(R.id.AppNameSpinner);
    	appNameSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, appNames.toArray(new String[appNames.size()])));
		appNameSpinner.setOnItemSelectedListener(appChosen);    	
    	
        ((ListView) findViewById(R.id.IssueListView)).setOnItemClickListener(listClicked);
	}

	private void loadList(String firstApp)
	{
		RemoteIssueCursor cursor = new RemoteIssueCursor(factory.getRepository(firstApp));
		SearchParams params = new SearchParams();
		params.setProjectAffectedVersions(firstApp, Arrays.asList(new String[] { "1.0.0" }));
		cursor.setSearchParams(params);
		// Used to map Issues.entries from the database to views
		adapter = new SimpleCursorAdapter(this, R.layout.issueslist_item, cursor,
		        new String[] { Issues.SUMMARY, Issues.ISSUE_ID, Issues.ISSUE_TYPE, Issues.ISSUE_PRIORITY, Issues.ISSUE_STATE }, 
		        new int[] { R.id.SummaryTextView, R.id.IssueIdTextView, R.id.TypeImageView, R.id.PriorityImageView, R.id.StateImageView });
		adapter.setViewBinder(new IssuesListViewBinder(this));
		((ListView) findViewById(R.id.IssueListView)).setAdapter(adapter);
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // This is our one standard application action -- inserting a
        // new note into the list.
        menu.add(0, MENU_ITEM_REFRESH, 0, R.string.refresh)
                .setShortcut('3', 'r')
                .setIcon(android.R.drawable.ic_popup_sync);
        
        return true;
    }	
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_ITEM_REFRESH:
            adapter.getCursor().requery();
        }
        return super.onOptionsItemSelected(item);
    }
 
    private OnItemSelectedListener appChosen = new OnItemSelectedListener()
    {
		public void onItemSelected(AdapterView<?> l, View v, int position, long id)
		{
			String appName = (String) l.getItemAtPosition(position);
			loadList(appName);
		}

		public void onNothingSelected(AdapterView<?> arg0)
		{
			//empty
		}
    	
    };
    
    private OnItemClickListener listClicked = new OnItemClickListener()
    {
		public void onItemClick(AdapterView<?> l, View v, int position, long id)
		{
	    	Cursor cursor = (Cursor) l.getAdapter().getItem(position);
	    	String appName = cursor.getString(cursor.getColumnIndex(Issues.APP_NAME));
	    	String issueId = cursor.getString(cursor.getColumnIndex(Issues.ISSUE_ID));
	    	Intent intent = new Intent(RemoteIssuesListActivity.this, IssueViewActivity.class);
	    	intent.putExtra(IssueViewActivity.EXTRA_ISSUE_ID, issueId);
	    	intent.putExtra(IssueViewActivity.EXTRA_APP_NAME, appName);
	        startActivity(intent);
		}
    };
}
