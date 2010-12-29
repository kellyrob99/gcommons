package com.goldin.gcommons.beans

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Base class for other beans, provides reusable functionality for all of them.
 */
class BaseBean
{
    /**
     * Map of loggers for each bean
     */
    static final Map<Class< ? extends BaseBean>, Logger> LOGGERS = [:]

    
    static Logger getLog( BaseBean o )
    {
        assert ( o != null ) && ( o.class != null )
        LOGGERS[ o.class ] = LOGGERS[ o.class ] ?: LoggerFactory.getLogger( o.class )
    }
}
