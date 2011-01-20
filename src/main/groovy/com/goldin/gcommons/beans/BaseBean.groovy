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
    private static final Map<Class<? extends BaseBean>, Logger> LOGGERS = [:]


    /**
     * Injected by Spring
     */
    ConstantsBean constants
    VerifyBean    verify
    GeneralBean   general


    /**
     * Retrieves logger for the bean instance specified.
     * @param o bean instance
     * @return logger to use
     */
    static Logger getLog( BaseBean bean )
    {
        LOGGERS[ bean.class ] = LOGGERS[ bean.class ] ?: LoggerFactory.getLogger( bean.class )
    }


    /**
     * Retrieves first element in the array specified.
     *
     * @param objects array of objects
     * @return first element in the array specified, or <code>null</code> if it si empty
     */
    public <T> T first( T[] objects ) { objects.size() ? objects[ 0 ] : null }
}
