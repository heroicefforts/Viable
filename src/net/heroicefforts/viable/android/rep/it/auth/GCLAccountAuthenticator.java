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

public class GCLAccountAuthenticator extends AbstractAccountAuthenticator
{
	private static final String TAG = "GCLAccountAuthenticator";
	
	public static final String ACCT_TYPE = "net.heroicefforts.google.auth";
	public static final String TOKEN_TYPE_ISSUE_TRACKER = "code";
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
	public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options)
	{
		Log.d(TAG, "confirmCredentials()");
		return null;
	}

	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response, String accountType)
	{
		Log.d(TAG, "editProperties()");
		
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features)
			throws NetworkErrorException
	{
		Log.d(TAG, "hasFeatures");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType,
			Bundle options)
	{
		Log.d(TAG, "updateCredentials");
		// TODO Auto-generated method stub
		return null;
	}

}
