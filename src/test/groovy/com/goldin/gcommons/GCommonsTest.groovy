package com.goldin.gcommons

import org.junit.Test

 /**
 * {@link GCommons} entry points test
 */
class GCommonsTest extends BaseTest
{
    @Test
    void shouldRefreshContext()
    {
        assert GCommons.context() == GCommons.context()
        assert GCommons.context() == GCommons.context( false )
        assert GCommons.context() != GCommons.context( true  )
    }

    @Test
    void shouldRetrieveBeans()
    {
        assert GCommons.constants()
        assert GCommons.constants( false )
        assert GCommons.constants( true )

        assert GCommons.verify()
        assert GCommons.verify( false )
        assert GCommons.verify( true )

        assert GCommons.general()
        assert GCommons.general( false )
        assert GCommons.general( true )

        assert GCommons.file()
        assert GCommons.file( false )
        assert GCommons.file( true )

        assert GCommons.io()
        assert GCommons.io( false )
        assert GCommons.io( true )

        assert GCommons.net()
        assert GCommons.net( false )
        assert GCommons.net( true )

    }


    @Test
    void shouldRefresh()
    {
        assert GCommons.general() == GCommons.general()
        assert GCommons.general() == GCommons.general( false )

        assert GCommons.verify() == GCommons.verify()
        assert GCommons.verify() == GCommons.verify( false )

// http://evgeny-goldin.org/youtrack/issue/gc-9
//        assert GCommons.general() != GCommons.general( true  )
//        assert GCommons.verify()  != GCommons.verify( true  )
//        assert GCommons.net()     != GCommons.net( true  )
    }


    @Test
    void testSplitWithDirectorySize()
    {
        def text1 = '1\n2\n3'
        def text2 = """
11111111111111111
rrrrrrrrrrr
yyyyyyyyyyyyyyyyyyyyyyyyy
"""
        def text3 = """
eqweqwdsadfaf
dfsafsas saf asf safasfa
wetqfasfdasfasf
"""
        def text4 = """
d;akjcZL;KJCal;kf kl LK
QWRJALKJF DFK AFSLAKJF AKJ
AWD;    2394OI9RURAl    129ui
"""

        def mkdir     = { File f   -> fileBean.mkdirs( f.parentFile ); f }
        def eachLine  = { String s -> s.splitWith( 'eachLine' )*.trim().findAll{ it }}
        def eachLineF = { File f   -> f.splitWith( 'eachLine' )*.trim().findAll{ it }}

        assert [ '1', '2', '3' ]                                                                            == eachLine( text1 )
        assert [ '11111111111111111', 'rrrrrrrrrrr', 'yyyyyyyyyyyyyyyyyyyyyyyyy' ]                          == eachLine( text2 )
        assert [ 'eqweqwdsadfaf', 'dfsafsas saf asf safasfa', 'wetqfasfdasfasf'  ]                          == eachLine( text3 )
        assert [ 'd;akjcZL;KJCal;kf kl LK', 'QWRJALKJF DFK AFSLAKJF AKJ', 'AWD;    2394OI9RURAl    129ui' ] == eachLine( text4 )

        def filesDir = testDir( 'files' )
        def f1       = mkdir( new File( filesDir, '1.txt'     ))
        def f2       = mkdir( new File( filesDir, '1/2/3.txt' ))
        def f3       = mkdir( new File( filesDir, '5/6/8.txt' ))

        f1.write ( text1 )
        f1.append( text2 )

        f2.write ( text2 )
        f2.append( text3 )

        f3.write ( text3 )
        f3.append( text4 )

        assert eachLineF( f1 ) == [ '1', '2', '3', '11111111111111111', 'rrrrrrrrrrr', 'yyyyyyyyyyyyyyyyyyyyyyyyy' ]
        assert eachLineF( f2 ) == [ '11111111111111111', 'rrrrrrrrrrr', 'yyyyyyyyyyyyyyyyyyyyyyyyy', 'eqweqwdsadfaf', 'dfsafsas saf asf safasfa', 'wetqfasfdasfasf' ]
        assert eachLineF( f3 ) == [ 'eqweqwdsadfaf', 'dfsafsas saf asf safasfa', 'wetqfasfdasfasf', 'd;akjcZL;KJCal;kf kl LK', 'QWRJALKJF DFK AFSLAKJF AKJ', 'AWD;    2394OI9RURAl    129ui' ]

        [( text1 + text2 ), ( text2 + text3 ), ( text3 + text4 )].bytes as List == [ f1, f2, f3 ]*.splitWith( 'eachByte' )
        assert filesDir.directorySize() == text1.size() + text2.size() + text2.size() + text3.size() + text3.size() + text4.size()


        shouldFailAssert { "aa".splitWith( ''   ) }
        shouldFailAssert { "aa".splitWith( '  ' ) }
        shouldFailAssert { "aa".splitWith( null ) }
        shouldFailAssert { "aa".splitWith( 'opa' ) }
        shouldFailAssert { "aa".splitWith( 'eachLine1' ) }
        shouldFailAssert { "aa".splitWith( 'size' ) }
        shouldFailAssert { "aa".splitWith( 'toString' ) }

        shouldFailAssert { constantsBean.USER_DIR_FILE.splitWith( 'eachDi'   ) }
        shouldFailAssert { constantsBean.USER_DIR_FILE.splitWith( 'eachDirr' ) }
        shouldFailAssert { constantsBean.USER_DIR_FILE.splitWith( 'exists'   ) }
        shouldFailAssert { constantsBean.USER_DIR_FILE.splitWith( 'isFile'   ) }

        shouldFailAssert { shouldFailAssert { "aa".splitWith( 'eachLine' ) }}
        shouldFailAssert { shouldFailAssert { "aa\nbb".splitWith( 'eachLine' ) }}
        shouldFailAssert { shouldFailAssert { "aa\nbb\ncc".splitWith( 'eachLine' ) }}
        shouldFailAssert { shouldFailAssert { constantsBean.USER_DIR_FILE.splitWith( 'eachDir'  ) }}
        shouldFailAssert { shouldFailAssert { constantsBean.USER_DIR_FILE.splitWith( 'eachFile' ) }}

    }
}
