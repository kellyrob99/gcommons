package com.goldin.gcommons

import java.util.regex.Pattern


/**
 * Various constants
 */
class Constants
{
    static final String  CONTEXT_PATH     = '/gcommons-application-context.xml'
    static final String  ZYMIC_FTP        = 'ftp://evgenyg_zxq:sdaed432e23@evgenyg.zxq.net:/'
    static final File    USER_DIR         = new File( System.getProperty( "user.dir"  ))
    static final File    USER_HOME        = new File( System.getProperty( "user.home" ))
    static final Pattern NETWORK_PATTERN  = ~/^(?i)(http|scp|ftp):(?:\/)+(.+):(.+)@(.+):(.+)$/
}
