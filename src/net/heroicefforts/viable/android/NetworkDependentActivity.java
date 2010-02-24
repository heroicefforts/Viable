package net.heroicefforts.viable.android;

/**
 * Interface implemented by Activities that rely upon NetworkDependentDialog to manage connectivity failures.
 * 
 * @author jevans
 *
 */
public interface NetworkDependentActivity
{
	/**
	 * Allows activities to delay instantiation of components dependent upon network connectivity.
	 * 
	 * NetworkDependentDialog will invoked this method once per instance.  If the network is available at construction time, then
	 * it will be invoked immediately.  Otherwise, it will be invoked the first time that the network become available.
	 */
	void initOnNetworkAvailability();

	/**
	 * Invoked if the user chooses to end the application because network connectivity will not be restored.  The application
	 * should perform necessary cleanup and finish.  Connectivity monitoring will not longer be active once this method has been invoked.
	 */
	void giveUp();

}
