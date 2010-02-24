package net.heroicefforts.viable.android.rep.it;

import net.heroicefforts.viable.android.dao.Issue;
import net.heroicefforts.viable.android.rep.TypePriorityStateResource;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

/**
 * IssueResource referencing I.T. specific display formatting.
 * 
 * @author jevans
 *
 */
public class ITIssueResource extends TypePriorityStateResource
{
	public ITIssueResource(String type, String priority, String state, int name, int desc, int iconRes)
	{
		super(type, priority, state, name, desc, iconRes);
	}

	public CharSequence getPriorityText(Issue issue)
	{
		return getColoredText(issue.getPriority(), ITResourceFlyweight.getPriorityColor(issue));
	}

	public CharSequence getStateText(Issue issue)
	{
		return getColoredText(issue.getState(), ITResourceFlyweight.getStateColor(issue));
	}

	public CharSequence getTypeText(Issue issue)
	{
		return getColoredText(issue.getType(), ITResourceFlyweight.getTypeColor(issue));
	}

	private CharSequence getColoredText(String text, int color)
	{
		text = Character.toUpperCase(text.charAt(0)) + text.substring(1);
		SpannableStringBuilder str = new SpannableStringBuilder();
		str.append(text);
		str.setSpan(new ForegroundColorSpan(color), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return str;
	}

}
