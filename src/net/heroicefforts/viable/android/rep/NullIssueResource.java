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
