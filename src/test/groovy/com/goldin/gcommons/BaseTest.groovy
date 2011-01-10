package com.goldin.gcommons

import com.goldin.gcommons.beans.FileBean
import com.goldin.gcommons.beans.GeneralBean
import com.goldin.gcommons.beans.NetBean
import com.goldin.gcommons.beans.VerifyBean
import org.junit.Test

 /**
 * Base class for the tests
 */
class BaseTest
{
    final VerifyBean  verifyBean  = GCommons.verify()
    final GeneralBean generalBean = GCommons.general()
    final FileBean    fileBean    = GCommons.file()
    final NetBean     netBean     = GCommons.net()


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
}
