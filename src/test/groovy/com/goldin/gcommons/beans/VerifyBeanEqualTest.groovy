package com.goldin.gcommons.beans

import com.goldin.gcommons.BaseTest
import org.junit.Test
import static com.goldin.gcommons.Constants.*


 /**
 * {@link com.goldin.gcommons.beans.VerifyBean#equal(File, File, boolean, String, String)} tests
 */
class VerifyBeanEqualTest extends BaseTest
{
    @Test
    void shouldFailOnNullInput()
    {
        shouldFailAssert { verifyBean.equal( new File( "doesn't exist" ), null ) }

        shouldFailWith( NullPointerException ) { verifyBean.equal( null, null ) }
        shouldFailWith( NullPointerException ) { verifyBean.equal( null, new File( "aaa" )) }
        shouldFailWith( NullPointerException ) { verifyBean.equal( null, USER_DIR_FILE  ) }
        shouldFailWith( NullPointerException ) { verifyBean.equal( null, USER_HOME_FILE ) }
        shouldFailWith( NullPointerException ) { verifyBean.equal( USER_DIR_FILE,  null ) }
        shouldFailWith( NullPointerException ) { verifyBean.equal( USER_HOME_FILE, null ) }
    }


    @Test
    void shouldFailOnMissingFiles()
    {
        shouldFailAssert { verifyBean.equal( new File( "doesn't exist" ),
                                             new File( "doesn't exist" ) ) }

        shouldFailAssert { verifyBean.equal( new File( USER_DIR_FILE, '1.txt' ),
                                             new File( USER_DIR_FILE, '1.txt' )) }

        shouldFailAssert { verifyBean.equal( new File( USER_DIR_FILE, '1.txt' ),
                                             USER_DIR_FILE) }

        def file = new File( USER_HOME_FILE, 'a.txt' )

        shouldFailAssert { verifyBean.equal( USER_HOME_FILE, file ) } // Directory + missing file

        file.write( 'anything' )

        shouldFailAssert { verifyBean.equal( USER_HOME_FILE, file ) } // Directory + existing file
        shouldFailAssert { verifyBean.equal( USER_DIR_FILE,  file ) }
        shouldFailAssert { verifyBean.equal( file,  USER_HOME_FILE) }
        shouldFailAssert { verifyBean.equal( file,  USER_DIR_FILE) }

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
            shouldFailAssert { verifyBean.equal( USER_DIR_FILE,  USER_DIR_FILE, false )}
        }

        shouldFailAssert { verifyBean.equal( USER_DIR_FILE,  USER_HOME_FILE, false )}

        fileBean.delete( d1, d2 )
        shouldFailAssert { verifyBean.equal( d1, d2 ) }
    }


    @Test
    void shouldVerifyEqualDirectoriesWithPattern()
    {
        def buildDir = new File( USER_DIR_FILE, 'build/classes' )
        def srcDir   = new File( USER_DIR_FILE, 'src/main'      )

        verifyBean.equal( buildDir, buildDir, false )
        verifyBean.equal( buildDir, buildDir, false, '**/*.class' )
        verifyBean.equal( buildDir, buildDir, false, '*.class' )

        verifyBean.equal( srcDir, srcDir, true )
        verifyBean.equal( srcDir, srcDir, false, '*.class'  )
        verifyBean.equal( srcDir, srcDir, true,  '*.groovy' )
        verifyBean.equal( srcDir, srcDir, true,  '*.xml'    )

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
