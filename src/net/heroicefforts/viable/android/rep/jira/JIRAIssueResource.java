package net.heroicefforts.viable.android.rep.jira;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import net.heroicefforts.viable.android.dao.Issue;
import net.heroicefforts.viable.android.rep.TypePriorityStateResource;

public class JIRAIssueResource extends TypePriorityStateResource
{
	public JIRAIssueResource(String type, String priority, String state, int name, int desc, int iconRes)
	{
		super(type, priority, state, name, desc, iconRes);
	}

	public CharSequence getPriorityText(Issue issue)
	{
		return getColoredText(issue.getPriority(), JIRAResourceFlyweight.getPriorityColor(issue));
	}

	public CharSequence getStateText(Issue issue)
	{
		return getColoredText(issue.getState(), JIRAResourceFlyweight.getStateColor(issue));
	}

	public CharSequence getTypeText(Issue issue)
	{
		return getColoredText(issue.getType(), JIRAResourceFlyweight.getTypeColor(issue));
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
