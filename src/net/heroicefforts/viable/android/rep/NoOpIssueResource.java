package net.heroicefforts.viable.android.rep;

import net.heroicefforts.viable.android.dao.Issue;
import android.content.Context;
import android.graphics.drawable.Drawable;

public class NoOpIssueResource implements IssueResource
{
	private String type;
	private String priority;
	private String state;

	
	public NoOpIssueResource(String type, String priority, String state)
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
		// TODO Auto-generated method stub
		return issue.getState();
	}

	public CharSequence getTypeText(Issue issue)
	{
		// TODO Auto-generated method stub
		return issue.getType();
	}

	public void setState(Issue issue)
	{
		// TODO Auto-generated method stub

	}

}
