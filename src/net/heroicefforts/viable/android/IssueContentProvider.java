package net.heroicefforts.viable.android;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Provides access to a database of notes. Each note has a title, the note
 * itself, a creation date and a modified data.
 */
public class IssueContentProvider extends ContentProvider {

    private static final String TAG = "IssueContentProvider";

    private static final String DATABASE_NAME = "issue.db";
    private static final int DATABASE_VERSION = 1;
    private static final String ISSUES_TABLE_NAME = "issue";
    private static final String REG_APPS_TABLE_NAME = "registered_apps";

    private static HashMap<String, String> issuesProjectionMap;
	private static HashMap<String, String> appsProjectionMap;

    private static final int ISSUES = 1;
    private static final int ISSUES_ID = 2;
	private static final int APPS = 3;

    private static final UriMatcher sUriMatcher;

    private DatabaseHelper openHelper;

    
    /**
     * This class helps open, create, and upgrade the database file.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + ISSUES_TABLE_NAME + " ("
                    + Issues._ID + " INTEGER PRIMARY KEY,"
                    + Issues.ISSUE_ID + " TEXT UNIQUE ON CONFLICT ROLLBACK,"
                    + Issues.ISSUE_TYPE + " TEXT NOT NULL ON CONFLICT ROLLBACK,"
                    + Issues.ISSUE_PRIORITY + " TEXT NOT NULL ON CONFLICT ROLLBACK,"
                    + Issues.ISSUE_STATE + " TEXT,"
                    + Issues.SUMMARY + " TEXT NOT NULL ON CONFLICT ROLLBACK,"
                    + Issues.DESCRIPTION + " TEXT,"
                    + Issues.STACKTRACE + " TEXT,"
                    + Issues.HASH + " TEXT,"
                    + Issues.APP_NAME + " TEXT NOT NULL ON CONFLICT ROLLBACK,"
                    + Issues.APP_VERSION + " TEXT NOT NULL ON CONFLICT ROLLBACK,"
                    + Issues.CREATED_DATE + " INTEGER,"
                    + Issues.MODIFIED_DATE + " INTEGER"
                    + ");"); 
            
            db.execSQL("CREATE TABLE " + REG_APPS_TABLE_NAME + " ("
                    + Issues._ID + " INTEGER PRIMARY KEY,"
            		+ Issues.APP_NAME + " TEXT UNIQUE ON CONFLICT ROLLBACK,"
            		+ "ACTIVE" + " INTEGER DEFAULT 1,"
                    + Issues.CREATED_DATE + " INTEGER,"
                    + Issues.MODIFIED_DATE + " INTEGER" 
            		+ ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
        }
    }

    @Override
    public boolean onCreate() {
        openHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) 
        {
	        case ISSUES:
	            qb.setTables(ISSUES_TABLE_NAME);
	            qb.setProjectionMap(issuesProjectionMap);
	            break;
	
	        case ISSUES_ID:
	            qb.setTables(ISSUES_TABLE_NAME);
	            qb.setProjectionMap(issuesProjectionMap);
	            qb.appendWhere(Issues._ID + "=" + uri.getPathSegments().get(1));
	            break;
	
	        case APPS:
	        	qb.setTables(REG_APPS_TABLE_NAME);
	        	qb.setProjectionMap(appsProjectionMap);
	        	break;
	            
	        default:
	            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = Issues.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        // Get the database and run the query
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
        case ISSUES:
            return Issues.CONTENT_TYPE;

        case ISSUES_ID:
            return Issues.CONTENT_ITEM_TYPE;

        case APPS:
        	return Issues.APP_CONTENT_TYPE;
            
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
    	int type = sUriMatcher.match(uri);
        if (type != ISSUES && type != APPS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        Long now = Long.valueOf(System.currentTimeMillis());

        // Make sure that the fields are all set
        if (values.containsKey(Issues.CREATED_DATE) == false) {
            values.put(Issues.CREATED_DATE, now);
        }

        if (values.containsKey(Issues.MODIFIED_DATE) == false) {
            values.put(Issues.MODIFIED_DATE, now);
        }

        SQLiteDatabase db = openHelper.getWritableDatabase();
        long rowId;
        if(type == ISSUES)
        {
        	Cursor apps = null;
        	try {
        		apps = db.query(REG_APPS_TABLE_NAME, new String[] { "_ID" }, Issues.APP_NAME + " = ?", new String[] { (String) values.get(Issues.APP_NAME) }, null, null, null);
            	if(!apps.moveToFirst())
            	{
            		ContentValues appValues = new ContentValues();
            		appValues.put(Issues.APP_NAME, values.getAsString(Issues.APP_NAME));
                    values.put(Issues.CREATED_DATE, now);
                    values.put(Issues.MODIFIED_DATE, now);
            		long appRowId = db.insert(REG_APPS_TABLE_NAME, Issues.APP_NAME, appValues);
            		if(appRowId > 0) 
            		{
                    	Uri newUri = ContentUris.withAppendedId(Issues.APP_CONTENT_URI, appRowId);            
                        getContext().getContentResolver().notifyChange(newUri, null);            			
            		}
            	}
        	}
        	finally
        	{
        		apps.close();
        	}
        	
        	rowId = db.insert(ISSUES_TABLE_NAME, Issues.ISSUE_ID, values);
        }
        else
        	rowId = db.insert(REG_APPS_TABLE_NAME, Issues.APP_NAME, values);
        if (rowId > 0) {
            Uri newUri;
            if(type == ISSUES)
            	newUri = ContentUris.withAppendedId(Issues.CONTENT_URI, rowId);
            else
            	newUri = ContentUris.withAppendedId(Issues.APP_CONTENT_URI, rowId);            
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case ISSUES:
            count = db.delete(ISSUES_TABLE_NAME, where, whereArgs);
            break;

        case ISSUES_ID:
            String noteId = uri.getPathSegments().get(1);
            count = db.delete(ISSUES_TABLE_NAME, Issues._ID + "=" + noteId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        case APPS:
        	count = db.delete(REG_APPS_TABLE_NAME, where, whereArgs);
        	break;
            
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case ISSUES:
            count = db.update(ISSUES_TABLE_NAME, values, where, whereArgs);
            break;

        case ISSUES_ID:
            String noteId = uri.getPathSegments().get(1);
            count = db.update(ISSUES_TABLE_NAME, values, Issues._ID + "=" + noteId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        case APPS:
        	count = db.update(REG_APPS_TABLE_NAME, values, where, whereArgs);
        	break;
            
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(Issues.AUTHORITY, "issues", ISSUES);
        sUriMatcher.addURI(Issues.AUTHORITY, "issues/#", ISSUES_ID);
        sUriMatcher.addURI(Issues.AUTHORITY, "apps", APPS );

        issuesProjectionMap = new HashMap<String, String>();
        issuesProjectionMap.put(Issues._ID, Issues._ID);
        issuesProjectionMap.put(Issues.ISSUE_ID, Issues.ISSUE_ID);
        issuesProjectionMap.put(Issues.ISSUE_TYPE, Issues.ISSUE_TYPE);
        issuesProjectionMap.put(Issues.ISSUE_PRIORITY, Issues.ISSUE_PRIORITY);
        issuesProjectionMap.put(Issues.ISSUE_STATE, Issues.ISSUE_STATE);
        issuesProjectionMap.put(Issues.APP_NAME, Issues.APP_NAME);
        issuesProjectionMap.put(Issues.APP_VERSION, Issues.APP_VERSION);
        issuesProjectionMap.put(Issues.SUMMARY, Issues.SUMMARY);
        issuesProjectionMap.put(Issues.DESCRIPTION, Issues.DESCRIPTION);
        issuesProjectionMap.put(Issues.HASH, Issues.HASH);
        issuesProjectionMap.put(Issues.STACKTRACE, Issues.STACKTRACE);
        issuesProjectionMap.put(Issues.CREATED_DATE, Issues.CREATED_DATE);
        issuesProjectionMap.put(Issues.MODIFIED_DATE, Issues.MODIFIED_DATE); 
        
        appsProjectionMap = new HashMap<String, String>();
        appsProjectionMap.put(Issues._ID, Issues._ID);
        appsProjectionMap.put(Issues.APP_NAME, Issues.APP_NAME);
        appsProjectionMap.put("ACTIVE", "ACTIVE");
        appsProjectionMap.put(Issues.CREATED_DATE, Issues.CREATED_DATE);
        appsProjectionMap.put(Issues.MODIFIED_DATE, Issues.MODIFIED_DATE);
    }
}
