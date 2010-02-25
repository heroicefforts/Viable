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
package net.heroicefforts.viable.android;

import net.heroicefforts.viable.android.rep.RegEntry;
import android.content.Intent;

/**
 * This class is used to wrap the configuration surrounding uninstall commenting.
 * 
 * @author jevans
 *
 */
public class UninstallIntent extends Intent
{
	/**
	 * Constructor that configures to start the uninstall Activity.
	 * @param entry
	 */
	public UninstallIntent(RegEntry entry)
	{
		super(Intent.ACTION_MAIN);
		addCategory("android.intent.category.REPORT_UNINSTALL");
		addFlags(getFlags() | Intent.FLAG_ACTIVITY_NEW_TASK);
		this.putExtra("entry", entry);
	}

	/**
	 * Simple copy constructor for use within the uninstall activity.
	 * 
	 * @param intent the activitie's configured intent
	 */
	public UninstallIntent(Intent intent)
	{
		super(intent);
	}

	/**
	 * Return the uninstalled application's entry.
	 * @return
	 */
	public RegEntry getAppEntry()
	{
		return (RegEntry) this.getSerializableExtra("entry");
	}

	/**
	 * Return true if this is a properly configured uninstallation activity intent.
	 * @return
	 */
	public boolean isValid()
	{
		return getAppEntry() != null;
	}
	
}
