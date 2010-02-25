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
package net.heroicefforts.viable.android.dist;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.util.Log;

/**
 * This class handles linking Viable's exception handling to client applications.  Users that want to be have their
 * crashes reported by Viable should invoke {@link register} from within their main Activity or Service.
 * 
 * @author jevans
 *
 */
public class ViableExceptionHandler implements UncaughtExceptionHandler
{
	private WeakReference<Context> activityRef;
	private UncaughtExceptionHandler parent;
	
	/**
	 * Registers a client activity or service so that exceptions will be reported by Viable.  Registration will not replace the
	 * existing exception handler, but chain to it upon completion.<br/>
	 * <br/>
	 * This call is the only direct code call required by Viable clients.<br/>
	 * <br/>
	 * Calls to this method are idempotent.
	 *  
	 * @param ctx your main service or activity.
	 */
	public static void register(Context ctx)
	{
		Context oldCtx = null;
		UncaughtExceptionHandler oldHandler = Thread.getDefaultUncaughtExceptionHandler();
		
		if(oldHandler instanceof ViableExceptionHandler)
			oldCtx = ((ViableExceptionHandler) oldHandler).activityRef.get();
		
		if(oldCtx == null)
		{
			Log.d("ViableExceptionHandler", "Original ExceptionHandler:  " + Thread.getDefaultUncaughtExceptionHandler());
			Thread.setDefaultUncaughtExceptionHandler(new ViableExceptionHandler(ctx.getApplicationContext()));
		}					
	}
	
	private ViableExceptionHandler(Context ctx)
	{
		activityRef = new WeakReference<Context>(ctx);
		parent = Thread.getDefaultUncaughtExceptionHandler();
	}
	
	/**
	 * Invoke Viable to handle exception reporting, if available on the client's phone.
	 */
	public void uncaughtException(Thread thread, Throwable ex)
	{		
		Context ctx = activityRef.get();
		
		if(activityRef.get() != null)
		{ 
			String appName = ctx.getApplicationContext().getApplicationInfo().loadLabel(ctx.getPackageManager()).toString();			
			Log.d("ViableExceptionHandler", "Exception in app '" + appName + "'.");
			BugReportIntent intent = new BugReportIntent(appName, ex);
			ctx.sendBroadcast(intent);
			Log.d("ExceptionHandler", "Storing bug context.");
		}
		else
		{
			Log.d("ViableExceptionHandler", "Context was already collected.  Could not report exception.");
		}
		
		Log.d("ViableExceptionHandler", "Parent:  " + parent);
		if(parent != null)
			parent.uncaughtException(thread, ex);

	}

}
