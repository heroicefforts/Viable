package net.heroicefforts.viable.android;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class BugContext implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private static final String model = android.os.Build.MODEL;
	private static final String device = android.os.Build.DEVICE;
	private static final int version = android.os.Build.VERSION.SDK_INT;
	
	private String exc;
	private String appName;
	private String pkgName;
	private int versionCode = -1;
	private String versionName;
	
	public void setAppData(Context ctx)
	{
		appName = ctx.getApplicationContext().getApplicationInfo().loadLabel(ctx.getPackageManager()).toString();			
		PackageManager manager = ctx.getPackageManager();
        try
		{
			PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
			pkgName = info.packageName;
			versionCode = info.versionCode;
			versionName = info.versionName;
		}
		catch (NameNotFoundException e)
		{
			//should never occur.
		}			
	}
	
	public void setError(Throwable t)
	{
		this.exc = toString(t);
	}
	
	public void store(Context ctx)
	{
		SharedPreferences prefs = ctx.getSharedPreferences("bug_report", Activity.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putBoolean("enqueued", true);
		editor.putString("stacktrace", exc);
		editor.putString("app_name", appName);
		editor.putString("pkg_name", pkgName);
		editor.putInt("app_version_code", versionCode);
		editor.putString("app_version_name", versionName);
		editor.commit();
	}

	public static BugContext load(Context ctx)
	{
		SharedPreferences prefs = ctx.getSharedPreferences("bug_report", Activity.MODE_PRIVATE);
		BugContext retVal = null;
		
		if(prefs.getBoolean("enqueued", false))
		{
			retVal = new BugContext();
			retVal.exc = prefs.getString("stacktrace", null);
			retVal.appName = prefs.getString("app_name", null);
			retVal.pkgName = prefs.getString("pkg_name", null);
			retVal.versionCode = prefs.getInt("app_version_code", -1);
			retVal.versionName = prefs.getString("app_version_name", null);
							
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
			ctx.exc = intent.getStringExtra("stacktrace");
			ctx.appName = intent.getStringExtra("app_name");
			ctx.pkgName = intent.getStringExtra("pkg_name");
			ctx.versionCode = intent.getIntExtra("app_version_code", -1);
			ctx.versionName = intent.getStringExtra("app_version_name");
		}
		
		return ctx;
	}
	
	public Intent getJIRAIntent()
	{
		Intent intent = new Intent("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.BUGREPORT_JIRA");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		intent.putExtra("stacktrace", exc);
		intent.putExtra("app_name", appName);
		intent.putExtra("pkg_name", pkgName);
		intent.putExtra("app_version_code", versionCode);
		intent.putExtra("app_version_name", versionName);
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

	public String getStacktrace()
	{
		return exc;
	}

	public String getAppName()
	{
		return appName;
	}

	public String getPkgName()
	{
		return pkgName;
	}

	public int getVersionCode()
	{
		return versionCode;
	}

	public String getVersionName()
	{
		return versionName;
	}
}
