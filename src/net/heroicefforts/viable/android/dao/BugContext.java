package net.heroicefforts.viable.android.dao;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class BugContext extends Issue implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String model = android.os.Build.MODEL;
	private String device = android.os.Build.DEVICE;
	private int version = android.os.Build.VERSION.SDK_INT;
	
	
	public BugContext()
	{
		//empty
	}
	
//	public BugContext(String json) throws JSONException
//	{
//		super(json);
//	}

	public void setError(Throwable t)
	{
		this.stacktrace = toString(t);
	}
	
	public void store(Context ctx)
	{
		SharedPreferences prefs = ctx.getSharedPreferences("bug_report", Activity.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putBoolean("enqueued", true);
		editor.putString("stacktrace", stacktrace);
		editor.putString("app_name", appName);
		editor.putString("app_version_name", getVersionName());
		editor.commit();
	}

	public static BugContext load(Context ctx)
	{
		SharedPreferences prefs = ctx.getSharedPreferences("bug_report", Activity.MODE_PRIVATE);
		BugContext retVal = null;
		
		if(prefs.getBoolean("enqueued", false))
		{
			retVal = new BugContext();
			retVal.stacktrace = prefs.getString("stacktrace", null);
			retVal.appName = prefs.getString("app_name", null);
			retVal.setVersionName(prefs.getString("app_version_name", null));
							
			Editor editor = prefs.edit();
			editor.clear();
			editor.commit();
		}
		
		return retVal;
	}
	
	public static BugContext load(Intent intent)
	{
		BugContext ctx = null;
		if(intent.getCategories() != null && intent.getCategories().contains("android.intent.category.BUGREPORT_JIRA"))
		{
			ctx = new BugContext();
			ctx.stacktrace = intent.getStringExtra("stacktrace");
			ctx.appName = intent.getStringExtra("app_name");
			ctx.setVersionName(intent.getStringExtra("app_version_name"));
		}
		
		return ctx;
	}
	
	public Intent getJIRAIntent()
	{
		Intent intent = new Intent("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.BUGREPORT_JIRA");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		intent.putExtra("stacktrace", stacktrace);
		intent.putExtra("app_name", appName);
		intent.putExtra("app_version_name", getVersionName());
		intent.putExtra("phone_model", model);
		intent.putExtra("phone_device", device);
		intent.putExtra("phone_version", version);
		
		return intent;
	}
	
	private static String toString(Throwable t)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		t.printStackTrace(ps);
		ps.flush();
		return baos.toString(); 
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

	public String getPhoneModel()
	{
		return model;
	}

	public String getPhoneDevice()
	{
		return device;
	}

	public int getPhoneVersion()
	{
		return version;
	}

	@Override
	public void copy(Issue issue)
	{
		super.copy(issue);
		
		if(issue instanceof BugContext)
		{
			BugContext bc = (BugContext) issue;
			this.model = bc.model;
			this.device = bc.device;
			this.version = bc.version;
		}

	}
	
	/**
	 * @deprecated
	 * @param version
	 */
	public void setVersionName(String version)
	{
		if(version == null)
			this.affectedVersions = new String[0];
		else
			this.affectedVersions = new String[] { version };
	}
	
	/**
	 * @deprecated
	 * @return
	 */
	public String getVersionName()
	{
		if(this.affectedVersions == null || this.affectedVersions.length == 0)
			return null;
		else
			return this.affectedVersions[0];
	}
}
