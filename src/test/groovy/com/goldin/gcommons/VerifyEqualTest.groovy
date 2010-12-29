package com.goldin.gcommons

import org.junit.Test

/**
 * {@link Verify#equal(File, File, boolean, String, String) } tests
 */
class VerifyEqualTest extends BaseTest
{
    @Test
    void shouldFailOnNullInput()
    {
        shouldFailAssert { verify.equal( new File( "doesn't exist" ), null ) }

        shouldFailWith( NullPointerException.class ) { verify.equal( null, null ) }
        shouldFailWith( NullPointerException.class ) { verify.equal( null, new File( "aaa" )) }
        shouldFailWith( NullPointerException.class ) { verify.equal( null, Constants.USER_DIR ) }
        shouldFailWith( NullPointerException.class ) { verify.equal( null, Constants.USER_HOME ) }
        shouldFailWith( NullPointerException.class ) { verify.equal( Constants.USER_DIR,  null ) }
        shouldFailWith( NullPointerException.class ) { verify.equal( Constants.USER_HOME, null ) }
    }


    @Test
    void shouldFailOnMissingFiles()
    {
        shouldFailAssert { verify.equal( new File( "doesn't exist" ),
                                         new File( "doesn't exist" ) ) }

        shouldFailAssert { verify.equal( new File( Constants.USER_DIR, '1.txt' ),
                                         new File( Constants.USER_DIR, '1.txt' )) }
        
        shouldFailAssert { verify.equal( new File( Constants.USER_DIR, '1.txt' ),
                                         Constants.USER_DIR ) }

        def file = new File( Constants.USER_HOME, 'a.txt' )

        shouldFailAssert { verify.equal( Constants.USER_HOME, file ) } // Directory + missing file

        file.write( 'anything' )

        shouldFailAssert { verify.equal( Constants.USER_HOME, file ) } // Directory + existing file
        shouldFailAssert { verify.equal( Constants.USER_DIR,  file ) }
        shouldFailAssert { verify.equal( file, Constants.USER_HOME ) }
        shouldFailAssert { verify.equal( file, Constants.USER_DIR  ) }

        general.delete( file )
        shouldFailAssert { verify.equal( file, file ) }
    }

    
    @Test
    void shouldVerifyEqualFiles()
    {
        def f1 = general.tempFile()
        def f2 = general.tempFile()

        verify.equal( f1, f2 )

        def data = System.currentTimeMillis() as String

        f1.write( data * 10 )
        f2.write( data * 10 )

        verify.equal( f1, f2 )

        f1.append( data * 10 )
        f2.append( data * 10 )

        verify.equal( f1, f2 )

        f1.write( data * 10 )
        f2.write( data * 11 )

        shouldFailAssert{ verify.equal( f1, f2 ) }

        general.delete( f1, f2 )
        shouldFailAssert{ verify.equal( f1, f2 ) }
    }


    @Test
    void shouldVerifyEqualDirectories()
    {
        def d1 = general.tempDirectory()
        def d2 = general.tempDirectory()

        verify.equal( d1, d2 )

        new File( d1, 'a.txt' ).write( 'aa' )
        new File( d2, 'a.txt' ).write( 'aa' )
        
        verify.equal( d1, d2 )

        new File( d1, 'aa.txt' ).write( 'aa' )
        new File( d2, 'aa.txt' ).write( 'ab' )

        shouldFailAssert { verify.equal( d1, d2 ) }

        new File( d2, 'aa.txt' ).write( 'aa' )

        verify.equal( d1, d2 )
        
        new File( d1, 'aa.txt' ).write( 'aa' )
        new File( d2, 'aa.xml' ).write( 'aa' )

        shouldFailAssert { verify.equal( d1, d2 ) }

        shouldFailAssert {
            shouldFailAssert { verify.equal( Constants.USER_DIR,  Constants.USER_DIR, false )}
        }

        shouldFailAssert { verify.equal( Constants.USER_DIR, Constants.USER_HOME, false )}
        
        general.delete( d1, d2 )
        shouldFailAssert { verify.equal( d1, d2 ) }
    }


    @Test
    void shouldVerifyEqualDirectoriesWithPattern()
    {
        def buildDir = new File( Constants.USER_DIR, 'build' )
        def srcDir   = new File( Constants.USER_DIR, 'src'   )

        verify.equal( buildDir, buildDir, false )
        verify.equal( buildDir, buildDir, false, '**/*.class' )
        verify.equal( buildDir, buildDir, false, '*.class' )

        verify.equal( srcDir, srcDir, true )
        verify.equal( Constants.USER_DIR, Constants.USER_DIR, false, '*.class'  )
        verify.equal( Constants.USER_DIR, Constants.USER_DIR, true,  '*.groovy' )
        verify.equal( Constants.USER_DIR, Constants.USER_DIR, true,  '*.xml'    )

        def d1 = general.tempDirectory()
        def d2 = general.tempDirectory()

        new File( d1, 'a.txt' ).write( 'txt'  )
        new File( d1, 'a.xml' ).write( 'xml'  )
        new File( d2, 'a.txt' ).write( 'txt'  )
        new File( d2, 'a.xml' ).write( 'xml2' )

        shouldFailAssert { verify.equal( d1, d2 ) }
        shouldFailAssert { verify.equal( d1, d2, true, '**/*.*'   ) }
        shouldFailAssert { verify.equal( d1, d2, true, '*.*'      ) }
        shouldFailAssert { verify.equal( d1, d2, true, '**/*.xml' ) }
        shouldFailAssert { verify.equal( d1, d2, true, '*.xml'    ) }
        
        verify.equal( d1, d2, true, '**/*.txt' )
        verify.equal( d1, d2, true, '*.txt' )
        verify.equal( d1, d2, true, '*.tx*' )
        verify.equal( d1, d2, true, '*.t*t' )
        verify.equal( d1, d2, true, '*.t*' )

        general.delete( d1, d2 )
        shouldFailAssert { verify.equal( d1, d2 ) }
    }
}
