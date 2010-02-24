/**
 * Google doesn't seem to provide built-in access to Issue Tracker login as with Calendar and AppEngine.  So, this
 * package was implemented to leverage the Google Client Login service for authentication.  A custom authenticator was
 * implemented to store the client's Google username/email and authentication tokens Android conventions.  Passwords are
 * not retained. 
 */
package net.heroicefforts.viable.android.rep.it.auth;