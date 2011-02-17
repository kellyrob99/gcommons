package com.goldin.gcommons

import com.goldin.gcommons.util.MopHelper
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import com.goldin.gcommons.beans.*

 /**
 * "GCommons" entry points
 */
class GCommons
{
    private static ApplicationContext CONTEXT
    private static ApplicationContext newContext(){ new ClassPathXmlApplicationContext( '/gcommons-application-context.xml' )}

    /**
     * Mapping of all beans: bean class => bean instance
     */
    private static final Map<Class<? extends BaseBean>, ? extends BaseBean> BEANS = [:]


    /**
     * MOP updates
     */
    static {

        MopHelper helper = ( MopHelper ) getBean( MopHelper, false )

        /**
         * Splits an object to a list using its "iterating" each-like method
         * http://evgeny-goldin.com/blog/2010/09/01/groovy-splitwith/
         */
        Object.metaClass.splitWith = { Object[] args -> helper.splitWith( delegate, args ) }

        /**
         * Improved version of resursive directory iteration
         * http://evgeny-goldin.org/youtrack/issue/gc-6
         */
        File.metaClass.recurse = { Map configs = [:], Closure callback -> helper.recurse(( File ) delegate, configs, callback ) }

        /**
         * Calculates directory size
         */
        File.metaClass.directorySize = { helper.directorySize(( File ) delegate ) }
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
        synchronized ( GCommons.class ) { CONTEXT = ( CONTEXT ?: newContext()) }

        assert context()
        assert BaseBean.class.isAssignableFrom( beanClass )

        if ( refresh || ( ! BEANS.containsKey( beanClass )))
        {
            T bean = context().getBean( beanClass )
            assert (( bean instanceof BaseBean ) && ( beanClass.isInstance( bean )))
            BEANS[ beanClass ] = bean
        }

        BEANS[ beanClass ]
    }


    static ApplicationContext context       ( boolean refresh = false ) { CONTEXT = ( refresh ? newContext() : CONTEXT ) }
    static ConstantsBean      constants     ( boolean refresh = false ) { getBean( ConstantsBean, refresh ) }
    static VerifyBean         verify        ( boolean refresh = false ) { getBean( VerifyBean,    refresh ) }
    static GeneralBean        general       ( boolean refresh = false ) { getBean( GeneralBean,   refresh ) }
    static FileBean           file          ( boolean refresh = false ) { getBean( FileBean,      refresh ) }
    static IOBean             io            ( boolean refresh = false ) { getBean( IOBean,        refresh ) }
    static NetBean            net           ( boolean refresh = false ) { getBean( NetBean,       refresh ) }
    static GroovyBean         groovy        ( boolean refresh = false ) { getBean( GroovyBean,    refresh ) }
}
