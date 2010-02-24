package net.heroicefforts.viable.android.rep;

/**
 * Thrown if there is an issue connecting to the service or the service throws an unexpected error.
 * 
 * @author jevans
 *
 */
public class ServiceException extends Exception
{
	private static final long serialVersionUID = 1L;

	
	public ServiceException(String msg)
	{
		super(msg);
	}

	public ServiceException(String msg, Exception e)
	{
		super(msg, e);
	}

}
