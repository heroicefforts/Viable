package net.heroicefforts.viable.android.dist;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.util.Log;

public class ViableExceptionHandler implements UncaughtExceptionHandler
{
	private WeakReference<Context> activityRef;
	private UncaughtExceptionHandler parent;
	
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
