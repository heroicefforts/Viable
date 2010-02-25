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
package net.heroicefforts.viable.android.rep.it;

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
 * This class lazily caches all the issue states recognized by the I.T. repository. 
 * 
 * @author jevans
 *
 */
public class ITResourceFlyweight
{
	private static final String TAG = "ITResourceFlyweight";
	
	private static final String TYPE_DEFECT = "Defect";
	private static final String TYPE_ENHANCEMENT = "Enhancement";
	private static final String TYPE_TASK = "Task";
	private static final String TYPE_REVIEW = "Review";
	private static final String TYPE_OTHER = "Other";
	private static final String[] TYPES = new String[] { TYPE_DEFECT, TYPE_ENHANCEMENT, TYPE_TASK, TYPE_REVIEW, TYPE_OTHER };

	private static final String PRIORITY_CRITICAL = "Critical";
	private static final String PRIORITY_HIGH = "High";
	private static final String PRIORITY_MEDIUM = "Medium";
	private static final String PRIORITY_LOW = "Low";
	private static final String[] PRIORITIES = new String[] { PRIORITY_CRITICAL, PRIORITY_HIGH, PRIORITY_MEDIUM, PRIORITY_LOW };

	private static final String STATE_OPEN = "open";
	private static final String STATE_CLOSED = "closed";
	private static final String[] STATES = new String[] { STATE_OPEN, STATE_CLOSED };
	
//	private static final String RESOLUTION_NEW = "New";
//	private static final String RESOLUTION_ACCEPTED = "Accepted";
//	private static final String RESOLUTION_STARTED = "Started";
//	private static final String RESOLUTION_FIXED = "Fixed";
//	private static final String RESOLUTION_VERIFIED = "Verified";
//	private static final String RESOLUTION_INVALID = "Invalid";
//	private static final String RESOLUTION_DUPLICATE = "Duplicate";
//	private static final String RESOLUTION_WONT_FIX = "WontFix";
//	private static final String RESOLUTION_DONE = "Done";
//	private static final String[] RESOLUTIONS = new String[] { RESOLUTION_NEW, RESOLUTION_ACCEPTED, RESOLUTION_STARTED, RESOLUTION_FIXED, RESOLUTION_VERIFIED, RESOLUTION_INVALID, RESOLUTION_DUPLICATE, RESOLUTION_WONT_FIX, RESOLUTION_DONE };
	
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

	static int getTypeColor(Issue issue)
	{
		String type = issue.getType();
		if(type.equals(TYPE_DEFECT))
			return Color.RED;
		else if(type.equals(TYPE_ENHANCEMENT))
			return Color.GREEN;
		else
			return Color.GRAY;
	}
	
	static int getPriorityColor(Issue issue)
	{
		String priority = issue.getPriority();
		if(PRIORITY_CRITICAL.equals(priority))
			return Color.RED;
		else if(PRIORITY_HIGH.equals(priority))
			return Color.YELLOW;
		else if(PRIORITY_MEDIUM.equals(priority))
			return Color.rgb(160, 82, 45); //sienna
		else if(PRIORITY_LOW.equals(priority))
			return Color.GREEN;
		else
			return Color.GRAY;
	}

	static int getStateColor(Issue issue)
	{
		String state = issue.getState();
		if(state.equals(STATE_OPEN))
			return Color.WHITE;
		else if(state.equals(STATE_CLOSED))
			return Color.DKGRAY;
		else
			return Color.GRAY;
	}
	
	
	static {
		HashMap<String, TypePriorityStateResource> allStates = new LinkedHashMap<String, TypePriorityStateResource>();
		HashSet<TypePriorityStateResource> defBugs = new LinkedHashSet<TypePriorityStateResource>();
		putDefault(allStates, defBugs, TYPE_DEFECT, PRIORITY_CRITICAL, STATE_OPEN, R.string.bug_critical_open_name, R.string.bug_critical_open_desc, R.drawable.bug_on_fire);
		putDefault(allStates, defBugs, TYPE_DEFECT, PRIORITY_HIGH, STATE_OPEN, R.string.bug_major_open_name, R.string.bug_major_open_desc, R.drawable.bug_big);
		putDefault(allStates, defBugs, TYPE_DEFECT, PRIORITY_MEDIUM, STATE_OPEN, R.string.bug_minor_open_name, R.string.bug_minor_open_desc, R.drawable.bug_medium);
		putDefault(allStates, defBugs, TYPE_DEFECT, PRIORITY_LOW, STATE_OPEN, R.string.bug_trivial_open_name, R.string.bug_trivial_open_desc, R.drawable.bug_small);
		put(allStates, TYPE_DEFECT, ANY, STATE_CLOSED, R.string.bug_closed_name, R.string.bug_closed_desc, R.drawable.bug_desiccated);
		
		DEFAULT_BUG_STATES = Collections.unmodifiableSet(defBugs);
		
		HashSet<TypePriorityStateResource> defAll = new LinkedHashSet<TypePriorityStateResource>(defBugs);
		putDefault(allStates, defAll, TYPE_ENHANCEMENT, PRIORITY_CRITICAL, STATE_OPEN, R.string.feature_critical_open_name, R.string.feature_critical_open_desc, R.drawable.bulb);
		put(allStates, TYPE_ENHANCEMENT, PRIORITY_HIGH, STATE_OPEN, R.string.feature_major_open_name, R.string.feature_major_open_desc, R.drawable.bulb_half);
		put(allStates, TYPE_ENHANCEMENT, PRIORITY_MEDIUM, STATE_OPEN, R.string.feature_minor_open_name, R.string.feature_minor_open_desc, R.drawable.bulb_third);
		putDefault(allStates, defAll, TYPE_ENHANCEMENT, PRIORITY_LOW, STATE_OPEN, R.string.feature_trivial_open_name, R.string.feature_trivial_open_desc, R.drawable.bulb_off);		
		put(allStates, TYPE_ENHANCEMENT, ANY, STATE_CLOSED, R.string.feature_closed_name, R.string.feature_closed_desc, R.drawable.bulb_negative);

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
		ITIssueResource resource = new ITIssueResource(type, priority, state, nameRes, descRes, iconRes);
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
