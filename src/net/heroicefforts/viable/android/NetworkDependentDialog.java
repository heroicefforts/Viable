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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Monitors network connectivity.  If connectivity is lost, then a modal dialog is raised prompting the user to 
 * re-establish connectivity or close the application.  If connectivity is restored, then the dialog will close.  If the user
 * chooses to end the application, then giveUp will be invoked on the parent activity.
 * 
 * This class supports delayed network dependent activity creation logic by invoking initOnNetworkAvailability.  
 * 
 * @author jevans
 *
 */
public class NetworkDependentDialog extends BroadcastReceiver
{
	private NetworkDependentActivity parent;
	protected AlertDialog dialog;
	private Activity activity;
	private boolean init;

	/**
	 * Constructor.  Takes an Activity that implements NetworkDependentActivity.
	 * 
	 * @param parent the parent to which the modal dialog will bind.
	 */
	public NetworkDependentDialog(NetworkDependentActivity parent)
	{
		this.activity = verifyNonNullActivity(parent);
		this.parent = parent;

		activity.registerReceiver(this, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		
		if(disconnected())
		{
			activity.setVisible(false);
			showNetworkDialog();
		}
		else
			networkEstablished();
	}
	
	/**
	 * Validate that the activity is not null and is an Activity.
	 * @param parent the dependent activity
	 * @return the casted activity.
	 */
	private Activity verifyNonNullActivity(NetworkDependentActivity parent)
	{
		if(parent == null)
			throw new IllegalArgumentException("The NetworkDependentActivity entity must not be null.");
		if(!(parent instanceof Activity))
			throw new IllegalArgumentException("Implementors of NetworkDependentActivity must extend Activity.");
		else
			return (Activity) parent;
	}

	/**
	 * Deallocates resources.  This method should be invoked by the parent's finish method. 
	 */
	public void finish()
	{
		activity.unregisterReceiver(this);
	}

	/**
	 * Cleans up the dialog resources and invokes the parent to giveUp.
	 */
	private void giveUp()
	{
		finish();
		parent.giveUp();		
	}
	
	/**
	 * Invoked by the system to notify of a network connectivity change broadcast.
	 * 
	 * @param context the system context
	 * @param intent the broadcast intention.
	 */
	@Override
	public void onReceive(Context context, Intent intent)
	{
		if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false))
		{
			activity.runOnUiThread(new Runnable() {
				public void run()
				{
					if(dialog == null)
						showNetworkDialog();
				}
			});
		}
		else
		{
			activity.runOnUiThread(new Runnable() {
				public void run()
				{
					if(dialog != null)
						dialog.getButton(Dialog.BUTTON_POSITIVE).performClick();
				}
			});				
		}
	}

	/**
	 * Instantiates the dialog and displays the network unavailable dialog box.
	 */
	protected void showNetworkDialog()
	{
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.disconnected_title);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	if(!disconnected())
            	{
            		networkEstablished();
            	}
            	else
            	{
            		dialog.dismiss();
            		showNetworkDialog();
            	}
            }

        });
        builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                giveUp();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
            	if(disconnected())
            		giveUp();
            	else
            	{
            		networkEstablished();
            	}
            }
        });
        builder.setMessage(activity.getString(R.string.disconnected_msg));
        dialog = builder.create();
        dialog.show();		
	}
	
	/**
	 * Invokes the necessary logic when network availability exists or reappears.
	 */
	private void networkEstablished()
	{
		if(!init)
		{
			parent.initOnNetworkAvailability();
			init = true;
		}
		if(dialog != null)
		{
			dialog.dismiss();
			dialog = null;
		}
		activity.setVisible(true);

	}
	
	/**
	 * @return true if no network connectivity is available.
	 */
	protected boolean disconnected()
	{
		ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo net = connMgr.getActiveNetworkInfo();
		return net == null || !net.isAvailable();
	}
}
