package com.goldin.gcommons

import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

 /**
 * "GCommons" entry points
 */
class GCommons
{
    static ApplicationContext newContext(){ new ClassPathXmlApplicationContext( Constants.CONTEXT_PATH ) }
    static General            getGeneral(){ CONTEXT.getBean( General.class ) }
    static Verify             getVerify (){ CONTEXT.getBean( Verify.class  ) }

    private static ApplicationContext CONTEXT = newContext()
    private static General            GENERAL = getGeneral()
    private static Verify             VERIFY  = getVerify()

    static ApplicationContext context( boolean refresh = false ) { CONTEXT = ( refresh ? newContext() : CONTEXT ) }
    static General            general( boolean refresh = false ) { GENERAL = ( refresh ? getGeneral() : GENERAL ) }
    static Verify             verify ( boolean refresh = false ) { VERIFY  = ( refresh ? getVerify()  : VERIFY  ) }
}
