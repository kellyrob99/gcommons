package com.goldin.gcommons.beans

import com.goldin.gcommons.BaseTest
import java.nio.BufferOverflowException
import org.junit.Test
import com.goldin.gcommons.beans.GeneralBean.ExecOption

/**
 * {@link com.goldin.gcommons.beans.GeneralBean} tests
 */
class GeneralBeanTest extends BaseTest
{

    /**
     * Verifies {@link #shouldFailWith} behavior
     */
    @Test
    void testShouldFail()
    {
        shouldFailWith( RuntimeException )        { throw new RuntimeException()        }
        shouldFailWith( RuntimeException )        { throw new BufferOverflowException() }
        shouldFailWith( BufferOverflowException ) { throw new BufferOverflowException() }
        shouldFailWith( NullPointerException )    { throw new NullPointerException()    }
        shouldFailWith( IOException )             { throw new FileNotFoundException()   }
        shouldFailWith( IOException )             { throw new IOException( new RuntimeException())}

        shouldFailAssert {
            shouldFailWith( NullPointerException ) { throw new RuntimeException() }
        }

        shouldFailAssert { throw new AssertionError() }
        shouldFailAssert { shouldFailAssert { throw new IOException() }}
        shouldFailAssert { assert 3 == 5 }
        shouldFailAssert { assert false  }
        shouldFailAssert { assert null   }
        shouldFailAssert { assert ''     }
        shouldFailAssert { assert 'aa' == 'bb' }
        shouldFailAssert { assert    3 == 4    }
        shouldFailAssert { assert 'aa'.is( new String( 'aa' )) }

        try
        {
            shouldFailWith( IOException ) { throw new Throwable() }
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


    @Test
    void matchShouldFailOnBadInput ()
    {
        shouldFailAssert { generalBean.match( '', ''       ) }
        shouldFailAssert { generalBean.match( '   ', ''    ) }
        shouldFailAssert { generalBean.match( 'aaaa', ''   ) }
        shouldFailAssert { generalBean.match( '  ', 'bbbb' ) }
        shouldFailAssert { generalBean.match( null, 'bbbb' ) }
        shouldFailAssert { generalBean.match( 'cccc', null ) }
        shouldFailAssert { generalBean.match( null, null   ) }
        shouldFailAssert { generalBean.match( null, ''     ) }
        shouldFailAssert { generalBean.match( '  ', null   ) }
    }

    @Test
    void shouldMatch ()
    {
        assert generalBean.match( '/a/b/c/d', '/a/b/c/d' )
        assert generalBean.match( '/a/b/c/d', '**/b/c/d' )
        assert generalBean.match( '/a/b/c/d', '**/c/d' )
        assert generalBean.match( '/a/b/c/d', '**/d' )
        assert generalBean.match( '/a/b/c/d', '**/d' )
        assert generalBean.match( '/a/b/c/d', '**' )
        assert generalBean.match( '/a/b/c/d/1.txt', '**/*.*' )
        assert generalBean.match( '/a/b/c/d/1.txt', '**/1.txt' )
        assert generalBean.match( '/a/b/c/d/1.txt', '**/*.txt' )
        assert generalBean.match( '/a/b/c/d/1.txt', '**/*.t*' )
        assert generalBean.match( '/a/b/c/d/1.txt', '**/*.tx*' )
        assert generalBean.match( '/a/b/c/d/1.txt', '**/*.txt*' )

        assert ! generalBean.match( '/a/b/c/d', '**/*.*' )
        assert ! generalBean.match( '/a/b/c/d/1.txt', '**/*.xml'  )
        assert ! generalBean.match( '/a/b/c/d/3.xml', '**/*.xml2' )
        assert ! generalBean.match( '/a/b/c/d/3.xml', '**/*.txt'  )
        assert ! generalBean.match( '/a/b/c/d/3.xml', '**/*.x'    )
        assert ! generalBean.match( '/a/b/c/d/3.xml', '**/*.xm'   )
        assert ! generalBean.match( '/a/b/c/d/3.xml', '**/4.xml'  )
        assert ! generalBean.match( '/a/b/c/d/3.xml', '**/3xml'  )
        assert ! generalBean.match( '/a/b/c/d/3.xml', 'aaa'  )
        assert ! generalBean.match( '/a/b/c/d/3.xml', 'bbb'  )

        assert generalBean.match( 'c:\\path\\dir', 'c:\\path\\dir' )
        assert generalBean.match( 'c:\\path\\dir', '**\\path\\dir' )
        assert generalBean.match( 'c:\\path\\dir', '**\\dir' )
        assert generalBean.match( 'c:\\path\\dir', '**' )
        assert generalBean.match( 'c:\\path\\dir\\1.txt', '**/*.*' )
        assert generalBean.match( 'c:\\path\\dir\\1.txt', '**/1.txt'  )
        assert generalBean.match( 'c:\\path\\dir\\1.txt', '**/*.txt'  )
        assert generalBean.match( 'c:\\path\\dir\\1.txt', '**/*.t*'   )
        assert generalBean.match( 'c:\\path\\dir\\1.txt', '**/*.tx*'  )
        assert generalBean.match( 'c:\\path\\dir\\1.txt', '**/*.txt*' )

        assert ! generalBean.match( 'c:\\path\\dir', '**/*.*' )
        assert ! generalBean.match( 'c:\\path\\dir\\1.txt', '**/*.xml'  )
        assert ! generalBean.match( 'c:\\path\\dir\\8.xml', '**/*.xml2' )
        assert ! generalBean.match( 'c:\\path\\dir\\8.xml', '**/*.txt'  )
        assert ! generalBean.match( 'c:\\path\\dir\\8.xml', '**/*.x'    )
        assert ! generalBean.match( 'c:\\path\\dir\\8.xml', '**/*.xm'   )
        assert ! generalBean.match( 'c:\\path\\dir\\8.xml', '**/9.xml'  )
        assert ! generalBean.match( 'c:\\path\\dir\\8.xml', '**/8xml'   )
        assert ! generalBean.match( 'c:\\path\\dir\\8.xml', '8xml'      )
        assert ! generalBean.match( 'c:\\path\\dir\\8.xml', '8xml/aaa'  )

        assert generalBean.match( 'd:/some/path/dir',        'd:/some/path/dir' )
        assert generalBean.match( 'd:/some/path/dir',        '**\\path\\dir' )
        assert generalBean.match( 'd:/some/path/dir',        '**/path/dir' )
        assert generalBean.match( 'd:/some/path/dir',        '**\\dir'   )
        assert generalBean.match( 'd:/some/path/dir',        '**/dir'    )
        assert generalBean.match( 'd:/some/path/dir',        '**'        )
        assert generalBean.match( 'd:/some/path/dir/1.txt',  '**/*.*'    )
        assert generalBean.match( 'd:/some\\path/dir/1.txt', '**/1.txt'  )
        assert generalBean.match( 'd:/some/path/dir/1.txt',  '**/*.txt'  )
        assert generalBean.match( 'd:/some\\path/dir/1.txt', '**/*.t*'   )
        assert generalBean.match( 'd:/some/path/dir/1.txt',  '**/*.tx*'  )
        assert generalBean.match( 'd:/some\\path/dir/1.txt', '**/*.txt*' )

        assert ! generalBean.match( 'd:/some/path/dir',        '**/*.*'    )
        assert ! generalBean.match( 'd:/some/path/dir/1.txt',  '**/*.xml'  )
        assert ! generalBean.match( 'd:/some/path/dir/8.xml',  '**/*.xml2' )
        assert ! generalBean.match( 'd:/some\\path/dir/8.xml', '**/*.txt'  )
        assert ! generalBean.match( 'd:/some/path/dir/8.xml',  '**/*.x'    )
        assert ! generalBean.match( 'd:/some/path/dir/8.xml',  '**/*.xm'   )
        assert ! generalBean.match( 'd:/some\\path/dir/8.xml', '**/9.xml'  )
        assert ! generalBean.match( 'd:/some/path/dir/8.xml',  '**/8xml'   )
        assert ! generalBean.match( 'd:/some/path/dir/8.xml',  '8xml'      )
        assert ! generalBean.match( 'd:/some/path/dir/8.xml',  '8xml/aaa'  )
    }


    @Test
    void shouldMatchAgain()
    {
        def check = { String path, String pattern, boolean positiveCheck = true ->
            def result = generalBean.match( path, pattern )
            if ( positiveCheck ) { assert result;       getLog( this ).info( "[$path] matches [$pattern]" ) }
            else                 { assert ( ! result ); getLog( this ).info( "[$path] doesn't match [$pattern]" ) }
        }

        check( 'M1.xml',                                        '**/*' )
        check( 'M2.xml',                                        '**/*.xml' )
        check( 'M3.xml',                                        '*.xml' )
        check( 'M4.xml',                                        'M4.xml' )
        check( '.hudson/hudson.scm.CVSSCM.xml',                 '**/*' )
        check( '.hudson/hudson.scm.CVSSCM.xml',                 '**/*.xml' )
        check( '.hudson/aaa/bbb/someDir/hudson.scm.CVSSCM.xml', '**/someDir/*.xml' )
        check( '.hudson/aaa/bbb/someDir/hudson.scm.CVSSCM.xml', '**/aaa/bbb/someDir/*.xml' )
        check( '.hudson/aaa/bbb/someDir/hudson.scm.CVSSCM.xml', '**/aaa/**/someDir/*.xml' )
        check( '.hudson/aaa/bbb/someDir/hudson.scm.CVSSCM.xml', '**/aaa/**' )
        check( '.hudson/aaa/bbb/someDir/hudson.scm.CVSSCM.xml', '**/bbb/**' )
        check( '.hudson/aaa/bbb/someDir/hudson.scm.CVSSCM.xml', '**/someDir/**' )
        check( '.hudson/aaa/bbb/someDir/hudson.scm.CVSSCM.xml', '**/bbb/**/*.xml' )
        check( 'src/test/resources/configs/google-guice',       '**' )
        check( 'src/test/resources/configs/google-guice',        '**/google-guice' )
        check( 'src/test/resources/configs/google-guice',        '**/google-guice/**' )
        check( 'src/test/resources/configs/google-guice/1.txt',  '**/*.txt' )
        check( 'src/test/resources/configs/google-guice',        'src/test/resources/configs/google-guice' )
        check( 'src/test/resources/configs/google-guice/1.txt',  'src/test/resources/configs/google-guice/1.txt' )
        check( 'src/test/resources/configs/google-guice/1.txt',  'src/**/resources/**/google-guice/1.txt' )
        check( 'src/test/resources/configs/google-guice/1.txt',  'src/**/resources/**/**/1.txt' )
        check( 'src/test/resources/configs/google-guice/1.txt',  'src/**/resources/**/1.txt' )
        check( 'src/test/resources/configs/google-guice/1.txt',  'src/**/1.txt' )
        check( 'src/test/resources/configs/google-guice/1.txt',  '**/1.txt' )

        check( '/home/evgenyg_admin/java/agent/work/265f468bcb78a703/maven-hudson-plugin/full/src/test/resources/configs/gitorious-wsarena3-version1/config.xml',
               '**/*.xml' )
        check( '/home/evgenyg_admin/java/agent/work/265f468bcb78a703/maven-hudson-plugin/full/src/test/resources/configs/gitorious-wsarena3-version1/config.xml',
               '/**/*.xml' )
        check( '/home/evgenyg_admin/java/agent/work/265f468bcb78a703/maven-hudson-plugin/full/src/test/resources/configs/gitorious-wsarena3-version1/config.xml',
               '**' )
        check( '/home/evgenyg_admin/java/agent/work/265f468bcb78a703/maven-hudson-plugin/full/src/test/resources/configs/gitorious-wsarena3-version1/config.xml',
               '/**/' )

        check( 'src/test/resources/configs/google-guice',       '**/*.xml', false )
        check( 'src/test/resources/configs/google-guice',       'src/test/resources/configs/google-guice/a', false )
        check( 'src/test/resources/configs/google-guice',       'a/src/test/resources/configs/google-guice/a', false )
        check( 'src/test/resources/configs/google-guice',       'a/src/test/resources/configs/google-guice', false )
        check( 'src/test/resources/configs/google-guice/a.xml', '**/*.txt', false )
        check( 'src/test/resources/configs/google-guice/a.xml', '**/aaaaa/*.xml', false )
    }


    @Test
    void testTryIt()
    {
        generalBean.tryIt( 1, null, {} )
        shouldFailWith( RuntimeException )  { generalBean.tryIt( 1, String, {} ) }
        shouldFailWith( RuntimeException )  { generalBean.tryIt( 1, String, { 1 } ) }
        shouldFailAssert { shouldFailAssert { generalBean.tryIt( 1, String, { "aaaaa" } ) } }
        shouldFailAssert { shouldFailAssert { generalBean.tryIt( 1, Number, { 33 + 44 } ) } }

        assert '12345' == generalBean.tryIt( 1, String, { "12345" } )
        assert 12345   == generalBean.tryIt( 1, Number, {  12345  } )
        assert 12345   == generalBean.tryIt( 1, Number, { 12345 -5 + 5 } )

        def c =
        {
            int n, int max, String s ->
            def counter = 0
            generalBean.tryIt( max, String )
            {
                if (( ++counter ) == n ) { s }
                else                     { assert false, counter }
            }
        }

        shouldFailWith( RuntimeException )  { assert 'qwerty' == c( 0, 3, 'qwerty' ) }
        shouldFailAssert { assert 'qwerty1' == c( 1, 3, 'qwerty2' ) }
        shouldFailAssert { shouldFailAssert { assert 'qwerty' == c( 2, 3, 'qwerty' ) }}

        assert 'qwerty1' == c( 1, 3, 'qwerty1' )
        assert 'qwerty2' == c( 2, 3, 'qwerty2' )
        assert 'qwerty3' == c( 3, 3, 'qwerty3' )
        assert 'qwerty4' == c( 3, 4, 'qwerty4' )
        assert 'qwerty5' == c( 4, 5, 'qwerty5' )
        assert 'qwerty6' == c( 1, 5, 'qwerty6' )
    }


    @Test
    void testS()
    {
        assert 's' == generalBean.s( 0 )
        assert ''  == generalBean.s( 1 )
        assert 's' == generalBean.s( 2 )

        assert '5 attempts'   == "5 attempt${ generalBean.s( 5 ) }"
        assert '1 attempt'    == "1 attempt${ generalBean.s( 1 ) }"
        assert 'many chances' == "many chance${ generalBean.s( 1000 ) }"
        assert 'one chance'   == "one chance${ generalBean.s( 1 ) }"
    }


    @Test
    void testArray()
    {
        assert [1, 2] as Integer[] == generalBean.array( [1, 2] as Integer[], 3,    Integer )
        assert [1, 2] as Integer[] == generalBean.array( [1, 2] as Integer[], null, Integer )
        assert [3]    as Integer[] == generalBean.array( null,                3,    Integer )
        assert [77]   as Integer[] == generalBean.array( null,                77,   Integer )
        assert []     as Integer[] == generalBean.array( [] as Integer[],     5,    Integer )
        assert []     as Integer[] == generalBean.array( null,                null, Integer )
    }


    @Test
    void testChoose()
    {
        assert 3   == generalBean.choose( null, null, null, 3 )
        assert 3   == generalBean.choose( null, null, null, 3, null )
        assert "4" == generalBean.choose( null, null, null, null, "4", "5" )
        assert [:] == generalBean.choose( [:] )
        assert [:] == generalBean.choose( null, [:], null )
        assert []  == generalBean.choose( null, [], [:], null )
    }

    
    @Test
    void shouldExecute()
    {
        List<String> commonCommands  = [ 'call java -version', 'call groovy --version', 'call gradle -version', 'call mvn -version' ]
        List<String> windowsCommands = [ 'dir'    ]
        List<String> unixCommands    = [ 'ls -al' ]
        List<String> commands        = null
        File         tempFile        = null

        if ( generalBean.isWindows())
        {
            tempFile = fileBean.tempFile( '.bat' )
            tempFile.write(( windowsCommands + commonCommands ).join( constantsBean.CRLF ))
            commands = [ tempFile.canonicalPath ]
        }
        else
        {
            tempFile = fileBean.tempFile( '.sh' )
            tempFile.write(( unixCommands + commonCommands ).join( constantsBean.CRLF ))
            commands = [ 'sudo chmod +x ' + tempFile.canonicalPath,
                         tempFile.canonicalPath ]
        }

        getLog( this ).info( "Commands to run are: $commands" )

        for ( command in commands )
        {
            assert ! generalBean.execute( command )
            assert ! generalBean.execute( command, ExecOption.CommonsExec )
            assert ! generalBean.execute( command, ExecOption.ProcessBuilder )
            assert ! generalBean.execute( command, ExecOption.Runtime )
        }

        fileBean.delete( tempFile )
    }
}
