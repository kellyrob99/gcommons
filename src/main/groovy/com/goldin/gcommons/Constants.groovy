package com.goldin.gcommons

import java.util.regex.Pattern


/**
 * Various constants
 */
class Constants
{
    static final String  CONTEXT_PATH     = '/gcommons-application-context.xml'
    static final String  ZYMIC_FTP        = 'ftp://evgenyg_zxq:sdaed432e23@evgenyg.zxq.net:/'
    static final String  CRLF             = System.getProperty( 'line.separator' )
    static final String  USER_HOME        = System.getProperty( 'user.home' )
    static final String  USER_DIR         = System.getProperty( 'user.dir' )

    static final File    USER_DIR_FILE    = new File( USER_DIR )
    static final File    USER_HOME_FILE   = new File( USER_HOME )

    static final Pattern NETWORK_PATTERN  = ~/^(?i)(http|scp|ftp):(?:\/)+(.+):(.+)@(.+):(.+)$/

    static final int    MILLIS_IN_SECOND  = 1000 // Milliseconds in a second
    static final int    SECONDS_IN_MINUTE = 60   // Seconds in a minute
    static final int    MILLIS_IN_MINUTE  = MILLIS_IN_SECOND * SECONDS_IN_MINUTE // Milliseconds in a minute

}
