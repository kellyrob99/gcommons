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
        verify.exists( Constants.USER_DIR  )
        verify.exists( Constants.USER_HOME )

        def f = general.tempFile()
        verify.exists( f )
        general.delete( f )

        shouldFailAssert { verify.exists( f ) }
        shouldFailAssert { verify.exists( new File( "Doesn't exist" )) }
        shouldFailWith( NullPointerException.class ) { verify.exists( new File( System.getProperty( "aaa" ))) }
    }


    @Test
    void shouldVerifyFile()
    {
        def f = general.tempFile()
        verify.file( f )
        shouldFailAssert { verify.file( f.getParentFile() ) }
        shouldFailAssert { verify.file( f.getParentFile().getParentFile()) }

        general.delete( f )

        shouldFailAssert { verify.file( f ) }
    }


    @Test
    void shouldVerifyDirectory()
    {
        verify.directory( Constants.USER_DIR  )
        verify.directory( Constants.USER_HOME )

        def f = general.tempFile()
        verify.file( f )
        verify.directory( f.getParentFile())
        verify.directory( f.getParentFile().getParentFile())

        general.delete( f )

        shouldFailAssert { verify.file( f ) }
        
        verify.directory( f.getParentFile())
        verify.directory( f.getParentFile().getParentFile())
    }
}
