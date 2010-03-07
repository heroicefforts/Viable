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

import net.heroicefforts.viable.android.rep.CreateException;
import net.heroicefforts.viable.android.rep.ServiceException;
import android.content.Context;
import android.widget.Toast;

/**
 * Utility class for alerting users to errors that bubble to the UI in a uniform fashion.
 * 
 * @author jevans
 *
 */
public class Error
{
	public static void handle(Context ctx, CreateException ce)
	{
		Toast.makeText(ctx, ctx.getString(R.string.create_error_msg), Toast.LENGTH_LONG).show();
	}

	public static void handle(Context ctx, ServiceException ce)
	{
		Toast.makeText(ctx, ctx.getString(R.string.service_error_msg), Toast.LENGTH_LONG).show();
	}
}
