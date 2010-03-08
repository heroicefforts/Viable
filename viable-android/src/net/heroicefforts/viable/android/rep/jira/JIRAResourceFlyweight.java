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

import static net.heroicefforts.viable.android.rep.TypePriorityStateResource.ANY;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.heroicefforts.viable.android.R;
import net.heroicefforts.viable.android.dao.Issue;
import net.heroicefforts.viable.android.rep.IssueResource;
import net.heroicefforts.viable.android.rep.NullIssueResource;
import net.heroicefforts.viable.android.rep.TypePriorityStateResource;
import android.graphics.Color;
import android.util.Log;

/**
 * This class lazily caches all the issue states recognized by the JIRA repository. 
 * 
 * @author jevans
 *
 */
public class JIRAResourceFlyweight
{
	private static final String TAG = "JIRAResourceFlyweight";
	
	private static final String TYPE_IMPROVEMENT = "improvement";
	private static final String TYPE_FEATURE = "new feature";
	private static final String TYPE_BUG = "bug";
	private static final String[] TYPES = new String[] { TYPE_IMPROVEMENT, TYPE_FEATURE, TYPE_BUG };
	
	private static final String PRIORITY_TRIVIAL = "trivial";
	private static final String PRIORITY_MINOR = "minor";
	private static final String PRIORITY_MAJOR = "major";
	private static final String PRIORITY_CRITICAL = "critical";
	private static final String PRIORITY_BLOCKER = "blocker";
	private static final String[] PRIORITIES = new String[] { PRIORITY_TRIVIAL, PRIORITY_MINOR, PRIORITY_MAJOR, PRIORITY_CRITICAL, PRIORITY_BLOCKER };
	
	private static final String STATE_OPEN = "open";
	private static final String STATE_CLOSED = "closed";
	private static final String STATE_RESOLVED = "resolved";
	private static final String[] STATES = new String[] { STATE_OPEN, STATE_CLOSED, STATE_RESOLVED };
	
	
	private static final Set<TypePriorityStateResource> DEFAULT_ALL_STATES;
	private static final Set<TypePriorityStateResource> DEFAULT_BUG_STATES;
	private static final Map<String, TypePriorityStateResource> ALL_STATES;
	
	
	public static IssueResource getState(String type, String priority, String state)
	{
		IssueResource resource = ALL_STATES.get(type + priority + state);
		if(resource == null)
		{
			Log.w(TAG, "No issue state defined for Type:  '" + type + "', Priority:  '" + priority + "', State:  '" + state + "'.  Returning no-op.");
			resource = new NullIssueResource(type, priority, state);
		}
		
		return resource;
	}
	
	public static Set<? extends IssueResource> getDefaultDefectStates()
	{
		return DEFAULT_BUG_STATES;
	}
	
	public static Set<? extends IssueResource> getDefaultStates()
	{
		return DEFAULT_ALL_STATES;
	}

	public static IssueResource getUninstallState()
	{
		return getState(TYPE_IMPROVEMENT, PRIORITY_MAJOR, STATE_OPEN);
	}
	
	static int getTypeColor(Issue issue)
	{
		String type = issue.getType();
		if(type.equals(TYPE_BUG))
			return Color.RED;
		else if(type.equals(TYPE_IMPROVEMENT))
			return Color.GREEN;
		else if(type.equals("new feature"))
			return Color.WHITE;
		else
			return Color.GRAY;
	}
	
	static int getPriorityColor(Issue issue)
	{
		String priority = issue.getPriority();
		if(PRIORITY_BLOCKER.equals(priority))
			return Color.MAGENTA;
		else if(PRIORITY_CRITICAL.equals(priority))
			return Color.RED;
		else if(PRIORITY_MAJOR.equals(priority))
			return Color.YELLOW;
		else if(PRIORITY_MINOR.equals(priority))
			return Color.rgb(160, 82, 45); //sienna
		else if(PRIORITY_TRIVIAL.equals(priority))
			return Color.GREEN;
		else
			return Color.GRAY;
	}

	static int getStateColor(Issue issue)
	{
		String state = issue.getState();
		if(state.equals(STATE_OPEN))
			return Color.WHITE;
		else if(state.equals(STATE_RESOLVED))
			return Color.GRAY;
		else if(state.equals(STATE_CLOSED))
			return Color.DKGRAY;
		else
			return Color.GRAY;
	}
	
	
	static {
		HashMap<String, TypePriorityStateResource> allStates = new LinkedHashMap<String, TypePriorityStateResource>();
		HashSet<TypePriorityStateResource> defBugs = new LinkedHashSet<TypePriorityStateResource>();
		putDefault(allStates, defBugs, TYPE_BUG, PRIORITY_BLOCKER, STATE_OPEN, R.string.bug_blocker_open_name, R.string.bug_blocker_open_desc, R.drawable.bug_ablaze);
		putDefault(allStates, defBugs, TYPE_BUG, PRIORITY_CRITICAL, STATE_OPEN, R.string.bug_critical_open_name, R.string.bug_critical_open_desc, R.drawable.bug_on_fire);
		putDefault(allStates, defBugs, TYPE_BUG, PRIORITY_MAJOR, STATE_OPEN, R.string.bug_major_open_name, R.string.bug_major_open_desc, R.drawable.bug_big);
		putDefault(allStates, defBugs, TYPE_BUG, PRIORITY_MINOR, STATE_OPEN, R.string.bug_minor_open_name, R.string.bug_minor_open_desc, R.drawable.bug_medium);
		putDefault(allStates, defBugs, TYPE_BUG, PRIORITY_TRIVIAL, STATE_OPEN, R.string.bug_trivial_open_name, R.string.bug_trivial_open_desc, R.drawable.bug_small);
		put(allStates, TYPE_BUG, ANY, STATE_RESOLVED, R.string.bug_resolved_name, R.string.bug_resolved_desc, R.drawable.bug_squished);
		put(allStates, TYPE_BUG, ANY, STATE_CLOSED, R.string.bug_closed_name, R.string.bug_closed_desc, R.drawable.bug_desiccated);
		
		DEFAULT_BUG_STATES = Collections.unmodifiableSet(defBugs);
		
		HashSet<TypePriorityStateResource> defAll = new LinkedHashSet<TypePriorityStateResource>(defBugs);
		put(allStates, TYPE_FEATURE, PRIORITY_BLOCKER, STATE_OPEN, R.string.feature_blocker_open_name, R.string.feature_blocker_open_desc, R.drawable.bulb_on_fire);
		putDefault(allStates, defAll, TYPE_FEATURE, PRIORITY_CRITICAL, STATE_OPEN, R.string.feature_critical_open_name, R.string.feature_critical_open_desc, R.drawable.bulb);
		put(allStates, TYPE_FEATURE, PRIORITY_MAJOR, STATE_OPEN, R.string.feature_major_open_name, R.string.feature_major_open_desc, R.drawable.bulb_half);
		put(allStates, TYPE_FEATURE, PRIORITY_MINOR, STATE_OPEN, R.string.feature_minor_open_name, R.string.feature_minor_open_desc, R.drawable.bulb_third);
		putDefault(allStates, defAll, TYPE_FEATURE, PRIORITY_TRIVIAL, STATE_OPEN, R.string.feature_trivial_open_name, R.string.feature_trivial_open_desc, R.drawable.bulb_off);		
		put(allStates, TYPE_FEATURE, ANY, STATE_RESOLVED, R.string.feature_resolved_name, R.string.feature_resolved_desc, R.drawable.bulb_cracked);
		put(allStates, TYPE_FEATURE, ANY, STATE_CLOSED, R.string.feature_closed_name, R.string.feature_closed_desc, R.drawable.bulb_negative);

		put(allStates, TYPE_IMPROVEMENT, PRIORITY_BLOCKER, STATE_OPEN, R.string.improvement_blocker_open_name, R.string.improvement_blocker_open_desc, R.drawable.imp_blocker);
		putDefault(allStates, defAll, TYPE_IMPROVEMENT, PRIORITY_CRITICAL, STATE_OPEN, R.string.improvement_critical_open_name, R.string.improvement_critical_open_desc, R.drawable.imp_critical);
		put(allStates, TYPE_IMPROVEMENT, PRIORITY_MAJOR, STATE_OPEN, R.string.improvement_major_open_name, R.string.improvement_major_open_desc, R.drawable.imp_major);
		put(allStates, TYPE_IMPROVEMENT, PRIORITY_MINOR, STATE_OPEN, R.string.improvement_minor_open_name, R.string.improvement_minor_open_desc, R.drawable.imp_minor);
		putDefault(allStates, defAll, TYPE_IMPROVEMENT, PRIORITY_TRIVIAL, STATE_OPEN, R.string.improvement_trivial_open_name, R.string.improvement_trivial_open_desc, R.drawable.imp_trivial);		
		put(allStates, TYPE_IMPROVEMENT, ANY, STATE_RESOLVED, R.string.improvement_resolved_name, R.string.improvement_resolved_desc, R.drawable.imp_resolved);
		put(allStates, TYPE_IMPROVEMENT, ANY, STATE_CLOSED, R.string.improvement_closed_name, R.string.improvement_closed_desc, R.drawable.imp_closed);
		
		DEFAULT_ALL_STATES = Collections.unmodifiableSet(defAll);
		ALL_STATES = Collections.unmodifiableMap(allStates);
	}


	private static final void putDefault(HashMap<String, TypePriorityStateResource> states, HashSet<TypePriorityStateResource> def,
			String type, String priority, String state, int nameRes, int descRes, int iconRes)
	{
		def.add(put(states, type, priority, state, nameRes, descRes, iconRes));
	}
	
	private static final TypePriorityStateResource put(HashMap<String, TypePriorityStateResource> states, String type, String priority,
			String state, int nameRes, int descRes, int iconRes)
	{
		JIRAIssueResource resource = new JIRAIssueResource(type, priority, state, nameRes, descRes, iconRes);
		for(String t : select(type, TYPES))
			for(String p : select(priority, PRIORITIES))
				for(String s : select(state, STATES))
					states.put(t + p + s, resource);

		return resource;
	}

	private static final String[] select(String type, String[] all)
	{
		if(ANY.equals(type))
			return all;
		else
			return new String[] { type };
	}
	
}
