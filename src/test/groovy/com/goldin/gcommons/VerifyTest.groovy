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


    @Test
    void shouldVerifyEqualFiles()
    {
        def f1   = general.tempFile()
        def f2   = general.tempFile()
        def data = System.currentTimeMillis() as String

        f1.write( data * 10 )
        f2.write( data * 10 )

        verify.equal( f1, f2 )

        f1.write( data * 10 )
        f2.write( data * 11 )

        shouldFailAssert{ verify.equal( f1, f2 ) }

        general.delete( f1, f2 )
    }


    @Test
    void shouldVerifyEqualDirectories()
    {
        verify.equal( new File( Constants.USER_DIR, 'build' ),
                      new File( Constants.USER_DIR, 'build' ),
                      false )
        verify.equal( new File( Constants.USER_DIR, 'src' ),
                      new File( Constants.USER_DIR, 'src' ),
                      true )
        verify.equal( Constants.USER_DIR,  Constants.USER_DIR, false, '*.class'  )
        verify.equal( Constants.USER_DIR,  Constants.USER_DIR, true,  '*.groovy' )
        verify.equal( Constants.USER_DIR,  Constants.USER_DIR, true,  '*.xml'    )

        shouldFailAssert {
            shouldFailAssert { verify.equal( Constants.USER_DIR,  Constants.USER_DIR, false )}
        }

        shouldFailAssert { verify.equal( Constants.USER_DIR,  Constants.USER_HOME, false )}

        def d1   = general.tempDirectory()
        def d2   = general.tempDirectory()
        def f1   = new File( d1, 'a' )
        def f2   = new File( d2, 'a' )
        def data = System.currentTimeMillis() as String

        verify.equal( d1, d2 )

        f1.write( data * 3 )
        f2.write( data )
        f2.append( data )
        f2.append( data )

        verify.equal( d1, d2 )

        f1.append( 'aa' )
        f2.append( 'aa' )
        
        verify.equal( d1, d2 )

        f1.append( 'bc' )
        f2.append( 'cb' )

        shouldFailAssert { verify.equal( d1, d2 ) }

        general.delete( d1, d2 )
        
        shouldFailAssert { verify.equal( d1, d2 ) }
    }
}
