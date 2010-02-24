package net.heroicefforts.viable.android.rep;

import net.heroicefforts.viable.android.dao.Issue;
import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * This interface represents well-defined states of issues in the issue repository.
 * 
 * @author jevans
 *
 */
public interface IssueResource
{
	/**
	 * Initialize the issue with this state.
	 * @param issue the issue to configure
	 */
	public void setState(Issue issue);	
	
	/**
	 * Returns an icon representing this state.
	 * @param ctx the application context
	 * @return an icon
	 */
	public Drawable getIcon(Context ctx);
	
	/**
	 * Returns a description of this state.
	 * @param ctx the application context
	 * @return
	 */
	public CharSequence getDescription(Context ctx);
	
	/**
	 * Returns the descriptive name of the state.
	 * @param ctx the application context
	 * @return
	 */
	public CharSequence getName(Context ctx);
	
	//TODO refactor method locations below
	
	/**
	 * Returns the status display value for the issue.
	 * @param issue 
	 * @return
	 */
	public CharSequence getStateText(Issue issue);
	
	/**
	 * Returns the type display value for the issue.
	 * @param issue
	 * @return
	 */
	public CharSequence getTypeText(Issue issue);
	
	/**
	 * Returns the priority display value for the issue.
	 * @param issue
	 * @return
	 */
	public CharSequence getPriorityText(Issue issue);
}
