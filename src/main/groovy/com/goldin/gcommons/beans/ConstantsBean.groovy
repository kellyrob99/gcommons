package com.goldin.gcommons.beans

import java.util.regex.Pattern

 /**
 * Various constants
 */
class ConstantsBean extends BaseBean
{
    public final String  CRLF             = System.getProperty( 'line.separator' )
    public final String  USER_HOME        = System.getProperty( 'user.home' )
    public final String  USER_DIR         = System.getProperty( 'user.dir' )

    public final File    USER_DIR_FILE    = new File( USER_DIR )
    public final File    USER_HOME_FILE   = new File( USER_HOME )

    public final Pattern NETWORK_PATTERN  = ~/^(?i)(http|scp|ftp):(?:\/)+(.+):(.+)@(.+):(.+)$/

    public final int    MILLIS_IN_SECOND  = 1000 // Milliseconds in a second
    public final int    SECONDS_IN_MINUTE = 60   // Seconds in a minute
    public final int    MILLIS_IN_MINUTE  = MILLIS_IN_SECOND * SECONDS_IN_MINUTE // Milliseconds in a minute
}
