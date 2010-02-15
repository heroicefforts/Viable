package net.heroicefforts.viable.android;

import java.util.ArrayList;
import java.util.List;

import net.heroicefforts.viable.android.content.Issues;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;


public class LocalIssueListActivity extends AbstractIssueListActivity 
{
    private static final String TAG = "IssuesList";

    // Menu item ids
    private static final int MENU_ITEM_DELETE = Menu.FIRST;


    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        Intent intent = getIntent();
        if (intent.getData() == null) {
            intent.setData(Issues.CONTENT_URI);
        }

        super.onCreate(savedInstanceState);
        
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
    }

	protected List<String> getAppNames()
	{
		List<String> apps = new ArrayList<String>();
    	apps.add(getString(R.string.all));
    	Cursor appNameCursor = managedQuery(Issues.APP_CONTENT_URI, new String[] { Issues._ID, Issues.APP_NAME }, null, null, null);
    	while(appNameCursor.moveToNext())
    		apps.add(appNameCursor.getString(1));
		return apps;
	}
    
	protected SimpleCursorAdapter getIssueCursorAdapter(int position, String appName)
	{
	    final String[] PROJECTION = new String[] {
            Issues._ID,
            Issues.APP_NAME,
            Issues.ISSUE_ID,
            Issues.ISSUE_TYPE,
            Issues.ISSUE_PRIORITY,
            Issues.ISSUE_STATE,
            Issues.SUMMARY
	    };
				
		Cursor cursor;
		if(position == 0)
			cursor = managedQuery(getIntent().getData(), PROJECTION, null, null, Issues.DEFAULT_SORT_ORDER);
		else
			cursor = managedQuery(getIntent().getData(), PROJECTION, Issues.APP_NAME + " = ?", new String[] { appName },
	                Issues.DEFAULT_SORT_ORDER);
			
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(LocalIssueListActivity.this, R.layout.issue_list_item, cursor,
                new String[] { Issues.SUMMARY, Issues.ISSUE_ID, Issues.ISSUE_TYPE, Issues.ISSUE_PRIORITY, Issues.ISSUE_STATE }, 
                new int[] { R.id.SummaryTextView, R.id.IssueIdTextView, R.id.TypeImageView, R.id.PriorityImageView, R.id.StateImageView });
		return adapter;
	}

    protected OnItemClickListener getIssueListClickListener()
    {
    	return new OnItemClickListener() 
        {    	
    	    public void onItemClick(AdapterView<?> l, View v, int position, long id)
    		{
    	        Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
    	        
    	        String action = getIntent().getAction();
    	        if (Intent.ACTION_PICK.equals(action) || Intent.ACTION_GET_CONTENT.equals(action))
    	            setResult(RESULT_OK, new Intent().setData(uri));
    	        else
    	            startActivity(new Intent(Intent.ACTION_EDIT, uri));
    		}
        };
    }	

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            Log.e(TAG, "bad menuInfo", e);
            return;
        }

        Cursor cursor = (Cursor) listView.getAdapter().getItem(info.position);
        if (cursor == null) {
            // For some reason the requested item isn't available, do nothing
            return;
        }

        // Setup the menu header
        menu.setHeaderTitle(cursor.getString(cursor.getColumnIndex(Issues.ISSUE_ID)));

        // Add a menu item to delete the issue
        menu.add(0, MENU_ITEM_DELETE, 0, R.string.menu_delete);
    }
        
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            Log.e(TAG, "bad menuInfo", e);
            return false;
        }

        switch (item.getItemId()) {
            case MENU_ITEM_DELETE: {
                Uri issueUri = ContentUris.withAppendedId(getIntent().getData(), info.id);
                getContentResolver().delete(issueUri, null, null);
                return true;
            }
        }
        return false;
    }

}
