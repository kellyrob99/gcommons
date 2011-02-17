package com.goldin.gcommons.util

import com.goldin.gcommons.beans.BaseBean
import com.goldin.gcommons.beans.FileBean
import groovy.io.FileType


/**
 * "File.metaClass.recurse" helper methods
 */
class RecurseHelper extends BaseBean
{

    /**
     * Container that holds callback invocation results.
     * See {@link #invokeCallback}
     */
    static class InvocationResult
    {
        boolean filterPass
        boolean invocationResult
    }


    /**
     * Injected by Spring
     */
    FileBean fileBean
        

    RecurseHelper ()
    {
    }

    
    /**
     * "File.metaClass.recurse" helper - handles directory provided.
     *
     * @param file         directory to handle
     * @param callback     callback to invoke
     * @param filter       file filter
     * @param fileType     type of callback file
     * @param filterType   type of filter callback file
     * @param stopOnFalse  whether recursive invocation should stop if callback invocation results in negative result
     * @param stopOnFilter whether recursive invocation should stop if filter type is "directory" and it returns false
     *
     * @return <code>false</code> if recursive iteration should be stopped,
     *         <code>true</code>  otherwise
     */
    void handleDirectory( File             directory,
                          Closure<?>       callback,
                          Closure<Boolean> filter,
                          FileType         fileType,
                          FileType         filterType,
                          boolean          stopOnFalse,
                          boolean          stopOnFilter )
    {
        verify.directory( directory )
        verify.notNull( callback, fileType, filterType, stopOnFalse )

        for ( File f in directory.listFiles())
        {
            def result          = invokeCallback( f, callback, filter, fileType, filterType )
            def recursiveInvoke = ( f.isDirectory() &&
                                    (( ! stopOnFilter ) || ( filterType != FileType.DIRECTORIES ) || ( result.filterPass )) &&
                                    (( ! stopOnFalse  ) || ( result.invocationResult )))
            if ( recursiveInvoke )
            {
                handleDirectory( f, callback, filter, fileType, filterType, stopOnFalse, stopOnFilter )
            }
        }
    }


    /**
     * "File.metaClass.recurse" helper - invokes callback provided.
     *
     * @param file       file or directory to handle
     * @param callback   callback to invoke
     * @param filter     file filter
     * @param fileType   type of callback file
     * @param filterType type of filter callback file
     *
     * @return callback invocation result
     */
    private InvocationResult invokeCallback ( File             file,
                                              Closure<?>       callback,
                                              Closure<Boolean> filter,
                                              FileType         fileType,
                                              FileType         filterType )
    {
        verify.exists( file )
        verify.notNull( callback, fileType, filterType )

        def fileTypeMatch   = fileBean.typeMatch( fileType,   file )
        def filterTypeMatch = fileBean.typeMatch( filterType, file )
        def result          = new InvocationResult()

        result.filterPass       = (( filter == null ) || ( ! filterTypeMatch ) || filter( file ))
        result.invocationResult = true

        if ( fileTypeMatch && result.filterPass )
        {
            Object callbackResult   = callback( file )
            result.invocationResult = general.choose( callbackResult as boolean, true )
        }

        result
    }
}
