package com.goldin.gcommons.beans

import com.goldin.gcommons.BaseTest
import org.junit.Test



 /**
 * {@link com.goldin.gcommons.beans.VerifyBean} tests
 */
class VerifyBeanTest extends BaseTest
{
    @Test
    void shouldVerifyEmptyStrings()
    {
        verifyBean.notNullOrEmpty( 'aa' )
        verifyBean.notNullOrEmpty( 'aa', 'bb' )

        shouldFailAssert { verifyBean.notNullOrEmpty( 'aa', 'bb', null ) }
        shouldFailAssert { verifyBean.notNullOrEmpty( 'aa', 'bb', ''   ) }
        shouldFailAssert { verifyBean.notNullOrEmpty( 'aa', 'bb', ' '  ) }
        shouldFailAssert { verifyBean.notNullOrEmpty( '', 'bb', ' '  ) }
        shouldFailAssert { verifyBean.notNullOrEmpty( ' ', 'bb', ' '  ) }
        shouldFailAssert { shouldFailAssert { verifyBean.notNullOrEmpty( ' c', 'bb', ' d'  ) }}
    }

    
    @Test
    void shouldVerifyEmptyCollections()
    {
        verifyBean.notNullOrEmpty( ['aa'] )
        verifyBean.notNullOrEmpty( ['aa'], ['bb'] )
        verifyBean.notNullOrEmpty( ['aa'], ['bb'], ['zzz'] )

        shouldFailAssert { verifyBean.notNullOrEmpty( ['aa'], ['bb'], null ) }
        shouldFailAssert { verifyBean.notNullOrEmpty( ['aa'], ['bb'], []   ) }
        shouldFailAssert { verifyBean.notNullOrEmpty( ['aa', 'bb'], []  ) }
        shouldFailAssert { verifyBean.notNullOrEmpty( null, ['aa', 'bb'], []  ) }
        shouldFailAssert { verifyBean.notNullOrEmpty( [], null, ['aa', 'bb'], []  ) }

        shouldFailAssert { shouldFailAssert { verifyBean.notNullOrEmpty( [''], ['bb'], [' ']  ) }}
        shouldFailAssert { shouldFailAssert { verifyBean.notNullOrEmpty( [' '], ['bb', ' ']  ) }}
        shouldFailAssert { shouldFailAssert { verifyBean.notNullOrEmpty( [' c'], ['bb'], [' d']  ) }}
    }


    @Test
    void shouldVerifyExists()
    {
        verifyBean.exists( constantsBean.USER_DIR_FILE  )
        verifyBean.exists( constantsBean.USER_HOME_FILE )

        def f = fileBean.tempFile()
        verifyBean.exists( f )
        fileBean.delete( f )

        shouldFailAssert { verifyBean.exists( f ) }
        shouldFailAssert { verifyBean.exists( new File( "Doesn't exist" )) }
        shouldFailWith( NullPointerException ) { verifyBean.exists( new File( System.getProperty( "aaa" ))) }
    }


    @Test
    void shouldVerifyFile()
    {
        def f = fileBean.tempFile()
        verifyBean.file( f )
        shouldFailAssert { verifyBean.file( f.getParentFile() ) }
        shouldFailAssert { verifyBean.file( f.getParentFile().getParentFile()) }

        fileBean.delete( f )

        shouldFailAssert { verifyBean.file( f ) }
    }


    @Test
    void shouldVerifyDirectory()
    {
        verifyBean.directory( constantsBean.USER_DIR_FILE  )
        verifyBean.directory( constantsBean.USER_HOME_FILE )

        def f = fileBean.tempFile()
        verifyBean.file( f )
        verifyBean.directory( f.getParentFile())
        verifyBean.directory( f.getParentFile().getParentFile())

        fileBean.delete( f )

        shouldFailAssert { verifyBean.file( f ) }

        verifyBean.directory( f.getParentFile())
        verifyBean.directory( f.getParentFile().getParentFile())
    }

    @Test
    void shouldVerifyEqual()
    {
        for ( archiveName in testArchives().keySet())
        {
            File dir1 = testDir( 'unpack-1' )
            File dir2 = testDir( 'unpack-2' )

            fileBean.unpack( testResource( archiveName + '.zip' ), dir1 )
            fileBean.unpack( testResource( archiveName + '.jar' ), dir2 )

            verifyBean.with {
                equal( dir1, dir2 )
                equal( dir1, dir2, false )
                equal( dir1, dir2, true, '**/*.xml' )
                equal( dir1, dir2, true, '**/*.xml', 'windows' )
                equal( dir1, dir2, true, '**/*.xml', 'linux'   )
                equal( dir1, dir2, true, '**/*.jar' )
            }
            
            fileBean.delete( fileBean.files( dir2, [ '**/*.xml' ] ) as File[] )

            shouldFailAssert { verifyBean.equal( dir1, dir2 )}
            shouldFailAssert { verifyBean.equal( dir1, dir2, false )}
            shouldFailAssert { shouldFailAssert { verifyBean.equal( dir2, dir1, true, '**/*.xml' )}}
            shouldFailAssert { verifyBean.equal( dir1, dir2, true, '**/*.xml' )}
            shouldFailAssert { verifyBean.equal( dir1, dir2, true, '**/*.xml', 'windows' )}
            shouldFailAssert { verifyBean.equal( dir1, dir2, true, '**/*.xml', 'linux'   )}
            shouldFailAssert { shouldFailAssert { verifyBean.equal( dir1, dir2, true, '**/*.jar' )}}
        }
    }
}
