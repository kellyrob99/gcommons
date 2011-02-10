package com.goldin.gcommons

import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.goldin.gcommons.beans.*

 /**
 * Base class for the tests
 */
class BaseTest
{
    /**
     * Initializing all beans
     */
    final ConstantsBean  constantsBean = GCommons.constants()
    final VerifyBean     verifyBean    = GCommons.verify()
    final GeneralBean    generalBean   = GCommons.general()
    final FileBean       fileBean      = GCommons.file()
    final NetBean        netBean       = GCommons.net()
    final GroovyBean     groovyBean    = GCommons.groovy()


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
    String shouldFailAssert   ( Closure c )           { new MyGroovyTestCase().shouldFail( AssertionError, c ) }


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

    /**
     * Map of loggers for each bean
     */
    private static final Map<Class<? extends BaseTest>, Logger> LOGGERS = [:]

    /**
     * Retrieves logger for the bean class specified.
     * @param o bean class
     * @return logger to use
     */
    static Logger getLog( BaseTest test)
    {
        LOGGERS[ test.class ] = LOGGERS[ test.class ] ?: LoggerFactory.getLogger( test.class )
    }


    @Test
    void testEmpty() { /* To prevent "java.lang.Exception: No runnable methods" */ }
}
