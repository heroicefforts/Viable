package net.heroicefforts.viable.android.rep.it.auth;

/**
 * Thrown if there is a network error during authentication.
 * @author jevans
 *
 */
//TODO remove this exception
public class NetworkException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	public NetworkException(String msg, Exception e)
	{
		super(msg, e);
	}
	
}
