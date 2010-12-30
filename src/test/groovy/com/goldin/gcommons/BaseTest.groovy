package com.goldin.gcommons

import com.goldin.gcommons.beans.FileBean
import com.goldin.gcommons.beans.GeneralBean
import com.goldin.gcommons.beans.VerifyBean
import java.nio.BufferOverflowException
import org.junit.Test

 /**
 * Base class for the tests
 */
class BaseTest
{
    final VerifyBean  verifyBean  = GCommons.verify()
    final GeneralBean generalBean = GCommons.general()
    final FileBean    fileBean    = GCommons.file()
    

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
     * {@link GroovyTestCase#shouldFailWithCause(Class, Closure)} wrapper
     */
    String shouldFailWith( Class cl, Closure c ) { new MyGroovyTestCase().shouldFail( cl, c ) }
    String shouldFailAssert ( Closure c )        { new MyGroovyTestCase().shouldFail( AssertionError.class, c ) }


    /**
     * Retrieves test dir to be used for temporal output 
     * @param dirName test directory name
     * @return test directory to use
     */
    File testDir( String dirName = System.currentTimeMillis() as String )
    {
        def caller = ( StackTraceElement ) new Throwable().stackTrace.findAll { it.className.startsWith( 'com.goldin' ) }[ 2 ]
        fileBean.delete( new File( "build/tests/${ this.class.name }/${ caller.methodName }/$dirName" ))
    }


    /**
     * Verifies {@link #shouldFailWith} behavior 
     */
    @Test
    void testShouldFail()
    {
        shouldFailWith( RuntimeException.class )    { throw new RuntimeException()        }
        shouldFailWith( RuntimeException.class )    { throw new BufferOverflowException() }
        shouldFailWith( NullPointerException.class ){ throw new NullPointerException()    }
        shouldFailWith( IOException.class )         { throw new FileNotFoundException()   }
        shouldFailWith( IOException.class )         { throw new IOException( new RuntimeException())}

        shouldFailAssert {
            shouldFailWith( NullPointerException.class ) { throw new RuntimeException() }
        }
        
        shouldFailAssert { throw new AssertionError() }
        shouldFailAssert { shouldFailAssert { throw new IOException() }}
        shouldFailAssert { assert 3 == 5 }
        shouldFailAssert { assert false  }
        shouldFailAssert { assert null   }
        shouldFailAssert { assert ''     }

        try
        {
            shouldFailWith( IOException.class ) { throw new Throwable() }
            assert false // Shouldn't get here
        }
        catch ( AssertionError expected ){ /* Good */ }

        try
        {
            shouldFailAssert { shouldFailAssert { shouldFailAssert { throw new IOException() }}}
            assert false // Shouldn't get here
        }
        catch ( AssertionError expected ){ /* Good */ }
    }
}
