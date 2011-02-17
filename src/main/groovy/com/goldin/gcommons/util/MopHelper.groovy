package com.goldin.gcommons.util

import groovy.io.FileType
import com.goldin.gcommons.beans.BaseBean



/**
 * MOP updates implementations.
 */
class MopHelper extends BaseBean
{

    MopHelper ()
    {
    }

    
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
     * Splits object to "pieces" with an "each"-like function specified by name.
     *
     * @param delegate original delegate object
     * @param args     invocation object, method and result type (optional)
     */
    List splitWith( Object delegate, Object[] args )
    {
        Object o           // Invocation object
        String methodName  // name of method to invoke
        Class  type        // Type of elements returned

        switch( args.size())
        {
        /**
         * [0] = {java.lang.String@2412}"eachByte" - invocation method
         */
            case 1 : o          = delegate
                     methodName = args[ 0 ]
                     type       = null
                     break
        /**
         * Two options:
         *
         * [0] = {java.lang.String@1944}"eachLine"              - invocation method
         * [1] = {java.lang.Class@1585}"class java.lang.Object" - type
         *
         * or
         *
         * [0] = {java.lang.String@2726}"aa" - invocation object
         * [1] = {java.lang.String@2727}""   - invocation method
         */
            case 2 : def typeAvailable = args[ 1 ] instanceof Class
                     o                 = typeAvailable ? delegate  : args[ 0 ]
                     methodName        = typeAvailable ? args[ 0 ] : args[ 1 ]
                     type              = typeAvailable ? args[ 1 ] : null
                     break
        /**
         * [0] = {java.lang.String@2549}"1\n2"              - invocation object
         * [1] = {java.lang.String@1936}"eachLine"          - invocation method
         * [2] = {java.lang.Class@1421}"class java.io.File" - type
         */
            case 3 : o          = args[ 0 ]
                     methodName = args[ 1 ]
                     type       = args[ 2 ]
                     break
            default : throw new RuntimeException( "splitWith() args is of size [${args.size()}]: [$args]" )
        }

        methodName = ( methodName ?: '' ).trim()
        assert     methodName, "Method name is not provided"
        MetaMethod m = o.metaClass.pickMethod( methodName, Closure )
        assert     m, "No method [$methodName] accepting Closure argument is found for class [${ o.class.name }]"

        def result = []
        m.doMethodInvoke( o, { result << it } )

        if ( type )
        {
            result.each{ assert type.isInstance( it ), \
                         "Object [$it][${ it.class.name }] returned by method [$methodName] is not an instance of type [$type.name]" }
        }

        result
    }


    /**
     * Enhanced recursive files iteration.
     *
     * @param delegate original delegate object
     * @param configs  configurations Map
     * @param callback invocation callback
     */
    void recurse( File delegate, Map configs = [:], Closure callback )
    {
        assert delegate.isDirectory(), "[$delegate.canonicalPath] is not a directory"
        assert configs,  "recurse(): Configs Map is not provided"
        assert callback, "recurse(): Callback is not provided"

        Closure  filter       = ( Closure ) configs[ 'filter' ] // Allowed to be null
        FileType fileType     = general.choose(( FileType ) configs[ 'type'        ],  FileType.FILES )
        FileType filterType   = general.choose(( FileType ) configs[ 'filterType'  ],  fileType       )
        boolean  stopOnFalse  = general.choose(( boolean )  configs[ 'stopOnFalse' ],  false          )
        boolean  stopOnFilter = general.choose(( boolean )  configs[ 'stopOnFilter' ], false          )

        handleDirectory(( File ) delegate, callback, filter, fileType, filterType, stopOnFalse, stopOnFilter )
    }


    /**
     * Calculates directory size.
     *
     * @param delegate original delegate object, must be a directory
     * @return directory size as a sum of all files it contains recursively
     */
    long directorySize( File delegate )
    {
        long size = 0
        delegate.recurse([ type : FileType.FILES, detectLoops : true ]){ size += it.size() }
        size
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
    private void handleDirectory( File             directory,
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
    private InvocationResult invokeCallback ( File             callbackFile,
                                              Closure<?>       callback,
                                              Closure<Boolean> filter,
                                              FileType         fileType,
                                              FileType         filterType )
    {
        verify.exists( callbackFile )
        verify.notNull( callback, fileType, filterType )

        def fileTypeMatch       = file.typeMatch( fileType,   callbackFile )
        def filterTypeMatch     = file.typeMatch( filterType, callbackFile )
        def result              = new InvocationResult()
        result.filterPass       = (( filter == null ) || ( ! filterTypeMatch ) || filter( callbackFile ))
        result.invocationResult = true

        if ( fileTypeMatch && result.filterPass )
        {
            Object callbackResult   = callback( callbackFile )
            result.invocationResult = general.choose( callbackResult as boolean, true )
        }

        result
    }
}
