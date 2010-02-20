package net.heroicefforts.viable.android.rep.it.auth;

import net.heroicefforts.viable.android.R;
import net.heroicefforts.viable.android.rep.NetworkException;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import android.widget.Toast;

public class CredsActivity extends PreferenceActivity
{
	private Preference passPref;
	private AccountAuthenticatorResponse response;
	private EditTextPreference emailPref;
	private Intent intent;
	
	@Override
	public void onCreate(Bundle saved)
	{
		super.onCreate(saved);
		addPreferencesFromResource(net.heroicefforts.viable.android.R.xml.account_preferences);
		passPref = findPreference("password");
		passPref.setOnPreferenceChangeListener(passwordChanged);
		response = getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
		response.onRequestContinued();
		emailPref = (EditTextPreference) findPreference("email");
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		Log.d("CredsActivity", "OnStart!");
		Log.d("CredsActivity", "Response:  " + response);
	}
	
	@Override
	public void finish()
	{
		
		if(intent == null)
		{
			response.onError(AccountManager.ERROR_CODE_CANCELED, "Authentication Canceled.");
			Intent intent = new Intent();
			intent.putExtra(AccountManager.KEY_AUTH_FAILED_MESSAGE, "Authentication Canceled.");
			setResult(CredsActivity.RESULT_CANCELED, intent);
		}
		else
			intent = null;

		super.finish();
	}
	
	private void setResponse(Intent intent)
	{
		this.intent = intent;
	}
	
	private OnPreferenceChangeListener passwordChanged = new OnPreferenceChangeListener() {

		public boolean onPreferenceChange(Preference preference, Object newValue)
		{
			String email = emailPref.getText();
			if(email != null)
			{
				try
				{
					String tokenType = getIntent().getStringExtra(GCLAccountAuthenticator.EXTRA_TOKEN_TYPE);
					String token = Authenticate.authenticate(email, (String) newValue, tokenType);
					Log.v("CredsActivity", token);
					AccountManager am = (AccountManager) getSystemService(Context.ACCOUNT_SERVICE);
					Bundle bundle = new Bundle();
					bundle.putString(AccountManager.KEY_ACCOUNT_NAME, email);
					bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, GCLAccountAuthenticator.ACCT_TYPE);
					bundle.putString(tokenType, token);
					am.addAccountExplicitly(new Account(email, GCLAccountAuthenticator.ACCT_TYPE), null, bundle);
					Intent intent = new Intent();
					intent.putExtras(bundle);
					response.onResult(bundle);
					setResult(RESULT_OK, intent);
					setResponse(intent);
					finish();
				}
				catch (AuthenticationException e)
				{
					Toast.makeText(CredsActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
				}
				catch (NetworkException e)
				{
					Toast.makeText(CredsActivity.this, getString(R.string.fatal_error) + ":  " + e.getMessage(), Toast.LENGTH_LONG).show();
					Intent intent = new Intent();
					intent.putExtra(AccountManager.KEY_AUTH_FAILED_MESSAGE, "Auth failed:  " + e.getMessage());
					response.onError(AccountManager.ERROR_CODE_NETWORK_ERROR, e.getMessage());
					setResult(CredsActivity.RESULT_CANCELED, intent);
					setResponse(intent);
					finish();
				}
				
			}
			
			return false;
		}
	};
}
