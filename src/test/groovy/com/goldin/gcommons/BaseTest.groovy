package com.goldin.gcommons

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import com.goldin.gcommons.beans.*

/**
 * Base class for the tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( '/gcommons-application-context.xml' )
class BaseTest
{
    /**
     * Injected by Spring
     */
    @Autowired final ConstantsBean  constantsBean
    @Autowired final VerifyBean     verifyBean
    @Autowired final GeneralBean    generalBean
    @Autowired final FileBean       fileBean
    @Autowired final NetBean        netBean


    /**
     * Providing a public access to {@link GroovyTestCase#shouldFail(Class, Closure)}
     */
    static class MyGroovyTestCase extends GroovyTestCase
    {
        @Override
        public String shouldFail ( Class c, Closure code ) { super.shouldFail( c, code ) }

        @Test
        void testNothing(){} // Fails otherwise: "No tests found in com.goldin.gcommons.BaseTest$MyGroovyTestCase"
    }


    /**
     * {@link GroovyTestCase} wrappers
     */
    String shouldFailWith     ( Class cl, Closure c ) { new MyGroovyTestCase().shouldFail( cl, c ) }
    String shouldFailWithCause( Class cl, Closure c ) { new MyGroovyTestCase().shouldFailWithCause( cl, c ) }
    String shouldFailAssert   ( Closure c )           { new MyGroovyTestCase().shouldFail( AssertionError.class, c ) }


    /**
     * Retrieves test dir to be used for temporal output
     * @param dirName test directory name
     * @return test directory to use
     */
    File testDir( String dirName = System.currentTimeMillis() as String )
    {
        def caller = ( StackTraceElement ) new Throwable().stackTrace.findAll { it.className.startsWith( 'com.goldin' ) }[ 2 ]
        fileBean.delete( new File( "build/test/${ this.class.name }/${ caller.methodName }/$dirName" ))
    }


    @Test
    void testEmpty() { /* To prevent "java.lang.Exception: No runnable methods" */ }
}
