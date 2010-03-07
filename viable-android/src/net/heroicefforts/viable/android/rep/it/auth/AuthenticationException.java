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
