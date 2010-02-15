package net.heroicefforts.viable.android;

import org.json.JSONException;

public class Bug extends Issue
{
	private String model = android.os.Build.MODEL;
	private String device = android.os.Build.DEVICE;
	private int version = android.os.Build.VERSION.SDK_INT;
	
	private String exc;
	private String pkgName;
	private int versionCode = -1;
	private String versionName;

	public Bug(String json) throws JSONException
	{
		super(json);
		// TODO Auto-generated constructor stub
	}
	
}
