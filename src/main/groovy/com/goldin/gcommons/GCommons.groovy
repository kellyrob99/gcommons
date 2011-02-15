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


    static {
        /**
         * Splits an object to a list using its "iterating" each-like mthod
         * http://evgeny-goldin.com/blog/2010/09/01/groovy-splitwith/
         */
         Object.metaClass.splitWith = { String methodName ->

             assert     methodName
             MetaMethod m = delegate.metaClass.pickMethod( methodName, Closure )
             assert     m

             def result = []
             m.doMethodInvoke( delegate, { result << it } )

             result
         }


        /**
         * Improved version of resursive directory iteration
         * http://evgeny-goldin.org/youtrack/issue/gc-6
         */
        File.metaClass.recurse = { FileType fileType,
                                   Closure  callback,
                                   Closure  filter      = { true },
                                   boolean  stopOnFalse = false ->

            assert delegate.isDirectory(), "[$delegate] is not a directory"
            assert fileType, "recurse(): FileType is not provided"
            assert callback, "recurse(): Callback is not provided"
            assert filter,   "recurse(): Filter callback is not provided"

            handleDirectory(( File ) delegate, fileType, callback, filter, stopOnFalse )
        }


        /**
         * Calculates directory size
         */
        File.metaClass.directorySize = {->
            long size = 0
            delegate.recurse( FileType.FILES ){ size += it.size() }
            size
        }
    }


    /**
     * "File.metaClass.recurse" helper - handles directory provided.
     *
     * @param file        directory to handle
     * @param fileType    file type filter
     * @param callback    callback to invoke
     * @param filter      filter callback
     * @param stopOnFalse whether false value value returned by callback stops recursive iteration
     * 
     * @return <code>false</code> if recursive iteration should be stopped,
     *         <code>true</code>  otherwise
     */
    private static boolean handleDirectory( File             directory,
                                            FileType         fileType,
                                            Closure<?>       callback,
                                            Closure<Boolean> filter,
                                            boolean          stopOnFalse )
    {
        verify().directory( directory )
        verify().notNull( fileType, callback, filter )

        for ( File f in directory.listFiles())
        {
            if (( ! invokeCallback( f, callback, filter, fileType )) && stopOnFalse )
            {   /**
                 * stopOnFalse + result of callback invocation is false - iteration is stopped
                 */
                return false
            }

            if ( f.isDirectory() && ( ! handleDirectory( f, fileType, callback, filter, stopOnFalse )))
            {   /**
                 * Result of recursive invocation is false - iteration is stopped
                 */
                return false
            }
        }

        true
    }


    /**
     * "File.metaClass.recurse" helper - invokes callback provided.
     *
     * @param file     file or directory to handle
     * @param callback callback to invoke
     * @param filter   filter to invoke
     * @param fileType file type filter
     * 
     * @return callback invocation result "as boolean" or
     *         <code>true</code> if callback provides no result or was not invoked at all due to filters applied
     */
    private static boolean invokeCallback ( File             file,
                                            Closure<?>       callback,
                                            Closure<Boolean> filter,
                                            FileType         fileType )
    {
        verify().exists( file )
        verify().notNull( callback, filter, fileType )

        def result    = true
        def typeMatch = ((  fileType == FileType.ANY         )  ||
                         (( fileType == FileType.DIRECTORIES ) && file.isDirectory()) ||
                         (( fileType == FileType.FILES       ) && file.isFile()))

        if ( typeMatch && filter( file ))
        {
            Object callbackResult = callback( file )
            if ( callbackResult != null )
            {
                result = callbackResult as boolean
            }
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
