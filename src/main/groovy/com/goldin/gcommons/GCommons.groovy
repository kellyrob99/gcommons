package com.goldin.gcommons

import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import com.goldin.gcommons.beans.*
import static com.goldin.gcommons.Constants.*


 /**
 * "GCommons" entry points
 */
class GCommons
{
    private static ApplicationContext CONTEXT = newContext()
    private static ApplicationContext newContext(){ new ClassPathXmlApplicationContext( CONTEXT_PATH ) }

    /**
     * Mapping of all beans: bean class => bean instance
     */
    private static final Map<Class<? extends BaseBean>, ? extends BaseBean> BEANS = [:]
    private static <T extends BaseBean> T getBean( Class<T> beanClass, boolean refresh )
    {
        assert BaseBean.class.isAssignableFrom( beanClass )

        BEANS[ beanClass ] = (( refresh ) || ( ! BEANS.containsKey( beanClass ))) ? CONTEXT.getBean( beanClass ) :
                                                                                    BEANS[ beanClass ]

        assert ( beanClass.isInstance( BEANS[ beanClass ] )) &&
               ( BEANS[ beanClass ] instanceof BaseBean )
        BEANS[ beanClass ]
    }


    static ApplicationContext context( boolean refresh = false ) { CONTEXT = ( refresh ? newContext() : CONTEXT ) }
    static VerifyBean         verify ( boolean refresh = false ) { getBean( VerifyBean.class,  refresh ) }
    static GeneralBean        general( boolean refresh = false ) { getBean( GeneralBean.class, refresh ) }
    static FileBean           file   ( boolean refresh = false ) { getBean( FileBean.class,    refresh ) }
    static IOBean             io     ( boolean refresh = false ) { getBean( IOBean.class,      refresh ) }
    static NetBean            net    ( boolean refresh = false ) { getBean( NetBean.class,     refresh ) }
}
