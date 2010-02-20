package net.heroicefforts.viable.android;

import net.heroicefforts.viable.android.rep.CreateException;
import net.heroicefforts.viable.android.rep.ServiceException;
import android.content.Context;
import android.widget.Toast;


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
