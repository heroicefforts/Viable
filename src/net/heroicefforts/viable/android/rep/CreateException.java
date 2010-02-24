package net.heroicefforts.viable.android.rep;

/**
 * This exception is thrown if there is an error creating a repository.
 * 
 * @author jevans
 *
 */
public class CreateException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public CreateException(String msg)
	{
		super(msg);
	}

	public CreateException(String msg, Exception e)
	{
		super(msg, e);
	}

}
