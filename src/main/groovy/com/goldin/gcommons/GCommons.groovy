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
         * Calculates directory size
         */
        File.metaClass.directorySize = {->

            File   directory = delegate
            assert directory.isDirectory(), "[$directory.canonicalPath] is not a directory"

            long size = 0
            directory.eachFileRecurse( FileType.FILES ){ size += it.size() }
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


    static ApplicationContext context   ( boolean refresh = false ) { CONTEXT = ( refresh ? newContext() : CONTEXT ) }
    static ConstantsBean      constants ( boolean refresh = false ) { getBean( ConstantsBean, refresh ) }
    static VerifyBean         verify    ( boolean refresh = false ) { getBean( VerifyBean,    refresh ) }
    static GeneralBean        general   ( boolean refresh = false ) { getBean( GeneralBean,   refresh ) }
    static FileBean           file      ( boolean refresh = false ) { getBean( FileBean,      refresh ) }
    static IOBean             io        ( boolean refresh = false ) { getBean( IOBean,        refresh ) }
    static NetBean            net       ( boolean refresh = false ) { getBean( NetBean,       refresh ) }
    static GroovyBean         groovy    ( boolean refresh = false ) { getBean( GroovyBean,    refresh ) }
}
