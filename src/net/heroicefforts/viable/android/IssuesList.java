package net.heroicefforts.viable.android;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Displays a list of Issues. Will display Issues.from the {@link Uri}
 * provided in the intent if there is one, otherwise defaults to displaying the
 * contents of the {@link NotePadProvider}
 */
public class IssuesList extends Activity {
    private static final String TAG = "IssuesList";

    // Menu item ids
    public static final int MENU_ITEM_DELETE = Menu.FIRST;
    public static final int MENU_ITEM_INSERT = Menu.FIRST + 1;

    /**
     * The columns we are interested in from the database
     */
    private static final String[] PROJECTION = new String[] {
            Issues._ID,
            Issues.APP_NAME,
            Issues.ISSUE_ID,
            Issues.ISSUE_TYPE,
            Issues.ISSUE_PRIORITY,
            Issues.ISSUE_STATE,
            Issues.SUMMARY
    };

    /** The index of the title column */
    private static final int COLUMN_INDEX_TITLE = 5;

	private ListView listView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.issuelist);		
        
        setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);

        Intent intent = getIntent();
        if (intent.getData() == null) {
            intent.setData(Issues.CONTENT_URI);
        }

    	Spinner appNameSpinner = (Spinner) findViewById(R.id.AppNameSpinner);
    	List<String> apps = new ArrayList<String>();
    	apps.add(getString(R.string.all));
    	Cursor appNameCursor = managedQuery(Issues.APP_CONTENT_URI, new String[] { Issues._ID, Issues.APP_NAME }, null, null, null);
    	while(appNameCursor.moveToNext())
    		apps.add(appNameCursor.getString(1));
//		appNameSpinner.setAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, appNameCursor, new String[] { Issues.APP_NAME }, new int[] { android.R.id.text1 }));
    	appNameSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, apps));
		appNameSpinner.setOnItemSelectedListener(appChosen); 
        
        // Inform the list we provide context menus for items
		listView = (ListView) findViewById(R.id.IssueListView);
        listView.setOnCreateContextMenuListener(this);
        listView.setOnItemClickListener(listClicked);        
    }

    private OnItemSelectedListener appChosen = new OnItemSelectedListener()
    {
		public void onItemSelected(AdapterView<?> l, View v, int position, long id)
		{
			String appName = (String) l.getItemAtPosition(position);
			Cursor cursor;
			if(position == 0)
				cursor = managedQuery(getIntent().getData(), PROJECTION, null, null, Issues.DEFAULT_SORT_ORDER);
			else
				cursor = managedQuery(getIntent().getData(), PROJECTION, Issues.APP_NAME + " = ?", new String[] { appName },
		                Issues.DEFAULT_SORT_ORDER);
				
	        SimpleCursorAdapter adapter = new SimpleCursorAdapter(IssuesList.this, R.layout.issueslist_item, cursor,
	                new String[] { Issues.SUMMARY, Issues.ISSUE_ID, Issues.ISSUE_TYPE, Issues.ISSUE_PRIORITY, Issues.ISSUE_STATE }, 
	                new int[] { R.id.SummaryTextView, R.id.IssueIdTextView, R.id.TypeImageView, R.id.PriorityImageView, R.id.StateImageView });
	        adapter.setViewBinder(new IssuesListViewBinder(IssuesList.this));	        
			listView.setAdapter(adapter);
		}

		public void onNothingSelected(AdapterView<?> arg0)
		{
			//empty
		}
    	
    };
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // This is our one standard application action -- inserting a
        // new note into the list.
        menu.add(0, MENU_ITEM_INSERT, 0, R.string.menu_insert)
                .setShortcut('3', 'a')
                .setIcon(android.R.drawable.ic_menu_add);

        // Generate any additional actions that can be performed on the
        // overall list.  In a normal install, there are no additional
        // actions found here, but this allows other applications to extend
        // our menu with their own actions.
        Intent intent = new Intent(null, getIntent().getData());
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, IssuesList.class), null, intent, 0, null);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        final boolean haveItems = listView.getAdapter().getCount() > 0;

        if (haveItems) {
            // This is the selected item.
            Uri uri = ContentUris.withAppendedId(getIntent().getData(), listView.getSelectedItemId());

            // Build menu...  always starts with the EDIT action...
            Intent[] specifics = new Intent[1];
            specifics[0] = new Intent(Intent.ACTION_EDIT, uri);
            MenuItem[] items = new MenuItem[1];

            // ... is followed by whatever other actions are available...
            Intent intent = new Intent(null, uri);
            intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
            menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0, null, specifics, intent, 0,
                    items);

            // Give a shortcut to the edit action.
            if (items[0] != null) {
                items[0].setShortcut('1', 'e');
            }
        } else {
            menu.removeGroup(Menu.CATEGORY_ALTERNATIVE);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_ITEM_INSERT:
            // Launch activity to insert a new item
            startActivity(new Intent(Intent.ACTION_INSERT, getIntent().getData()));
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        menu.setHeaderTitle(cursor.getString(COLUMN_INDEX_TITLE));

        // Add a menu item to delete the note
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

    private OnItemClickListener listClicked = new OnItemClickListener() 
    {    	
	    public void onItemClick(AdapterView<?> l, View v, int position, long id)
		{
	        Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
	        
	        String action = getIntent().getAction();
	        if (Intent.ACTION_PICK.equals(action) || Intent.ACTION_GET_CONTENT.equals(action)) {
	            // The caller is waiting for us to return a note selected by
	            // the user.  The have clicked on one, so return it now.
	            setResult(RESULT_OK, new Intent().setData(uri));
	        } else {
	            // Launch activity to view/edit the currently selected item
	            startActivity(new Intent(Intent.ACTION_EDIT, uri));
	        }
		}
    };
}
