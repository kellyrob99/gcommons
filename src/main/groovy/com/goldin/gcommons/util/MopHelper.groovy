package com.goldin.gcommons.util

import ch.qos.logback.classic.gaffer.ConfigurationDelegate
import ch.qos.logback.core.util.ContextUtil
import com.goldin.gcommons.beans.BaseBean
import groovy.io.FileType

/**
 * MOP updates implementations.
 */
class MopHelper extends BaseBean
{

    MopHelper ()
    {
        /**
         * Patching logback - specifying CL when initializing a GroovyShell
         */

        ch.qos.logback.classic.gaffer.GafferConfigurator.metaClass.run = {
            String dslText->
            Binding binding = new Binding();
            binding.setProperty("hostname", ContextUtil.getLocalHostName());
            Script dslScript = new GroovyShell( MopHelper.class.classLoader, binding ).parse( dslText ) // <==== Patch
            dslScript.metaClass.mixin(ConfigurationDelegate)
            dslScript.setContext(context)
            dslScript.metaClass.getDeclaredOrigin = { dslScript }
            dslScript.run()
        }
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
     * Recursive iteration configuration parameters.
     */
    static class RecurseConfig
    {
        Closure  filter
        FileType fileType
        FileType filterType
        boolean  stopOnFalse
        boolean  stopOnFilter
        boolean  detectLoops
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

        def config          = new RecurseConfig()
        config.filter       = ( Closure ) configs[ 'filter' ] // Allowed to be null
        config.fileType     = general.choose(( FileType ) configs[ 'type'         ], FileType.FILES  )
        config.filterType   = general.choose(( FileType ) configs[ 'filterType'   ], config.fileType )
        config.stopOnFalse  = general.choose(( boolean )  configs[ 'stopOnFalse'  ], false           )
        config.stopOnFilter = general.choose(( boolean )  configs[ 'stopOnFilter' ], false           )
        config.detectLoops  = general.choose(( boolean )  configs[ 'detectLoops'  ], false           )

        handleDirectory(( File ) delegate, callback, config, ( config.detectLoops ? [] as Set : null ))
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
    private void handleDirectory( File          directory,
                                  Closure<?>    callback,
                                  RecurseConfig config,
                                  Set<String>   directories )
    {
        verify.directory( directory )
        verify.notNull( callback, config )

        if ( config.detectLoops && ( ! directories.add( directory.canonicalPath )))
        {
            getLog( this ).info( "Loop detected - [$directory.canonicalPath] was already visited" )
            return
        }

        for ( File f in directory.listFiles())
        {
            def result          = invokeCallback( f, callback, config )
            def recursiveInvoke = ( f.isDirectory() &&
                                    (( ! config.stopOnFilter ) || ( config.filterType != FileType.DIRECTORIES ) || ( result.filterPass )) &&
                                    (( ! config.stopOnFalse  ) || ( result.invocationResult )))
            if ( recursiveInvoke )
            {
                handleDirectory( f, callback, config, directories )
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
    private InvocationResult invokeCallback ( File          callbackFile,
                                              Closure<?>    callback,
                                              RecurseConfig config )
    {
        verify.exists( callbackFile )
        verify.notNull( callback, config )

        def fileTypeMatch       = file.typeMatch( config.fileType,   callbackFile )
        def filterTypeMatch     = file.typeMatch( config.filterType, callbackFile )
        def result              = new InvocationResult()
        result.filterPass       = (( config.filter == null ) || ( ! filterTypeMatch ) || config.filter( callbackFile ))
        result.invocationResult = true

        if ( fileTypeMatch && result.filterPass )
        {
            Object callbackResult   = callback( callbackFile )
            result.invocationResult = general.choose( callbackResult as boolean, true )
        }

        result
    }
}
