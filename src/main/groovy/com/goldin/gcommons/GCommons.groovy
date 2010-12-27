package com.goldin.gcommons

import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * com.goldin.gcommons.GCommons entry point
 */
class GCommons
{
    private static final ApplicationContext CONTEXT = new ClassPathXmlApplicationContext( "/application-context.xml" )


    static ApplicationContext context()  { CONTEXT }
    static General            general () { context().getBean( General ) }
}
