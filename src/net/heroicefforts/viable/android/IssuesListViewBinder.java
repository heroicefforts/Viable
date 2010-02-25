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

import net.heroicefforts.viable.android.content.Issues;
import net.heroicefforts.viable.android.rep.IssueResource;
import net.heroicefforts.viable.android.rep.RepositoryFactory;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter.ViewBinder;

/**
 * Binds issue values to the list view.
 * 
 * @author jevans
 *
 */
public class IssuesListViewBinder implements ViewBinder
{
	private Context ctx;
	private RepositoryFactory factory;
	
	public IssuesListViewBinder(Context ctx, RepositoryFactory factory)
	{
		this.ctx = ctx;
		this.factory = factory;
	}
	
	public boolean setViewValue(View view, Cursor cursor, int columnIndex)
	{
		String colName = cursor.getColumnName(columnIndex);
		if(Issues.ISSUE_PRIORITY.equals(colName) && (view instanceof ImageView))
			return true;
		else if(Issues.ISSUE_STATE.equals(colName) && (view instanceof ImageView))
			return true;
		else if(Issues.ISSUE_TYPE.equals(colName) && (view instanceof ImageView))
			((ImageView) view).setImageDrawable(getTypeIcon(
				cursor.getString(cursor.getColumnIndex(Issues.APP_NAME)), 
				cursor.getString(cursor.getColumnIndex(Issues.ISSUE_TYPE)), 
				cursor.getString(cursor.getColumnIndex(Issues.ISSUE_PRIORITY)), 
				cursor.getString(cursor.getColumnIndex(Issues.ISSUE_STATE))));
		else
			return false;
		
		return true;
	}

	protected Drawable getTypeIcon(String appName, String type, String priority, String state)
	{
		IssueResource resource = factory.getRepository(appName).getState(type, priority, state);
		if(resource != null)
			return resource.getIcon(ctx);
		else
			return null;
	}
	
}
