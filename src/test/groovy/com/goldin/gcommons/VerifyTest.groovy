package com.goldin.gcommons

import org.junit.Test

/**
 * {@link Verify} tests
 *
 */
class VerifyTest extends BaseTest
{
    @Test
    void shouldVerifyExists()
    {
        verify.exists( new File( System.getProperty( "user.dir" )))
        verify.exists( new File( System.getProperty( "user.home" )))

        def f = tempFile()
        verify.exists( f )

        f.delete()
        assert ! f.exists()

        shouldFailWith( AssertionError.class ) { verify.exists( f ) }
        shouldFailWith( AssertionError.class ) { verify.exists( new File( "Doesn't exist" )) }
        shouldFailWith( NullPointerException.class ) { verify.exists( new File( System.getProperty( "aaa" ))) }
    }


    @Test
    void shouldVerifyFile()
    {
        def f = tempFile()
        verify.file( f )
        shouldFailWith( AssertionError.class ) { verify.file( f.getParentFile() ) }
        shouldFailWith( AssertionError.class ) { verify.file( f.getParentFile().getParentFile()) }

        f.delete()
        assert ! f.exists()

        shouldFailWith( AssertionError.class ) { verify.file( f ) }
    }


    @Test
    void shouldVerifyDirectory()
    {
        verify.directory( new File( System.getProperty( "user.dir" )))
        verify.directory( new File( System.getProperty( "user.home" )))

        def f = tempFile()
        verify.file( f )
        verify.directory( f.getParentFile())
        verify.directory( f.getParentFile().getParentFile())

        f.delete()
        assert ! f.exists()

        shouldFailWith( AssertionError.class ) { verify.file( f ) }
        
        verify.directory( f.getParentFile())
        verify.directory( f.getParentFile().getParentFile())
    }


    @Test
    void shouldVerifyEqualFiles()
    {
        def f1   = tempFile()
        def f2   = tempFile()
        def data = System.currentTimeMillis() as String

        f1.write( data * 10 )
        f2.write( data * 10 )

        verify.equal( f1, f2 )
    }
}
