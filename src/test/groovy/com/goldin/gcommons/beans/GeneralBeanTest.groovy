package com.goldin.gcommons.beans

import com.goldin.gcommons.BaseTest
import org.junit.Test

 /**
 * {@link com.goldin.gcommons.beans.GeneralBean} tests
 */
class GeneralBeanTest extends BaseTest
{

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
}
