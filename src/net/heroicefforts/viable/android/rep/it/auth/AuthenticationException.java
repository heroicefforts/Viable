package net.heroicefforts.viable.android.rep.it.auth;

/**
 * Thrown if authentication fails.
 * 
 * @author jevans
 *
 */
public class AuthenticationException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	private int responseCode;

	
	public AuthenticationException(int responseCode, String msg)
	{
		super(msg);
		this.responseCode = responseCode;
	}

	public AuthenticationException(String msg, Exception e)
	{
		super(msg, e);
	}

	/**
	 * Return the response code sent by the authentication server.
	 * @return
	 */
	public int getResponseCode()
	{
		return responseCode;
	}

}
