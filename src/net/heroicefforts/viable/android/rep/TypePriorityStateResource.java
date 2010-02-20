package net.heroicefforts.viable.android.rep;

import java.lang.ref.SoftReference;

import net.heroicefforts.viable.android.dao.Issue;
import android.content.Context;
import android.graphics.drawable.Drawable;

public class TypePriorityStateResource implements IssueResource
{
	public final static String ANY = "*"; 
	
	private String type;
	private String priority;
	private String state;
	int nameRes;
	int descRes;
	private int iconRes;
	private SoftReference<Drawable> icon;
	
	public TypePriorityStateResource(String type, String priority, String state, int name, int desc, int iconRes)
	{
		super();
		this.type = type;
		this.priority = priority;
		this.state = state;
		this.nameRes = name;
		this.descRes = desc;
		this.iconRes = iconRes;
	}

	public Drawable getIcon(Context ctx)
	{		
		Drawable retVal = null;
		if(icon != null)
			retVal = icon.get();
		
		if(retVal == null)
		{
			retVal = ctx.getResources().getDrawable(iconRes);
			icon = new SoftReference<Drawable>(retVal);
		}
		
		return retVal;
	}
	
	public void setState(Issue issue)
	{
		if(!ANY.equals(issue.getPriority()))
			issue.setPriority(priority);
		if(!ANY.equals(issue.getType()))
			issue.setType(type);
		if(!ANY.equals(issue.getState()))
			issue.setState(state);
	}

	public String getDescription(Context ctx)
	{
		return ctx.getString(descRes);
	}

	public String getName(Context ctx)
	{
		return ctx.getString(nameRes);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((priority == null) ? 0 : priority.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TypePriorityStateResource other = (TypePriorityStateResource) obj;
		if (priority == null)
		{
			if (other.priority != null)
				return false;
		}
		else if (!priority.equals(other.priority))
			return false;
		if (state == null)
		{
			if (other.state != null)
				return false;
		}
		else if (!state.equals(other.state))
			return false;
		if (type == null)
		{
			if (other.type != null)
				return false;
		}
		else if (!type.equals(other.type))
			return false;
		return true;
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

}
