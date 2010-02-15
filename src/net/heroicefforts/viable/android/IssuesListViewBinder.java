package net.heroicefforts.viable.android;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class IssuesListViewBinder implements ViewBinder
{
	private Context ctx;
	
	public IssuesListViewBinder(Context ctx)
	{
		this.ctx = ctx;
	}
	
	public boolean setViewValue(View view, Cursor cursor, int columnIndex)
	{
		String colName = cursor.getColumnName(columnIndex);
		if(Issues.ISSUE_PRIORITY.equals(colName) && (view instanceof ImageView))
			return true;
		else if(Issues.ISSUE_STATE.equals(colName) && (view instanceof ImageView))
			return true;
		else if(Issues.ISSUE_TYPE.equals(colName) && (view instanceof ImageView))
			((ImageView) view).setImageDrawable(getTypeIcon(cursor.getString(columnIndex), 
				cursor.getString(cursor.getColumnIndex("priority")), cursor.getString(cursor.getColumnIndex("state"))));
		else
			return false;
		
		return true;
	}

	protected Drawable getTypeIcon(String type, String priority, String state)
	{
		IssueState iState = IssueState.getState(type, priority, state);
		if(iState != null)
			return ctx.getResources().getDrawable(iState.getIconRes());
		else
			return null;
	}
	
}
