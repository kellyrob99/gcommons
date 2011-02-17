package com.goldin.gcommons

import com.goldin.gcommons.util.RecurseHelper
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
     * MOP updates
     */
    static {
        /**
         * Splits an object to a list using its "iterating" each-like method
         * http://evgeny-goldin.com/blog/2010/09/01/groovy-splitwith/
         */
         Object.metaClass.splitWith = { Object[] args ->

             assert args.size().any{( it >= 1 ) && ( it <= 3 )}, "splitWith() args is of size [${args.size()}]: [$args]"
             Object o          = ( args.size() == 1 ) ? delegate  : args[ 0 ]
             String methodName = ( args.size() == 1 ) ? args[ 0 ] :
                                 ( args.size() == 2 ) ? args[ 1 ] :
                                 ( args.size() == 3 ) ? args[ 1 ] : null
             Class type        = ( args.size() == 3 ) ? args[ 2 ] : null

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
         * Improved version of resursive directory iteration
         * http://evgeny-goldin.org/youtrack/issue/gc-6
         */
        File.metaClass.recurse = { Map     configs = [:],
                                   Closure callback ->

            assert delegate.isDirectory(), "[$delegate.canonicalPath] is not a directory"
            assert configs,  "recurse(): Configs Map is not provided"
            assert callback, "recurse(): Callback is not provided"

            Closure  filter       = ( Closure ) configs[ 'filter' ] // Allowed to be null
            FileType fileType     = general().choose(( FileType ) configs[ 'type'        ],  FileType.FILES )
            FileType filterType   = general().choose(( FileType ) configs[ 'filterType'  ],  fileType       )
            boolean  stopOnFalse  = general().choose(( boolean )  configs[ 'stopOnFalse' ],  false          )
            boolean  stopOnFilter = general().choose(( boolean )  configs[ 'stopOnFilter' ], false          )

            recurseHelper().handleDirectory(( File ) delegate, callback, filter, fileType, filterType, stopOnFalse, stopOnFilter )
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


    static ApplicationContext context       ( boolean refresh = false ) { CONTEXT = ( refresh ? newContext() : CONTEXT ) }
    static RecurseHelper      recurseHelper ( boolean refresh = false ) { getBean( RecurseHelper, refresh ) }
    static ConstantsBean      constants     ( boolean refresh = false ) { getBean( ConstantsBean, refresh ) }
    static VerifyBean         verify        ( boolean refresh = false ) { getBean( VerifyBean,    refresh ) }
    static GeneralBean        general       ( boolean refresh = false ) { getBean( GeneralBean,   refresh ) }
    static FileBean           file          ( boolean refresh = false ) { getBean( FileBean,      refresh ) }
    static IOBean             io            ( boolean refresh = false ) { getBean( IOBean,        refresh ) }
    static NetBean            net           ( boolean refresh = false ) { getBean( NetBean,       refresh ) }
    static GroovyBean         groovy        ( boolean refresh = false ) { getBean( GroovyBean,    refresh ) }
}
