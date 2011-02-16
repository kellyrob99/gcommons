package com.goldin.gcommons

import groovy.io.FileType
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import com.goldin.gcommons.beans.*

 /**
 * "GCommons" entry points
 */
class GCommons
{
    private static ApplicationContext CONTEXT = newContext()
    private static ApplicationContext newContext(){ new ClassPathXmlApplicationContext( '/gcommons-application-context.xml' )}

    /**
     * Mapping of all beans: bean class => bean instance
     */
    private static final Map<Class<? extends BaseBean>, ? extends BaseBean> BEANS = [:]


    /**
     * Container that holds callback invocation results.
     * See {@link #invokeCallback}
     */
    static class InvocationResult
    {
        boolean filterPass
        boolean invocationResult
    }


    static {
        /**
         * Splits an object to a list using its "iterating" each-like mthod
         * http://evgeny-goldin.com/blog/2010/09/01/groovy-splitwith/
         */
         Object.metaClass.splitWith = { String methodName ->

             methodName = ( methodName ?: '' ).trim()
             assert     methodName, "Method name should be provided"
             MetaMethod m = delegate.metaClass.pickMethod( methodName, Closure )
             assert     m, "No method [$methodName] accepting Closure argument is found for class [${ delegate.class.name }]"

             def result = []
             m.doMethodInvoke( delegate, { result << it } )

             result
         }


        /**
         * Improved version of resursive directory iteration
         * http://evgeny-goldin.org/youtrack/issue/gc-6
         * ??????????????????????????????????????????????????????????????
         * Documentation link
         */
        File.metaClass.recurse = { Map     configs = [:],
                                   Closure callback ->

            assert delegate.isDirectory(), "[$delegate] is not a directory"
            assert callback, "recurse(): Callback is not provided"
            assert configs,  "recurse(): Configs Map is not provided"

            handleDirectory(( File ) delegate, configs, callback )
        }


        /**
         * Calculates directory size
         */
        File.metaClass.directorySize = {->
            long size = 0
            delegate.recurse([ type        : FileType.FILES,
                               detectLoops : true ]){ size += it.size() }
            size
        }
    }


    /**
     * "File.metaClass.recurse" helper - handles directory provided.
     *
     * @param file     directory to handle
     * @param configs  invocation configs with keys "filter", "type", "stopOnFalse" and "detectLoops"
     * @param callback callback to invoke
     *
     * @return <code>false</code> if recursive iteration should be stopped,
     *         <code>true</code>  otherwise
     */
    private static boolean handleDirectory( File       directory,
                                            Map        configs,
                                            Closure<?> callback )
    {
        verify().directory( directory )
        verify().notNull( configs, callback )

        def stopOnFalse  = general().choose( configs[ 'stopOnFalse'  ], false )
        def stopOnFilter = general().choose( configs[ 'stopOnFilter' ], false )

        for ( File f in directory.listFiles())
        {
            def result = invokeCallback( f, configs, callback )
            if ( stopOnFalse && ( ! result.invocationResult ))
            {
                // stopOnFalse + callback was invoked with negative result - iteration is stopped
                return false
            }

            if ( f.isDirectory())
            {
                // If we look at directory and it didn't pass the filter - no recursive invocation is made
                def recursiveInvoke = (( ! stopOnFilter ) || ( result.filterPass ))
                if ( recursiveInvoke && ( ! handleDirectory( f, configs, callback )))
                {
                    // Result of recursive invocation is false - iteration is stopped
                    return false
                }
            }
        }

        true
    }


    /**
     * "File.metaClass.recurse" helper - invokes callback provided.
     *
     * @param file     file or directory to handle
     * @param configs  invocation configs with keys "filter", "type", "stopOnFalse" and "detectLoops"
     * @param callback callback to invoke
     *
     * @return callback invocation result "as boolean" or
     *         <code>true</code> if callback provides no result or
     *                           was not invoked at all due to filters applied or
     *                           there's no need to read invocation result at all
     */
    private static InvocationResult invokeCallback ( File       file,
                                                     Map        configs,
                                                     Closure<?> callback )
    {
        verify().exists( file )
        verify().notNull( configs, callback )

        def filter          = configs[ 'filter' ]
        def fileType        = general().choose(( FileType ) configs[ 'type'       ], FileType.ANY )
        def filterType      = general().choose(( FileType ) configs[ 'filterType' ], fileType     )
        def fileTypeMatch   = GCommons.file().typeMatch( fileType,   file )
        def filterTypeMatch = GCommons.file().typeMatch( filterType, file )
        def result          = new InvocationResult()

        result.filterPass       = (( filter == null ) || ( ! filterTypeMatch ) || filter( file ))
        result.invocationResult = true

        if ( fileTypeMatch && result.filterPass )
        {
            Object callbackResult   = callback( file )
            result.invocationResult = general().choose( callbackResult as boolean, true )
        }

        result
    }


    /**
     * Retrieves bean instance for the class specified.
     *
     * @param beanClass bean class, extends {@link BaseBean}
     * @param refresh whether a new instance should be retrieved from Spring context
     * @return bean instance for the class specified
     */
    private static <T extends BaseBean> T getBean( Class<T> beanClass, boolean refresh )
    {
        assert BaseBean.class.isAssignableFrom( beanClass )

        BEANS[ beanClass ] = (( refresh ) || ( ! BEANS.containsKey( beanClass ))) ? context().getBean( beanClass ) :
                                                                                    BEANS[ beanClass ]

        assert ( beanClass.isInstance( BEANS[ beanClass ] )) && ( BEANS[ beanClass ] instanceof BaseBean )
        BEANS[ beanClass ]
    }


    static ApplicationContext context   ( boolean refresh = false ) { CONTEXT = ( refresh ? newContext() : CONTEXT ) }
    static ConstantsBean      constants ( boolean refresh = false ) { getBean( ConstantsBean, refresh ) }
    static VerifyBean         verify    ( boolean refresh = false ) { getBean( VerifyBean,    refresh ) }
    static GeneralBean        general   ( boolean refresh = false ) { getBean( GeneralBean,   refresh ) }
    static FileBean           file      ( boolean refresh = false ) { getBean( FileBean,      refresh ) }
    static IOBean             io        ( boolean refresh = false ) { getBean( IOBean,        refresh ) }
    static NetBean            net       ( boolean refresh = false ) { getBean( NetBean,       refresh ) }
    static GroovyBean         groovy    ( boolean refresh = false ) { getBean( GroovyBean,    refresh ) }
}
