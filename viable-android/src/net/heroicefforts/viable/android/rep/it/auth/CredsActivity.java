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

import net.heroicefforts.viable.android.R;
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
import android.widget.Toast;

/**
 * This activity collects the user's Google credentials authenticates them and creates the account.
 * 
 * @author jevans
 *
 */
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
					if(tokenType == null)
						tokenType = GCLAccountAuthenticator.TOKEN_TYPE_ISSUE_TRACKER; //TODO add pick list
					String token = Authenticate.authenticate(email, (String) newValue, tokenType);

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
