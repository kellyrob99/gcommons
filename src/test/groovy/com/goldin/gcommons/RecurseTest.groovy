package com.goldin.gcommons

import groovy.io.FileType
import org.junit.Test

/**
 * "File.metaClass.recurse" tests
 */
class RecurseTest extends BaseTest
{

    private File prepareTestRecurse()
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
    void testRecurseTypes()
    {
        def testDir = prepareTestRecurse()

        testDir.with {
            recurse([ type : FileType.FILES       ]){ assert it.isFile() }
            recurse([ type : FileType.DIRECTORIES ]){ assert it.isDirectory() }
            recurse([ type : FileType.ANY         ]){ assert it.isFile() || it.isDirectory() }

            recurse([ filterType: FileType.FILES,
                      filter    : { assert it.isFile() }]){}

            recurse([ filterType: FileType.DIRECTORIES,
                      filter    : { assert it.isDirectory() }]){}

            recurse([ filterType: FileType.ANY,
                     filter    : { assert it.isFile() || it.isDirectory() }]){}

            recurse([ type      : FileType.DIRECTORIES,
                      filterType: FileType.FILES,
                      filter    : { assert it.isFile() }]){ assert it.isDirectory() }

            recurse([ type      : FileType.FILES,
                      filterType: FileType.DIRECTORIES,
                      filter    : { assert it.isDirectory() }]){ assert it.isFile() }

            recurse([ type      : FileType.FILES,
                      filterType: FileType.ANY,
                      filter    : { assert it.isFile() || it.isDirectory() }]){ assert it.isFile() }
        }
    }


    @Test
    void testRecurseFiles()
    {
        def testDir = prepareTestRecurse()
        def names   = []
        def c       = { names << it.name }

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
        assert counter == 5

        counter = 0
        testDir.recurse([ type        : FileType.DIRECTORIES,
                          filter      : { true },
                          stopOnFalse : true ]){ counter++ }
        assert counter == 5

        counter = 0
        testDir.recurse([ type        : FileType.DIRECTORIES,
                          filter      : { true },
                          stopOnFalse : true ]){ ++counter }
        assert counter == 6

        counter = -1
        testDir.recurse([ type        : FileType.DIRECTORIES,
                          stopOnFalse : true ]){ ++counter }
        assert counter == 4

        counter = 0
        testDir.recurse([ type        : FileType.DIRECTORIES,
                          stopOnFalse : true ]){ counter++; ( counter < 2 ) }
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
        assert counter == 4

        counter = 0
        testDir.recurse([ type        : FileType.DIRECTORIES,
                          filter      : { ! it.isFile() },
                          stopOnFalse : true ]){ counter++; ( counter == 1 ) }
        assert counter == 4

        counter = 0
        testDir.recurse([ type        : FileType.DIRECTORIES,
                          filter      : { ! it.isFile() },
                          stopOnFalse : true ]) { counter++; ( counter > 0 ) }
        assert counter == 6
    }


    @Test
    void testRecurseStopOnFilter()
    {
        def testDir = prepareTestRecurse()
        def counter = 0
        def names   = []
        def c       = { names << it.name }

        testDir.recurse([ filterType   : FileType.DIRECTORIES,
                          filter       : { File dir -> ['1', '2'].any{ it == dir.name } /* Dir name is '1' or '2' */ },
                          stopOnFilter : true ], c )
        assert names == [ '3.txt' ]

        names = []
        testDir.recurse([ filterType   : FileType.DIRECTORIES,
                          filter       : { File dir -> ['1', '2'].any{ it == dir.name }} /* Dir name is '1' or '2' */ ], c )
        assertSameLists( names, [ '3.txt', '7.txt', '22.txt' ] )

        names = []
        testDir.recurse([ filterType   : FileType.DIRECTORIES,
                          filter       : {},
                          stopOnFilter : true ], c )
        assert names == []

        names = []
        testDir.recurse([ filterType   : FileType.DIRECTORIES,
                          filter       : { false },
                          stopOnFilter : true ], c )
        assert names == []

        names = []
        testDir.recurse([ type         : FileType.ANY,
                          filterType   : FileType.DIRECTORIES,
                          filter       : { it.name != '5' },
                          stopOnFilter : true ], c )
        assertSameLists( names, [ '1', '2', '3.txt', '7', '8', '22.txt' ])

        names = []
        testDir.recurse([ filterType   : FileType.DIRECTORIES,
                          filter       : { true },
                          stopOnFilter : true ], c )
        assertSameLists( names, [ '3.txt', '7.txt', '22.txt' ] )

        names   = []
        testDir.recurse([ filterType   : FileType.DIRECTORIES,
                          filter       : { it.name == '5' },
                          stopOnFilter : true ], c )
        assert names   == []

        names   = []
        testDir.recurse([ filterType   : FileType.DIRECTORIES,
                          filter       : { File dir -> [ '5', '6' ].any{ it == dir.name }},
                          stopOnFilter : true ], c )
        assert names == [ '7.txt' ]

        names   = []
        testDir.recurse([ filterType   : FileType.DIRECTORIES,
                          filter       : { File dir -> [ '5', '6' ].any{ it == dir.name }},
                          stopOnFilter : false ], c )
        assertSameLists( names, [ '3.txt', '7.txt', '22.txt' ] )

        names   = []
        testDir.recurse([ type         : FileType.DIRECTORIES,
                          filterType   : FileType.DIRECTORIES,
                          filter       : { File dir -> dir.listFiles()[ 0 ].name == '22.txt' },
                          stopOnFilter : false ], c )
        assert names == [ '8' ]

        names   = []
        testDir.recurse([ type         : FileType.DIRECTORIES,
                          filterType   : FileType.DIRECTORIES,
                          filter       : { File dir -> [ '8', '22.txt' ].any{ it == dir.listFiles()[ 0 ].name }},
                          stopOnFilter : true ], c )
        assertSameLists( names, [ '7', '8' ])
    }


    @Test
    void recurseShouldFindPOMs()
    {
        def testDir = testDir( 'poms' )
        def write   = { String path ->
            def file = new File( testDir, path )
            fileBean.mkdirs( file.parentFile )
            file.write( 'aaa' )
        }

        write( 'build/something/pom.xml' )
        write( 'pom.xml' )
        write( 'moduleA/pom.xml' )
        write( 'moduleA/build/aa/pom.xml' )
        write( 'moduleA/dist/bb/pom.xml' )
        write( 'moduleB/pom.xml' )
        write( 'moduleB/dist/pom.xml' )
        write( 'moduleB/build/pom.xml' )

        List<String> poms =  []
        testDir.recurse([ type         : FileType.FILES,
                          filterType   : FileType.DIRECTORIES,
                          filter       : { File dir -> ( ! ( [ 'build', 'dist' ].any{ it == dir.name } )) },
                          stopOnFilter : true ]){ poms << it.canonicalPath.replace( '\\', '/' ) }

        assert poms.size() == 3
        assert poms[ 0 ].endsWith( '/poms/moduleA/pom.xml' )
        assert poms[ 1 ].endsWith( '/poms/moduleB/pom.xml' )
        assert poms[ 2 ].endsWith( '/poms/pom.xml' )
    }


    @Test
    void recurseShouldFindSvn()
    {
        def testDir = testDir( 'svn' )
        def write   = { String path ->
            def file = new File( testDir, path )
            fileBean.mkdirs( file.parentFile )
            file.write( 'aaazzzxxx' )
        }

        write( 'project/.svn/1.txt' )
        write( 'project/moduleA/.svn/1.txt' )
        write( 'project/moduleA/1.txt' )
        write( 'project/moduleA/src/.svn/1.txt' )
        write( 'project/moduleA/src/main/.svn/1.txt' )
        write( 'project/moduleA/src/main/resources/.svn/1.txt' )
        write( 'project/moduleA/src/main/resources/1.txt' )
        write( 'project/moduleA/src/test/.svn/1.txt' )
        write( 'project/moduleA/src/test/resources/.svn/.s1.txt' )
        write( 'project/moduleA/src/test/resources/1.txt' )
        write( 'project/moduleB/.svn/1.txt' )
        write( 'project/moduleB/1.txt' )

        write( 'project2/.svn/1.txt' )
        write( 'project2/moduleA/.svn/1.txt' )
        write( 'project2/moduleA/1.txt' )
        write( 'project2/moduleA/src/.svn/1.txt' )
        write( 'project2/moduleA/src/main/.svn/1.txt' )
        write( 'project2/moduleA/src/main/resources/.svn/1.txt' )
        write( 'project2/moduleA/src/main/resources/1.txt' )
        write( 'project2/moduleA/src/test/.svn/1.txt' )
        write( 'project2/moduleA/src/test/resources/.svn/.s1.txt' )
        write( 'project2/moduleA/src/test/resources/1.txt' )
        write( 'project2/moduleB/.svn/1.txt' )
        write( 'project2/moduleB/1.txt' )

        write( 'project3/project4/.svn/1.txt' )
        write( 'project3/project4/src/.svn/1.txt' )
        write( 'project3/project4/src/main/.svn/1.txt' )
        write( 'project3/project4/src/main/groovy/.svn/1.txt' )
        write( 'project3/project4/src/main/groovy/1.groovy' )


        def hasSvn = { File[] dirs -> dirs.every { File dir -> dir.listFiles().any{ File f -> ( f.name == '.svn' ) }}}
        List<String> projectRoots = []
        def          counter      = 0
        testDir.recurse([ type : FileType.DIRECTORIES, stopOnFalse: true ]) {
            File dir ->

            counter++
            if ( hasSvn( dir ))
            {
                projectRoots << dir.canonicalPath.replace( '\\', '/' )
                false
            }
            else
            {
                true
            }
        }

        assert projectRoots.size() == 3
        assert counter             == 4
        assert projectRoots[ 0 ].endsWith( '/svn/project' )
        assert projectRoots[ 1 ].endsWith( '/svn/project2' )
        assert projectRoots[ 2 ].endsWith( '/svn/project3/project4' )


        projectRoots = []
        counter      = 0

        testDir.recurse([ filterType   : FileType.DIRECTORIES,
                          type         : FileType.DIRECTORIES,
                          stopOnFilter : true,
                          filter       : { File dir -> (( dir.name != '.svn' ) && ( ! hasSvn( dir, dir.parentFile ))) } ]) {
            counter++
            if ( hasSvn( it )) { projectRoots << it.canonicalPath.replace( '\\', '/' ) }
        }

        assert projectRoots.size() == 3
        assert counter             == 4
        assert projectRoots[ 0 ].endsWith( '/svn/project' )
        assert projectRoots[ 1 ].endsWith( '/svn/project2' )
        assert projectRoots[ 2 ].endsWith( '/svn/project3/project4' )
    }
}
