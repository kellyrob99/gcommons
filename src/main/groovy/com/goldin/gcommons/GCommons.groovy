package com.goldin.gcommons

import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import static com.goldin.gcommons.Constants.*
import com.goldin.gcommons.beans.*


/**
 * "GCommons" entry points
 */
class GCommons
{
    private static ApplicationContext newContext(){ new ClassPathXmlApplicationContext( CONTEXT_PATH ) }
    private static VerifyBean         getVerifyBean  (){ CONTEXT.getBean( VerifyBean.class  ) }
    private static GeneralBean        getGeneralBean (){ CONTEXT.getBean( GeneralBean.class ) }
    private static FileBean           getFileBean    (){ CONTEXT.getBean( FileBean.class    ) }
    private static IOBean             getIoBean      (){ CONTEXT.getBean( IOBean.class      ) }
    private static NetBean            getNetBean     (){ CONTEXT.getBean( NetBean.class     ) }

    private static ApplicationContext CONTEXT = newContext()
    private static VerifyBean         VERIFY  = getVerifyBean()
    private static GeneralBean        GENERAL = getGeneralBean()
    private static FileBean           FILE    = getFileBean()
    private static IOBean             IO      = getIoBean()
    private static NetBean            NET     = getNetBean()

    static ApplicationContext context( boolean refresh = false ) { CONTEXT = ( refresh ? newContext()     : CONTEXT ) }
    static VerifyBean         verify ( boolean refresh = false ) { VERIFY  = ( refresh ? getVerifyBean()  : VERIFY  ) }
    static GeneralBean        general( boolean refresh = false ) { GENERAL = ( refresh ? getGeneralBean() : GENERAL ) }
    static FileBean           file   ( boolean refresh = false ) { FILE    = ( refresh ? getFileBean()    : FILE    ) }
    static IOBean             io     ( boolean refresh = false ) { IO      = ( refresh ? getIoBean()      : IO      ) }
    static NetBean            net    ( boolean refresh = false ) { NET     = ( refresh ? getNetBean()     : NET     ) }
}
