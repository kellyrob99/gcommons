package com.goldin.gcommons

import com.goldin.gcommons.util.MopHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import com.goldin.gcommons.beans.*


/**
 * "GCommons" entry points
 */
class GCommons
{
    private static ConfigurableApplicationContext CONTEXT
    private static ConfigurableApplicationContext newContext(){ new ClassPathXmlApplicationContext( '/gcommons-application-context.xml' )}
    private static Logger LOG

    /**
     * Mapping of all beans: bean class => bean instance
     */
    private static final Map<Class<? extends BaseBean>, ? extends BaseBean> BEANS = [:]


    /**
     * Retrieves bean instance for the class specified.
     *
     * @param beanClass bean class, extends {@link BaseBean}
     * @param refresh whether a new instance should be retrieved from Spring context
     * @return bean instance for the class specified
     */
    private static <T extends BaseBean> T getBean( Class<T> beanClass, boolean refresh )
    {
        /**
         * When run under @Grab, static { .. } context has libraries missing in class loading hierarchy
         * and can not be used to trigger context initialization. Thus it only happens when the first actual
         * call is made and therefore protected by synchronized block.
         */

        synchronized ( GCommons.class )
        {
            if ( ! CONTEXT )
            {
                long t  = System.currentTimeMillis()
                CONTEXT = newContext()
                LOG     = LoggerFactory.getLogger( GCommons.class )
                MopHelper helper = ( MopHelper ) getBean( MopHelper, false )
                Object.metaClass.splitWith   = { Object[] args                       -> helper.splitWith( delegate, args ) }
                File.metaClass.recurse       = { Map configs = [:], Closure callback -> helper.recurse(( File ) delegate, configs, callback ) }
                File.metaClass.directorySize = { helper.directorySize(( File ) delegate ) }

                LOG.info( "GCommons context initialized: [$CONTEXT.beanDefinitionCount] beans - $CONTEXT.beanDefinitionNames ([${ System.currentTimeMillis() - t }] ms)" )
            }
        }

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


    static ConfigurableApplicationContext context   ( boolean refresh = false ) { CONTEXT = ( refresh ? newContext() : CONTEXT ) }
    static ConstantsBean                  constants ( boolean refresh = false ) { getBean( ConstantsBean, refresh ) }
    static VerifyBean                     verify    ( boolean refresh = false ) { getBean( VerifyBean,    refresh ) }
    static GeneralBean                    general   ( boolean refresh = false ) { getBean( GeneralBean,   refresh ) }
    static FileBean                       file      ( boolean refresh = false ) { getBean( FileBean,      refresh ) }
    static IOBean                         io        ( boolean refresh = false ) { getBean( IOBean,        refresh ) }
    static NetBean                        net       ( boolean refresh = false ) { getBean( NetBean,       refresh ) }
    static GroovyBean                     groovy    ( boolean refresh = false ) { getBean( GroovyBean,    refresh ) }
}
