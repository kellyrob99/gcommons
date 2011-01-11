package com.goldin.gcommons.beans

import java.util.regex.Pattern

 /**
 * Various constants
 */
class ConstantsBean extends BaseBean
{
    final String  CRLF             = System.getProperty( 'line.separator' )
    final String  USER_HOME        = System.getProperty( 'user.home' )
    final String  USER_DIR         = System.getProperty( 'user.dir' )

    final File    USER_DIR_FILE    = new File( USER_DIR )
    final File    USER_HOME_FILE   = new File( USER_HOME )

    final Pattern NETWORK_PATTERN  = ~/^(?i)(http|scp|ftp):(?:\/)+(.+):(.+)@(.+):(.+)$/

    final int    MILLIS_IN_SECOND  = 1000 // Milliseconds in a second
    final int    SECONDS_IN_MINUTE = 60   // Seconds in a minute
    final int    MILLIS_IN_MINUTE  = MILLIS_IN_SECOND * SECONDS_IN_MINUTE // Milliseconds in a minute
}
