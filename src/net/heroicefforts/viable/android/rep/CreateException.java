package net.heroicefforts.viable.android.rep;

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
