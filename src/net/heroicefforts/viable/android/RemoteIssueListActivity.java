package net.heroicefforts.viable.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.heroicefforts.viable.android.content.Issues;
import net.heroicefforts.viable.android.content.RemoteIssueCursor;
import net.heroicefforts.viable.android.dao.SearchParams;
import net.heroicefforts.viable.android.dao.VersionDetail;
import net.heroicefforts.viable.android.rep.CreateException;
import net.heroicefforts.viable.android.rep.RepositoryFactory;
import net.heroicefforts.viable.android.rep.ServiceException;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;

/**
 * This class handles the display of issues fetched from remote repositories.
 * 
 * @author jevans
 *
 */
public class RemoteIssueListActivity extends AbstractIssueListActivity
{
    public static final int MENU_ITEM_REFRESH = Menu.FIRST;
	
    private RepositoryFactory factory;

	protected List<String> getAppNames()
	{
		List<String> appNames = new ArrayList<String>(getFactory().getApplicationNames());
		return appNames;
	}
    
	protected Cursor getIssueCursor(String firstApp, String version) 
		throws ServiceException
	{
		RemoteIssueCursor cursor = new RemoteIssueCursor(getFactory().getRepository(firstApp));
		SearchParams params = new SearchParams();
		if(version != null)
			params.setProjectAffectedVersions(firstApp, Arrays.asList(new String[] { version }));
		else
			params.setProjectAffectedVersions(firstApp, Arrays.asList(new String[] { "all" }));
		cursor.setSearchParams(params);

		return cursor;
	}
		 
	protected ArrayList<String> getVersionList(int position, String appName) 
		throws CreateException, ServiceException
	{
		List<VersionDetail> vDetails = factory.getRepository(appName).getApplicationVersions();
		ArrayList<String> versions = new ArrayList<String>();
		versions.add(getString(R.string.all));
		for(VersionDetail detail : vDetails)
			versions.add(detail.getName());
		return versions;
	}
	
    protected OnItemClickListener getIssueListClickListener()
    {
    	return new OnItemClickListener()
        {
    		public void onItemClick(AdapterView<?> l, View v, int position, long id)
    		{
    	    	Cursor cursor = (Cursor) l.getAdapter().getItem(position);
    	    	String appName = cursor.getString(cursor.getColumnIndex(Issues.APP_NAME));
    	    	String issueId = cursor.getString(cursor.getColumnIndex(Issues.ISSUE_ID));
    	    	Intent intent = new Intent(RemoteIssueListActivity.this, IssueViewActivity.class);
    	    	intent.putExtra(IssueViewActivity.EXTRA_ISSUE_ID, issueId);
    	    	intent.putExtra(IssueViewActivity.EXTRA_APP_NAME, appName);
    	        startActivity(intent);
    		}
        };
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_ITEM_REFRESH, 0, R.string.refresh)
                .setShortcut('3', 'r')
                .setIcon(android.R.drawable.ic_popup_sync);
        
        return true;
    }	
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_ITEM_REFRESH:
        	((SimpleCursorAdapter) listView.getAdapter()).getCursor().requery();
        }
        return super.onOptionsItemSelected(item);
    }
    
    //late instantiation
	private RepositoryFactory getFactory()
	{
		if(factory == null)
			factory = new RepositoryFactory(this);
		
		return factory;
	}	
    
}
