package net.heroicefforts.viable.android.content;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Constant class for issue content.
 * 
 * @author jevans
 *
 */
public class Issues implements BaseColumns
{
    public static final String AUTHORITY = "net.heroicefforts.viable.Issues";
	
    /**
     * The content:// style URL for this table
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/issues");

    public static final Uri APP_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/apps");

    public static final Uri VERSION_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/versions");
    
    /**
     * The MIME type of {@link #CONTENT_URI} providing a directory of issues.
     */
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.heroicefforts.issue";

    /**
     * The MIME type of {@link #CONTENT_URI} providing a directory of issue trackable applications.
     */
    public static final String APP_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.heroicefforts.issue.app";
    
    /**
     * The MIME type of a {@link #CONTENT_URI} sub-directory of a single issue.
     */
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.heroicefforts.issue";

    /**
     * The default sort order for this table
     */
    public static final String DEFAULT_SORT_ORDER = "modified DESC";

    /**
     * The vendor assigned bug id.
     * <P>Type: TEXT</P>
     */
    public static final String ISSUE_ID = "issue_id";

    /**
     * The type of issue (bug, feature request, etc.).
     * <P>Type: TEXT</P>
     */
    public static final String ISSUE_TYPE = "type";

    /**
     * The priority of the issue.
     * <P>Type: TEXT</P>
     */
    public static final String ISSUE_PRIORITY = "priority";

    /**
     * The state of the issue (open, closed, etc.).
     * <P>Type: TEXT</P>
     */
    public static final String ISSUE_STATE = "state";

    /**
     * The name of issue's owner.
     * <P>Type: TEXT</P>
     */
    public static final String APP_NAME = "app_name";

    /**
     * The issue summary
     * <P>Type: TEXT</P>
     */
    public static final String SUMMARY = "summary";

    /**
     * The issue description
     * <P>Type: TEXT</P>
     */
    public static final String DESCRIPTION = "description";
    
    /**
     * The issue app version
     * <P>Type: TEXT</P>
     */
    public static final String APP_VERSION = "app_version";
    
    /**
     * The bug's hash value.
     * <P>Type: TEXT</P>
     */
    public static final String HASH = "hash";
    
    /**
     * The bug's associated stacktrace.
     * <P>Type: TEXT</P>
     */
    public static final String STACKTRACE = "stacktrace";
    
    
    /**
     * The timestamp for when the issue was created
     * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
     */
    public static final String CREATED_DATE = "created";

    /**
     * The timestamp for when the issue was last modified
     * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
     */
    public static final String MODIFIED_DATE = "modified";

	public static final String[] ISSUE_PROJECTION = new String[] {
        Issues._ID,
        Issues.ISSUE_ID,
        Issues.APP_NAME,
        Issues.ISSUE_TYPE,
        Issues.ISSUE_PRIORITY,
        Issues.ISSUE_STATE,
        Issues.APP_VERSION,
        Issues.SUMMARY,
        Issues.DESCRIPTION,
        Issues.HASH,
        Issues.STACKTRACE,
        Issues.CREATED_DATE,
        Issues.MODIFIED_DATE
	};
;
    
}
