package com.goldin.gcommons

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * ????????????????????????????????????????
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * @author Evgeny
 * Date: 12/27/10
 * Time: 11:26 PM
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
class Base
{
    static final Map<String, Logger> LOGGERS = [:]

    
    static Logger getLog( Object o )
    {
        assert ( o != null )
        LOGGERS[ o.class ] = LOGGERS[ o.class ] ?: LoggerFactory.getLogger( o.class )
    }
}
