package net.heroicefforts.viable.android.rep;

public class NetworkException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	public NetworkException(String msg, Exception e)
	{
		super(msg, e);
	}
	
}
