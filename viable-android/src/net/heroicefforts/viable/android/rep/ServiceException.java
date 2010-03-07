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
