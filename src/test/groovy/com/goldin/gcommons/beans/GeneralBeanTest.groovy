package com.goldin.gcommons.beans

import com.goldin.gcommons.BaseTest
import java.nio.BufferOverflowException
import org.junit.Test

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
    void testMatch()
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
    void testTryIt()
    {
        generalBean.tryIt( 1, null, {} )
        shouldFailWith( RuntimeException )  { generalBean.tryIt( 1, String.class, {} ) }
        shouldFailWith( RuntimeException )  { generalBean.tryIt( 1, String.class, { 1 } ) }
        shouldFailAssert { shouldFailAssert { generalBean.tryIt( 1, String.class, { "aaaaa" } ) } }
        shouldFailAssert { shouldFailAssert { generalBean.tryIt( 1, Number.class, { 33 + 44 } ) } }

        assert '12345' == generalBean.tryIt( 1, String.class, { "12345" } )
        assert 12345   == generalBean.tryIt( 1, Number.class, {  12345  } )
        assert 12345   == generalBean.tryIt( 1, Number.class, { 12345 -5 + 5 } )

        def c =
        {
            int n, int max, String s ->
            def counter = 0
            generalBean.tryIt( max, String.class )
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
}
