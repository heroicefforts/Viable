package net.heroicefforts.android.bugs;

import net.heroicefforts.viable.android.dist.ViableExceptionHandler;
import android.app.Activity;
import android.app.Service;
import android.content.Context;

public aspect ExceptionHandlerAspect
{
	pointcut ctxCreation(Context ctx) : (execution(* Activity+.onCreate(..)) || execution(* Service+.onCreate(..))) && this(ctx);
	
	before(Context a) : ctxCreation(a) 
	{		
		ViableExceptionHandler.register(a);
	}

}
 