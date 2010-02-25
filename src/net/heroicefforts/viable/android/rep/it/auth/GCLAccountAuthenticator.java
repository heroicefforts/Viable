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
package net.heroicefforts.viable.android.rep.it.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Authenticates Google credentials using the Google Client Login.  This would not be required if Google Android supported
 * all of Google's services instead of just cl and ah.
 * 
 * @author jevans
 *
 */
public class GCLAccountAuthenticator extends AbstractAccountAuthenticator
{
	private static final String TAG = "GCLAccountAuthenticator";

	/**
	 * Our Google Client Login account type definition for this authenticator.
	 */
	public static final String ACCT_TYPE = "net.heroicefforts.google.auth";
	
	/**
	 * The service name, authentication token type for Google Issue Tracker.
	 */
	public static final String TOKEN_TYPE_ISSUE_TRACKER = "code";
	
	/**
	 * Extra used to pass the requested token type to {@link CredsActivity}.
	 */
	public static final String EXTRA_TOKEN_TYPE = "tokenType";
	
	private AccountManager acctMgr;
	private Context context;
	
	
	public GCLAccountAuthenticator(Context context)
	{
		super(context);
		this.context = context;
		Log.d(TAG, "instantiated");
		acctMgr = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
	}

	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType,
			String[] requiredFeatures, Bundle options) throws NetworkErrorException
	{
		Log.d(TAG, "addAccount()");
		Bundle bundle = createLoginBundle(response, authTokenType);
		return bundle;
	}

	private Bundle createLoginBundle(AccountAuthenticatorResponse response, String authTokenType)
	{
		Bundle bundle = new Bundle();
		Intent intent = new Intent(context, CredsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
		intent.putExtra(EXTRA_TOKEN_TYPE, authTokenType);
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return bundle;
	}

	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType,
			Bundle options) throws NetworkErrorException
	{
		Log.d(TAG, "getAuthToken()");
		String authToken = acctMgr.getUserData(account, authTokenType);
		if(authToken != null)
		{ 
			Bundle bundle = new Bundle();
			bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
			bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
			bundle.putString(AccountManager.KEY_AUTHTOKEN, authToken);
			return bundle;
		}
		else
		{
			return createLoginBundle(response, authTokenType);
		}
	}

	@Override
	public String getAuthTokenLabel(String authTokenType)
	{
		Log.d(TAG, "getAuthTokenLabel()");

		return "Google Login for " + authTokenType;
	}

	/**
	 * Not supported.
	 */
	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options)
	{
		Log.d(TAG, "confirmCredentials()");
		return null;
	}

	/**
	 * Not supported.
	 */
	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response, String accountType)
	{
		Log.d(TAG, "editProperties()");
		return null;
	}

	/**
	 * Not supported.
	 */
	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features)
			throws NetworkErrorException
	{
		Log.d(TAG, "hasFeatures");
		return null;
	}

	/**
	 * Not supported.
	 */
	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType,
			Bundle options)
	{
		Log.d(TAG, "updateCredentials");
		return null;
	}

}
