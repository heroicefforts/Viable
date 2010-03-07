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
