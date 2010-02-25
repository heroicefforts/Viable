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
/**
 * Google doesn't seem to provide built-in access to Issue Tracker login as with Calendar and AppEngine.  So, this
 * package was implemented to leverage the Google Client Login service for authentication.  A custom authenticator was
 * implemented to store the client's Google username/email and authentication tokens Android conventions.  Passwords are
 * not retained. 
 */
package net.heroicefforts.viable.android.rep.it.auth;