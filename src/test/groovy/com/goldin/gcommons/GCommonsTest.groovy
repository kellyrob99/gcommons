package com.goldin.gcommons

import groovy.io.FileType
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
    }


    @Test
    void testRecurse()
    {
        def testDir = testDir( 'recurse' )
        def write   = { String path, String content ->
            def file = new File( testDir, path )
            fileBean.mkdirs( file.parentFile )
            file.write( content )
        }

        def eqList = { List l1, List l2 ->
            assert l1.size() == l2.size()
            assert l1.every { l2.contains( it ) }
            assert l2.every { l1.contains( it ) }
        }

        def eqMap = { Map m1, Map m2 ->
            assert m1.size() == m2.size()
            assert m1.every{ key, value -> m2[ key ] == value }
            assert m2.every{ key, value -> m1[ key ] == value }
        }

        write( '1/2/3.txt',  'aaaaaaaaaaaa' ) /* length is 12 */
        write( '5/6/7.txt',  'bbbbbbbbbbb' )  /* length is 11 */
        write( '7/8/22.txt', 'cccccccccc'  )  /* length is 10 */

        def names = []
        testDir.recurse( FileType.FILES, { names << it.name } )
        eqList( names, [ '3.txt', '7.txt', '22.txt' ])

        names = []
        testDir.recurse( FileType.FILES, { names << it.name }, { it.name.endsWith( '3.txt' ) } )
        assert names == [ '3.txt' ]

        names = []
        testDir.recurse( FileType.FILES, { names << it.name }, { it.name.endsWith( '.txt' ) } )
        eqList( names, [ '3.txt', '7.txt', '22.txt' ])

        names = []
        testDir.recurse( FileType.FILES, { names << it.name }, { it.name.endsWith( '.pdf' ) } )
        assert names == []

        names = []
        testDir.recurse( FileType.FILES, { names << it.name }, { it.text.contains( 'b' )} )
        assert names == [ '7.txt' ]

        names = []
        testDir.recurse( FileType.DIRECTORIES, { names << it.name } )
        eqList( names, [ '1', '2', '5', '6', '7', '8' ])

        names = []
        testDir.recurse( FileType.DIRECTORIES, { names << it.name }, { it.directorySize() < 11 } )
        eqList( names, [ '7', '8' ])

        names = []
        testDir.recurse( FileType.DIRECTORIES, { names << it.name }, { it.listFiles().name.contains( '8' ) } )
        assert names == [ '7' ]

        names = []
        testDir.recurse( FileType.DIRECTORIES, { names << it.name }, { it.listFiles().name.contains( '7.txt' ) } )
        assert names == [ '6' ]

        def sizes = [:]
        testDir.recurse( FileType.DIRECTORIES, { sizes[ it.name ] = it.directorySize() } )
        eqMap( sizes, [ '1': 12, '2':12, '5':11, '6':11, '7':10, '8':10 ])

        def counter = 0
        testDir.recurse( FileType.DIRECTORIES, { counter++; true } )
        assert counter == 6

        counter = 0
        testDir.recurse( FileType.DIRECTORIES, { counter++; ( counter < 4 ) } )
        assert counter == 4

        counter = 0
        testDir.recurse( FileType.DIRECTORIES, { counter++; ( counter == 1 ) } )
        assert counter == 2
    }
}
