package net.heroicefforts.viable.android.rep;

import net.heroicefforts.viable.android.dao.Issue;
import android.content.Context;
import android.graphics.drawable.Drawable;

public interface IssueResource
{
	public void setState(Issue issue);	
	public Drawable getIcon(Context ctx);
	public CharSequence getDescription(Context ctx);
	public CharSequence getName(Context ctx);
	
	public CharSequence getStateText(Issue issue);
	public CharSequence getTypeText(Issue issue);
	public CharSequence getPriorityText(Issue issue);
}
