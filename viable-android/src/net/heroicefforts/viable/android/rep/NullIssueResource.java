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
package net.heroicefforts.viable.android.rep;

import net.heroicefforts.viable.android.dao.Issue;
import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * A text-only IssueResource implementation used for unknown resource types.
 * 
 * @author jevans
 *
 */
public class NullIssueResource implements IssueResource
{
	private String type;
	private String priority;
	private String state;

	
	public NullIssueResource(String type, String priority, String state)
	{
		this.type = type;
		this.priority = priority;
		this.state = state;
	}
	
	public CharSequence getDescription(Context ctx)
	{
		return "";
	}

	public Drawable getIcon(Context ctx)
	{
		return null;
	}

	public CharSequence getName(Context ctx)
	{
		return type + " " + priority + " " + state;
	}

	public CharSequence getPriorityText(Issue issue)
	{
		return issue.getPriority();
	}

	public CharSequence getStateText(Issue issue)
	{
		return issue.getState();
	}

	public CharSequence getTypeText(Issue issue)
	{
		return issue.getType();
	}

	public void setState(Issue issue)
	{
		//empty
	}

}
