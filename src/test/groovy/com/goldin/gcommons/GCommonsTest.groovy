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


    File prepareTestRecurse()
    {
        def testDir = testDir( 'recurse' )
        def write   = { String path, String content ->
            def file = new File( testDir, path )
            fileBean.mkdirs( file.parentFile )
            file.write( content )
        }

        write( '1/2/3.txt',  'aaaaaaaaaaaa' ) /* length is 12 */
        write( '5/6/7.txt',  'bbbbbbbbbbb'  ) /* length is 11 */
        write( '7/8/22.txt', 'cccccccccc'   ) /* length is 10 */

        testDir
    }


    @Test
    void testRecurseFiles()
    {
        def testDir = prepareTestRecurse()
        def names   = []
        def c       = { names << it.name }

        testDir.recurse([ type: FileType.FILES       ]){ assert it.isFile() }
        testDir.recurse([ type: FileType.DIRECTORIES ]){ assert it.isDirectory() }
        testDir.recurse([ type: FileType.ANY         ]){ assert it.isFile() || it.isDirectory() }

        testDir.recurse([ type: FileType.FILES ], c )
        assertSameLists( names, [ '3.txt', '7.txt', '22.txt' ])

        names = []
        testDir.recurse([ type   : FileType.FILES,
                          filter : { it.name.endsWith( '3.txt' )} ], c )
        assert names == [ '3.txt' ]

        names = []
        testDir.recurse([ type   : FileType.FILES,
                          filter : { it.name.endsWith( '.txt' )} ], c )
        assertSameLists( names, [ '3.txt', '7.txt', '22.txt' ])

        names = []
        testDir.recurse([ type   : FileType.FILES,
                          filter : { it.name.endsWith( '.pdf' )} ], c )
        assert names == []

        names = []
        testDir.recurse([ type   : FileType.FILES,
                          filter : { it.text.contains( 'b' )} ]){ assert it.text.contains( 'b' )
                                                                  names << it.name }
        assert names == [ '7.txt' ]

        def counter = 0
        testDir.recurse([ type   : FileType.FILES,
                          filter : { it.isFile() && it.text.size() == 10 } ]){ ++counter; false }
        assert counter == 1
    }


    @Test
    void testRecurseDirectories()
    {
        def testDir = prepareTestRecurse()
        def names   = []
        def c       = { names << it.name }

        testDir.recurse([ type : FileType.DIRECTORIES ], c )
        assertSameLists( names, [ '1', '2', '5', '6', '7', '8' ])

        names = []
        testDir.recurse([ type   : FileType.DIRECTORIES,
                          filter : { it.directorySize() < 11 } ], c )
        assertSameLists( names, [ '7', '8' ])

        names = []
        testDir.recurse([ type   : FileType.DIRECTORIES,
                          filter : { it.listFiles().name.contains( '8' )} ], c )
        assert names == [ '7' ]

        names = []
        testDir.recurse([ type   : FileType.DIRECTORIES,
                          filter : { it.listFiles().name.contains( '7.txt' ) } ], c )
        assert names == [ '6' ]

        names = []
        testDir.recurse([ type   : FileType.DIRECTORIES,
                          filter : { it.listFiles().name.contains( 'aaa.exe' ) } ], c )
        assert names == []

        names = []
        testDir.recurse([ type   : FileType.DIRECTORIES,
                          filter : { ( it.listFiles() as List ).contains( new File( 'aaaa' )) } ], c )
        assert names == []

        names = []
        testDir.recurse([ type   : FileType.DIRECTORIES,
                          filter : { File dir -> dir.listFiles().name.any{ String s -> s ==~ /.*\.txt/ }} ], c )
        assertSameLists( names, [ '2', '6', '8' ])

        def sizes = [:]
        testDir.recurse([ type   : FileType.DIRECTORIES ]){ sizes[ it.name ] = it.directorySize() }
        assertSameMaps( sizes, [ '1': 12, '2':12, '5':11, '6':11, '7':10, '8':10 ])

        def counter = -1
        testDir.recurse([ type   : FileType.DIRECTORIES ]){ counter++ }
        assert counter == 5

        counter = -1
        testDir.recurse([ type        : FileType.DIRECTORIES,
                          filter      : { true },
                          stopOnFalse : true ]){ counter++ }
        assert counter == 1

        counter = -1
        testDir.recurse([ type        : FileType.DIRECTORIES,
                          stopOnFalse : true ]){ ++counter }
        assert counter == 0

        counter = 0
        testDir.recurse([ type        : FileType.DIRECTORIES,
                          stopOnFalse : true ]){ counter++; ( counter < 4 ) }
        assert counter == 4
    }


    @Test
    void testRecurseStopOnFalse()
    {
        def testDir = prepareTestRecurse()
        def counter = 0


        testDir.recurse([ type : FileType.ANY ]) { counter++ }
        assert counter == 9

        counter = 0
        testDir.recurse([ type : FileType.ANY ]){ ++counter; ( counter < 5 ) }
        assert counter == 9

        counter = 0
        testDir.recurse([ type : FileType.ANY ]){ ++counter; false }
        assert counter == 9

        counter = 0
        testDir.recurse([ type        : FileType.ANY,
                          filter      : { it.isDirectory() },
                          stopOnFalse : true ]) { ++counter; ( counter < 3 ) }
        assert counter == 3

        counter = 0
        testDir.recurse([ type        : FileType.DIRECTORIES,
                          filter      : { ! it.isFile() },
                          stopOnFalse : true ]){ counter++; ( counter == 1 ) }
        assert counter == 2

        counter = 0
        testDir.recurse([ type        : FileType.DIRECTORIES,
                          filter      : { ! it.isFile() },
                          stopOnFalse : true ]) { counter++; ( counter > 0 ) }
        assert counter == 6
    }
}
