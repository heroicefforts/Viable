package net.heroicefforts.viable.android.content;

import android.provider.BaseColumns;

/**
 * Constant class for comment cursor.
 * 
 * @author jevans
 *
 */
public class Comments implements BaseColumns
{
	public final static String AUTHOR = "author";
	public final static String BODY = "body";
	public final static String CREATED_DATE = "created";
	
	public final static String[] PROJECTION = {
		_ID,
		AUTHOR,
		BODY,
		CREATED_DATE
	};
}
