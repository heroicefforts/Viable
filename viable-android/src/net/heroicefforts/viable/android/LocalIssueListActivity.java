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
import java.util.TreeSet;

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
import android.widget.AdapterView.OnItemClickListener;

/**
 * This class handles the retrieval of user specific issues locally stored on the phone.
 * 
 * @author jevans
 *
 */
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
    
	protected ArrayList<String> getVersionList(int position, String appName)
	{
		TreeSet<String> versions = new TreeSet<String>();
		Cursor cursor;
		if(position > 0)
			cursor = managedQuery(Issues.VERSION_CONTENT_URI, new String[] { Issues.APP_VERSION }, Issues.APP_NAME + " = ?", new String[] { appName }, Issues.DEFAULT_SORT_ORDER);
		else
			cursor = managedQuery(Issues.VERSION_CONTENT_URI, new String[] { Issues.APP_VERSION }, null, null, Issues.DEFAULT_SORT_ORDER);
		while(cursor.moveToNext())
		{
			String v = cursor.getString(cursor.getColumnIndex(Issues.APP_VERSION));
			v = v.substring(1, v.length() - 1);
			String[] vArr = v.split("[ ]*,[ ]*");
			for(int i = 0; i < vArr.length; i++)
				versions.add(vArr[i]);
		}
		ArrayList<String> retVal = new ArrayList<String>(versions);
		retVal.add(0, getString(R.string.all));
		return retVal;

	}
	
	protected Cursor getIssueCursor(String appName, String version)
	{
	    final String[] PROJECTION = new String[] {
            Issues._ID,
            Issues.APP_NAME,
            Issues.ISSUE_ID,
            Issues.ISSUE_TYPE,
            Issues.ISSUE_PRIORITY,
            Issues.ISSUE_STATE,
            Issues.VOTED,
            Issues.VOTES,
            Issues.SUMMARY
	    };
				
		Cursor cursor;
		if(appName == null && version == null)
			cursor = managedQuery(getIntent().getData(), PROJECTION, null, null, Issues.DEFAULT_SORT_ORDER);
		else if(version == null)
			cursor = managedQuery(getIntent().getData(), PROJECTION, Issues.APP_NAME + " = ?", new String[] { appName },
	                Issues.DEFAULT_SORT_ORDER);
		else if(appName == null)
			cursor = managedQuery(getIntent().getData(), PROJECTION, Issues.APP_VERSION + " like ?", new String[] { "%" + version + "%" },
	                Issues.DEFAULT_SORT_ORDER);			
		else
			cursor = managedQuery(getIntent().getData(), PROJECTION, Issues.APP_NAME + " = ? and " + Issues.APP_VERSION + " like ?", new String[] { appName, "%" + version + "%" },
	                Issues.DEFAULT_SORT_ORDER);
			
		return cursor;
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
