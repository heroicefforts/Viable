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
package net.heroicefforts.viable.android.rep.jira;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import net.heroicefforts.viable.android.dao.Issue;
import net.heroicefforts.viable.android.rep.TypePriorityStateResource;

/**
 * IssueResource referencing I.T. specific display formatting.
 * 
 * @author jevans
 *
 */
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
