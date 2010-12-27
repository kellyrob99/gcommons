package com.goldin.gcommons

import java.nio.BufferOverflowException
import junit.framework.AssertionFailedError
import org.junit.Test

/**
 * Base class for the tests
 */
class BaseTest
{
    final General general = GCommons.general()
    final Verify  verify  = GCommons.verify()


    /**
     * Extends {@link GroovyTestCase} to provide public access to
     * {@link GroovyTestCase#shouldFailWithCause(Class, Closure) }
     */
    static class MyGroovyTestCase extends GroovyTestCase
    {
        String shouldFailWith ( Class c, Closure code ) { super.shouldFail( c, code )}

        @Test
        void testNothing(){}
    }

    
    /**
     * {@link GroovyTestCase#shouldFailWithCause(Class, Closure)} wrapper
     */
    Closure shouldFailWith = new MyGroovyTestCase().&shouldFailWith

    
    /**
     * {@link File#createTempFile(String, String)} wrapper
     * @return temp file created
     */
    File tempFile() { File.createTempFile( BaseTest.class.name, '' ) }


    /**
     * Verifies {@link #shouldFailWith} behavior 
     */
    @Test
    void shouldFail()
    {
        shouldFailWith( RuntimeException.class )    { throw new RuntimeException()        }
        shouldFailWith( RuntimeException.class )    { throw new BufferOverflowException() }
        shouldFailWith( NullPointerException.class ){ throw new NullPointerException()    }
        shouldFailWith( IOException.class )         { throw new FileNotFoundException()   }
        shouldFailWith( IOException.class )         { throw new IOException( new RuntimeException())}

        shouldFailWith( AssertionFailedError.class )
        {
            shouldFailWith( NullPointerException.class ) { throw new RuntimeException() }
        }

        try
        {
            shouldFailWith( IOException.class ) { throw new Throwable() }
            assert false // Shouldn't get here
        }
        catch ( AssertionFailedError ignored ){}
    }
}
