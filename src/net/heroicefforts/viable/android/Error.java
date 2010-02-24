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
