package net.heroicefforts.viable.android;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import android.content.res.ColorStateList;
import android.graphics.Color;

public class IssueState
{
	private static final IssueState BUG_BLOCKER_OPEN;
	private static final IssueState BUG_CRITICAL_OPEN;
	private static final IssueState BUG_MAJOR_OPEN;
	private static final IssueState BUG_MINOR_OPEN;
	private static final IssueState BUG_TRIVIAL_OPEN;
	private static final IssueState BUG_RESOLVED;
	private static final IssueState BUG_CLOSED;
	public static final Set<IssueState> DEFAULT_BUG_STATES;
	
	private static final IssueState FEATURE_BLOCKER_OPEN;
	private static final IssueState FEATURE_CRITICAL_OPEN;
	private static final IssueState FEATURE_MAJOR_OPEN;
	private static final IssueState FEATURE_MINOR_OPEN;
	private static final IssueState FEATURE_TRIVIAL_OPEN;
	private static final IssueState FEATURE_RESOLVED;
	private static final IssueState FEATURE_CLOSED;
	public static final Set<IssueState> DEFAULT_FEATURE_STATE;
	
	private String type;
	private String priority;
	private String state;
	private int nameRes;
	private int descRes;
	private int iconRes;
	
	
	private IssueState(String type, String priority, String state, int name, int desc, int iconRes)
	{
		super();
		this.type = type;
		this.priority = priority;
		this.state = state;
		this.nameRes = name;
		this.descRes = desc;
		this.iconRes = iconRes;
	}

	public String getType()
	{
		return type;
	}


	public String getPriority()
	{
		return priority;
	}


	public String getState()
	{
		return state;
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
		IssueState other = (IssueState) obj;
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

	public int getDescRes()
	{
		return descRes;
	}

	public int getIconRes()
	{
		return iconRes;
	}

	public int getNameRes()
	{
		return nameRes;
	}

	public static IssueState getState(Issue issue)
	{
		return getState(issue.getType(), issue.getPriority(), issue.getState());
	}
	
	public static IssueState getState(String type, String priority, String state)
	{
		if(type.equals("bug"))
		{
			if(state.equals("closed"))
				return BUG_CLOSED;
			else if(state.equals("resolved"))
				return BUG_RESOLVED;
			else
			{
				if(priority.equals("blocker"))
					return BUG_BLOCKER_OPEN;
				else if(priority.equals("critical"))
					return BUG_CRITICAL_OPEN;
				else if(priority.equals("major"))
					return BUG_MAJOR_OPEN;
				else if(priority.equals("minor"))
					return BUG_MINOR_OPEN;
				else
					return BUG_TRIVIAL_OPEN;
			}
		}
		else if(type.equals("new feature"))
		{
			if(state.equals("closed"))
				return FEATURE_CLOSED;
			else if(state.equals("resolved"))
				return FEATURE_RESOLVED;
			else
			{
				if(priority.equals("blocker"))
					return FEATURE_BLOCKER_OPEN;
				else if(priority.equals("critical"))
					return FEATURE_CRITICAL_OPEN;
				else if(priority.equals("major"))
					return FEATURE_MAJOR_OPEN;
				else if(priority.equals("minor"))
					return FEATURE_MINOR_OPEN;
				else
					return FEATURE_TRIVIAL_OPEN;
			}
		}
		else
			return null;		
	}
	
	static {
		Set<IssueState> states = new LinkedHashSet<IssueState>();		
		states.add(BUG_BLOCKER_OPEN = new IssueState("bug", "blocker", "open", R.string.bug_blocker_open_name, R.string.bug_blocker_open_desc, R.drawable.bug_ablaze));
		states.add(BUG_CRITICAL_OPEN = new IssueState("bug", "critical", "open", R.string.bug_critical_open_name, R.string.bug_critical_open_desc, R.drawable.bug_on_fire));
		states.add(BUG_MAJOR_OPEN = new IssueState("bug", "major", "open", R.string.bug_major_open_name, R.string.bug_major_open_desc, R.drawable.bug_big));
		states.add(BUG_MINOR_OPEN = new IssueState("bug", "minor", "open", R.string.bug_minor_open_name, R.string.bug_minor_open_desc, R.drawable.bug_medium));
		states.add(BUG_TRIVIAL_OPEN = new IssueState("bug", "trivial", "open", R.string.bug_trivial_open_name, R.string.bug_trivial_open_desc, R.drawable.bug_small));
		BUG_RESOLVED = new IssueState("bug", "*", "resolved", R.string.bug_resolved_name, R.string.bug_resolved_desc, R.drawable.bug_squished);
		BUG_CLOSED = new IssueState("bug", "*", "closed", R.string.bug_closed_name, R.string.bug_closed_desc, R.drawable.bug_desiccated);
		
		DEFAULT_BUG_STATES = Collections.unmodifiableSet(states);
		
		states = new LinkedHashSet<IssueState>();
		FEATURE_BLOCKER_OPEN = new IssueState("feature", "blocker", "open", R.string.feature_blocker_open_name, R.string.feature_blocker_open_desc, R.drawable.bulb_on_fire);
		states.add(FEATURE_CRITICAL_OPEN = new IssueState("feature", "critical", "open", R.string.feature_critical_open_name, R.string.feature_critical_open_desc, R.drawable.bulb));
		FEATURE_MAJOR_OPEN = new IssueState("feature", "major", "open", R.string.feature_major_open_name, R.string.feature_major_open_desc, R.drawable.bulb_half);
		FEATURE_MINOR_OPEN = new IssueState("feature", "minor", "open", R.string.feature_minor_open_name, R.string.feature_minor_open_desc, R.drawable.bulb_third);
		states.add(FEATURE_TRIVIAL_OPEN = new IssueState("feature", "trivial", "open", R.string.feature_trivial_open_name, R.string.feature_trivial_open_desc, R.drawable.bulb_off));		
		FEATURE_RESOLVED = new IssueState("feature", "*", "resolved", R.string.feature_resolved_name, R.string.feature_resolved_desc, R.drawable.bulb_cracked);
		FEATURE_CLOSED = new IssueState("feature", "*", "closed", R.string.feature_closed_name, R.string.feature_closed_desc, R.drawable.bulb_negative);
		DEFAULT_FEATURE_STATE = Collections.unmodifiableSet(states);
	}

	public static int getTypeColor(Issue issue)
	{
		String type = issue.getType();
		if(type.equals("bug"))
			return Color.RED;
		else if(type.equals("improvement"))
			return Color.GREEN;
		else if(type.equals("new feature"))
			return Color.WHITE;
		else
			return Color.GRAY;
	}
	
	public static int getPriorityColor(Issue issue)
	{
		String priority = issue.getPriority();
		if("blocker".equals(priority))
			return Color.MAGENTA;
		else if("critical".equals(priority))
			return Color.RED;
		else if("major".equals(priority))
			return Color.YELLOW;
		else if("minor".equals(priority))
			return Color.rgb(160, 82, 45); //sienna
		else if("trivial".equals(priority))
			return Color.GREEN;
		else
			return Color.GRAY;
	}

	public static int getStateColor(Issue issue)
	{
		String state = issue.getState();
		if(state.equals("open"))
			return Color.WHITE;
		else if(state.equals("resolved"))
			return Color.GRAY;
		else if(state.equals("closed"))
			return Color.DKGRAY;
		else
			return Color.GRAY;
	}
	
}
