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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;


import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * This class is used to interact with the Google Client Login protocol to attain user tokens for Google services.
 * 
 * @author jevans
 *
 */
public class Authenticate
{
	private static final String TAG = "Authenticate";

	/**
	 * Attempts to authenticate the user using a pre-existing stored authentication token.  If an account exists, but no such token 
	 * exists, then the user will be prompted by the account authenticator to re-enter their Google credentials to generate the new token.
	 * 
	 * @param act the calling activity
	 * @return the authentication token for the requested service or null if there is no Google Account.
	 * @throws AuthenticatorException if an error occurs during authentication.
	 * @throws OperationCanceledException
	 * @throws IOException
	 */
	public static String authenticate(Activity act, String serviceCode)
		throws AuthenticatorException, OperationCanceledException, IOException
	{
		AccountManager mgr = AccountManager.get(act); 
		Account[] temp = mgr.getAccounts();
		for(Account t : temp)
			Log.d(TAG, "Account Type/Name:  " + t.type + "/" + t.name);
        Account[] accts = mgr.getAccountsByType(GCLAccountAuthenticator.ACCT_TYPE); 
        if(accts.length > 0)
        {
	        Account acct = accts[0]; 
	        AccountManagerFuture<Bundle> accountManagerFuture = mgr.getAuthToken(acct, serviceCode, null, act, null, null); 
	        Bundle authTokenBundle = accountManagerFuture.getResult(); 
	        String authToken = authTokenBundle.get(AccountManager.KEY_AUTHTOKEN).toString();
	        
	        return authToken; 
        }
        else
        {
        	Log.e(TAG, "No google accounts registered for this device.");
        	return null;
        }
	}
	
	/**
	 * Directly posts to Google Client Login service to generate an authentication token.
	 * 
	 * @param username
	 * @param password
	 * @param service the code for the service desired e.g. code
	 * @return the authentication token for that service.
	 * @throws AuthenticationException if authentication failed.
	 * @throws NetworkException if there is an error communicating with the login service.
	 */
	public static final String authenticate(String username, String password, String service) 
		throws AuthenticationException, NetworkException
	{
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost post = new HttpPost("https://www.google.com/accounts/ClientLogin");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(12);
		nameValuePairs.add(new BasicNameValuePair("accountType", "HOSTED_OR_GOOGLE"));
		nameValuePairs.add(new BasicNameValuePair("Email", username));
		nameValuePairs.add(new BasicNameValuePair("Passwd", password));
		nameValuePairs.add(new BasicNameValuePair("service", service));
		nameValuePairs.add(new BasicNameValuePair("source", "heroicefforts-viable-1.0.0"));
		
		Log.d(TAG, "post params:  " + nameValuePairs.toString());
		int responseCode;
		String body;
		try
		{
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
			
			HttpResponse response = httpclient.execute(post);
			
			responseCode = response.getStatusLine().getStatusCode();
			body = readResponse(response);
		}
		catch (Exception e)
		{
			throw new NetworkException("Exception during authentication.", e);
		}
		
		if(HttpStatus.SC_OK == responseCode)
		{	
			Log.v(TAG, "Auth body response:  " + body);
			Pattern authPat = Pattern.compile("Auth=([A-Za-z0-9_-]+)");
			Matcher m = authPat.matcher(body);
			if(m.find())
			{
				String authToken = m.group(1);
				Log.v(TAG, "Auth token:  " + authToken);
				return authToken;
			}
			else
				throw new AuthenticationException(responseCode, "Couldn't locate Auth token in login response.");
		}		
		else
			throw new AuthenticationException(responseCode, "Authentication failed:  " + body);
	}
	
	private static String readResponse(HttpResponse response) throws IOException
	{
		InputStream instream = response.getEntity().getContent();
		Header contentEncoding = response.getFirstHeader("Content-Encoding");
		if(contentEncoding != null)
			Log.d(TAG, "Response content encoding was '" + contentEncoding.getValue() + "'");
		if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
			Log.d(TAG, "Handling GZIP response.");
		    instream = new GZIPInputStream(instream);
		}		

		BufferedInputStream bis = new BufferedInputStream(instream);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int read = 0;
		while((read = bis.read(buf)) > 0)
			baos.write(buf, 0, read);
		String body = baos.toString();
		Log.v(TAG, "Response:  " + body);
		return body;
	}
	
}
