package com.goldin.gcommons.beans

import org.junit.Test
import com.goldin.gcommons.BaseTest
import com.goldin.gcommons.Constants

/**
 * {@link com.goldin.gcommons.beans.VerifyBean#equal(File, File, boolean, String, String)} tests
 */
class VerifyBeanEqualTest extends BaseTest
{
    @Test
    void shouldFailOnNullInput()
    {
        shouldFailAssert { verifyBean.equal( new File( "doesn't exist" ), null ) }

        shouldFailWith( NullPointerException.class ) { verifyBean.equal( null, null ) }
        shouldFailWith( NullPointerException.class ) { verifyBean.equal( null, new File( "aaa" )) }
        shouldFailWith( NullPointerException.class ) { verifyBean.equal( null, Constants.USER_DIR ) }
        shouldFailWith( NullPointerException.class ) { verifyBean.equal( null, Constants.USER_HOME ) }
        shouldFailWith( NullPointerException.class ) { verifyBean.equal( Constants.USER_DIR,  null ) }
        shouldFailWith( NullPointerException.class ) { verifyBean.equal( Constants.USER_HOME, null ) }
    }


    @Test
    void shouldFailOnMissingFiles()
    {
        shouldFailAssert { verifyBean.equal( new File( "doesn't exist" ),
                                         new File( "doesn't exist" ) ) }

        shouldFailAssert { verifyBean.equal( new File( Constants.USER_DIR, '1.txt' ),
                                         new File( Constants.USER_DIR, '1.txt' )) }
        
        shouldFailAssert { verifyBean.equal( new File( Constants.USER_DIR, '1.txt' ),
                                         Constants.USER_DIR ) }

        def file = new File( Constants.USER_HOME, 'a.txt' )

        shouldFailAssert { verifyBean.equal( Constants.USER_HOME, file ) } // Directory + missing file

        file.write( 'anything' )

        shouldFailAssert { verifyBean.equal( Constants.USER_HOME, file ) } // Directory + existing file
        shouldFailAssert { verifyBean.equal( Constants.USER_DIR,  file ) }
        shouldFailAssert { verifyBean.equal( file, Constants.USER_HOME ) }
        shouldFailAssert { verifyBean.equal( file, Constants.USER_DIR  ) }

        fileBean.delete( file )
        shouldFailAssert { verifyBean.equal( file, file ) }
    }

    
    @Test
    void shouldVerifyEqualFiles()
    {
        def f1 = fileBean.tempFile()
        def f2 = fileBean.tempFile()

        verifyBean.equal( f1, f2 )

        def data = System.currentTimeMillis() as String

        f1.write( data * 10 )
        f2.write( data * 10 )

        verifyBean.equal( f1, f2 )

        f1.append( data * 10 )
        f2.append( data * 10 )

        verifyBean.equal( f1, f2 )

        f1.write( data * 10 )
        f2.write( data * 11 )

        shouldFailAssert{ verifyBean.equal( f1, f2 ) }

        fileBean.delete( f1, f2 )
        shouldFailAssert{ verifyBean.equal( f1, f2 ) }
    }


    @Test
    void shouldVerifyEqualDirectories()
    {
        def d1 = fileBean.tempDirectory()
        def d2 = fileBean.tempDirectory()

        verifyBean.equal( d1, d2 )

        new File( d1, 'a.txt' ).write( 'aa' )
        new File( d2, 'a.txt' ).write( 'aa' )
        
        verifyBean.equal( d1, d2 )

        new File( d1, 'aa.txt' ).write( 'aa' )
        new File( d2, 'aa.txt' ).write( 'ab' )

        shouldFailAssert { verifyBean.equal( d1, d2 ) }

        new File( d2, 'aa.txt' ).write( 'aa' )

        verifyBean.equal( d1, d2 )
        
        new File( d1, 'aa.txt' ).write( 'aa' )
        new File( d2, 'aa.xml' ).write( 'aa' )

        shouldFailAssert { verifyBean.equal( d1, d2 ) }

        shouldFailAssert {
            shouldFailAssert { verifyBean.equal( Constants.USER_DIR,  Constants.USER_DIR, false )}
        }

        shouldFailAssert { verifyBean.equal( Constants.USER_DIR, Constants.USER_HOME, false )}
        
        fileBean.delete( d1, d2 )
        shouldFailAssert { verifyBean.equal( d1, d2 ) }
    }


    @Test
    void shouldVerifyEqualDirectoriesWithPattern()
    {
        def buildDir = new File( Constants.USER_DIR, 'build' )
        def srcDir   = new File( Constants.USER_DIR, 'src'   )

        verifyBean.equal( buildDir, buildDir, false )
        verifyBean.equal( buildDir, buildDir, false, '**/*.class' )
        verifyBean.equal( buildDir, buildDir, false, '*.class' )

        verifyBean.equal( srcDir, srcDir, true )
        verifyBean.equal( Constants.USER_DIR, Constants.USER_DIR, false, '*.class'  )
        verifyBean.equal( Constants.USER_DIR, Constants.USER_DIR, true,  '*.groovy' )
        verifyBean.equal( Constants.USER_DIR, Constants.USER_DIR, true,  '*.xml'    )

        def d1 = fileBean.tempDirectory()
        def d2 = fileBean.tempDirectory()

        new File( d1, 'a.txt' ).write( 'txt'  ) // Same content for 'txt' files
        new File( d2, 'a.txt' ).write( 'txt'  )
        new File( d1, 'a.xml' ).write( 'xml1' ) // Different content for 'xml' files
        new File( d2, 'a.xml' ).write( 'xml2' )

        shouldFailAssert { verifyBean.equal( d1, d2 ) }
        shouldFailAssert { verifyBean.equal( d1, d2, true, '**/*.*'    ) }
        shouldFailAssert { verifyBean.equal( d1, d2, true, '**/*.xml'  ) }
        shouldFailAssert { verifyBean.equal( d1, d2, true, '**\\*.xml' ) }
        shouldFailAssert { verifyBean.equal( d1, d2, true, '**/a.xml'  ) }
        shouldFailAssert { verifyBean.equal( d1, d2, true, '**\\a.xml' ) }
        shouldFailAssert { verifyBean.equal( d1, d2, true, '**//a.xml' ) }

        verifyBean.equal( d1, d2, true, '*.*' )
        verifyBean.equal( d1, d2, true, '**/*.txt' )
        verifyBean.equal( d1, d2, true, '*.txt' )
        verifyBean.equal( d1, d2, true, '*.tx*' )
        verifyBean.equal( d1, d2, true, '*.t*t' )
        verifyBean.equal( d1, d2, true, '*.t*' )
        verifyBean.equal( d1, d2, true, '**/a.txt' )
        verifyBean.equal( d1, d2, true, '**\\a.txt' )
        verifyBean.equal( d1, d2, true, '**//a.txt' )
        verifyBean.equal( d1, d2, true, 'b.xml' )
        verifyBean.equal( d1, d2, true, '**/b.xml' )
        verifyBean.equal( d1, d2, true, '**/c.xml' )
        verifyBean.equal( d1, d2, true, '**//b.xml' )
        verifyBean.equal( d1, d2, true, '**\\b.xml' )

        fileBean.delete( d1, d2 )
        shouldFailAssert { verifyBean.equal( d1, d2 ) }
    }
}
