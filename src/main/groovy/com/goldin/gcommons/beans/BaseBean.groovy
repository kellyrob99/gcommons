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
    private static final Map<Class< ? extends BaseBean>, Logger> LOGGERS = [:]

    /**
     * Injected by Spring
     */
    ConstantsBean constants


    /**
     * Retrieves logger for the bean class specified.
     * @param o bean class
     * @return logger to use
     *
     * @see #getLog(BaseBean)
     */
    static Logger getLog( Class<? extends BaseBean> c )
    {
        assert c != null
        LOGGERS[ c ] = LOGGERS[ c ] ?: LoggerFactory.getLogger( c )
    }


    /**
     * Retrieves logger for the bean instance specified.
     * @param o bean instance
     * @return logger to use
     *
     * @see #getLog(Class<? extends com.goldin.gcommons.beans.BaseBean>)
     */
    static Logger getLog( BaseBean o ) { getLog( o.class ) }


    /**
     * Retrieves first element in the array specified.
     *
     * @param objects array of objects
     * @return first element in the array specified, or <code>null</code> if it si empty
     */
    static <T> T first( T[] objects ) { objects.size() ? objects[ 0 ] : null }
}
