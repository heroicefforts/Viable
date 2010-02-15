package net.heroicefforts.viable.android;

import android.provider.BaseColumns;

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
