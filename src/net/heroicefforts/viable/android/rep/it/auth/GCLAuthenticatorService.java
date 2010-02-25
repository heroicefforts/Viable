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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Service wrapper for the authenticator.
 * 
 * @author jevans
 *
 */
public class GCLAuthenticatorService extends Service
{
	private GCLAccountAuthenticator authenticator;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		authenticator = new GCLAccountAuthenticator(this);
	}
	
	@Override
	public IBinder onBind(Intent arg0)
	{
		return authenticator.getIBinder();
	}

}
