package com.goldin.gcommons

import com.goldin.gcommons.beans.FileBean
import com.goldin.gcommons.beans.GeneralBean
import com.goldin.gcommons.beans.VerifyBean
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

 /**
 * "GCommons" entry points
 */
class GCommons
{
    static ApplicationContext newContext(){ new ClassPathXmlApplicationContext( Constants.CONTEXT_PATH ) }
    static VerifyBean         getVerify (){ CONTEXT.getBean( VerifyBean.class  ) }
    static GeneralBean        getGeneral(){ CONTEXT.getBean( GeneralBean.class ) }
    static FileBean           getFile   (){ CONTEXT.getBean( FileBean.class    ) }

    private static ApplicationContext CONTEXT = newContext()
    private static VerifyBean         VERIFY  = getVerify()
    private static GeneralBean        GENERAL = getGeneral()
    private static FileBean           FILE    = getFile()

    static ApplicationContext context( boolean refresh = false ) { CONTEXT = ( refresh ? newContext() : CONTEXT ) }
    static VerifyBean         verify ( boolean refresh = false ) { VERIFY  = ( refresh ? getVerify()  : VERIFY  ) }
    static GeneralBean        general( boolean refresh = false ) { GENERAL = ( refresh ? getGeneral() : GENERAL ) }
    static FileBean           file   ( boolean refresh = false ) { FILE    = ( refresh ? getFile()    : FILE    ) }
}
