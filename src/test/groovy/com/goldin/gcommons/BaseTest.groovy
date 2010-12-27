package com.goldin.gcommons

import java.nio.BufferOverflowException
import org.junit.Test

/**
 * Base class for the tests
 */
class BaseTest
{
    final General general = GCommons.general()
    final Verify  verify  = GCommons.verify()

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
