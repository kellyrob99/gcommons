package com.goldin.gcommons.beans

import com.goldin.gcommons.BaseTest
import org.apache.tools.zip.ZipFile
import org.junit.Test

/**
 * {@link com.goldin.gcommons.beans.FileBean} tests
 */
class FileBeanTest extends BaseTest
{

    private static File writeFile( File f, String content = null )
    {
        assert ( f.getParentFile().isDirectory() || f.getParentFile().mkdirs())

        if ( content )
        {
            f.write( content )
        }
        else
        {
            f.write( f.canonicalPath )
            f.append( System.currentTimeMillis())
            f.append( new Date())
        }

        assert f.exists() && f.isFile()
        f
    }


    @Test
    void shouldDeleteFiles()
    {
        def file = fileBean.tempFile()

        assert file.exists() && file.isFile()

        fileBean.delete( file )

        assert ! file.exists()
        assert ! file.isFile()

        def dir = fileBean.tempDirectory()
        fileBean.delete( writeFile( new File( dir, '1.txt' )))
        fileBean.delete( writeFile( new File( dir, '2.xml' )))
        fileBean.delete( writeFile( new File( dir, '3.ppt' )))

        assert ! dir.list()
        assert ! dir.listFiles()
        assert ! file.exists()
        assert ! file.isFile()

        fileBean.delete( dir )
    }


    @Test
    void shouldMkdir()
    {
        def f = { String name -> new File( constantsBean.USER_HOME_FILE, name ) }

        fileBean.mkdirs( f( 'aa' ), f( 'aa/bb' ), f( 'aa/bb/dd' ), f( 'ee/bb/dd' ), f( 'ff/bb/dd/kk' ))
        verifyBean.directory( f( 'aa' ), f( 'aa/bb' ), f( 'aa/bb/dd' ),
                              f( 'ee' ), f( 'ee/bb' ), f( 'ee/bb/dd' ),
                              f( 'ff' ), f( 'ff/bb' ), f( 'ff/bb/dd' ),f( 'ff/bb/dd/kk' ))

        shouldFailAssert { verifyBean.directory( f( 'aa' ), f( 'aa/bb' ), f( 'aa/bb/dd1' )) }

        f( 'aa/1.txt' ).write( System.currentTimeMillis() as String )
        f( 'aa/bb/2.txt' ).write( System.currentTimeMillis() as String )
        f( 'aa/bb/dd/3.txt' ).write( System.currentTimeMillis() as String )

        f( 'ee/1.txt' ).write( System.currentTimeMillis() as String )
        f( 'ee/bb/2.txt' ).write( System.currentTimeMillis() as String )
        f( 'ee/bb/dd/3.txt' ).write( System.currentTimeMillis() as String )

        f( 'ff/1.txt' ).write( System.currentTimeMillis() as String )
        f( 'ff/bb/2.txt' ).write( System.currentTimeMillis() as String )
        f( 'ff/bb/dd/3.txt' ).write( System.currentTimeMillis() as String )
        f( 'ff/bb/dd/kk/4.txt' ).write( System.currentTimeMillis() as String )

        fileBean.delete( f( 'aa' ), f( 'ee' ), f( 'ff' ))

        shouldFailAssert { verifyBean.directory( f( 'aa' )) }
        shouldFailAssert { verifyBean.directory( f( 'ee' )) }
        shouldFailAssert { verifyBean.directory( f( 'ff' )) }
    }


    @Test
    void shouldDeleteDirectories()
    {
        def dir = fileBean.tempDirectory()

        assert dir.exists() && dir.isDirectory()

        writeFile( new File( dir, '1.txt' ))
        writeFile( new File( dir, '2.xml' ))
        writeFile( new File( dir, '3.ppt' ))
        writeFile( new File( dir, 'a/b/1.txt' ))
        writeFile( new File( dir, 'c/d/2.xml' ))
        writeFile( new File( dir, 'e/f/g/h/3.ppt' ))
        writeFile( new File( dir, '11.txt' ))
        writeFile( new File( dir, '22.xml' ))
        writeFile( new File( dir, '33.ppt' ))
        writeFile( new File( dir, 'aw/bq/1j.txt' ))
        writeFile( new File( dir, 'cy/do/2p.xml' ))
        writeFile( new File( dir, 'easdf/fdsd/gwqeq/hujy/3weqw.ppt.eqeq' ))

        fileBean.delete( dir )
        assert ! dir.exists()
        assert ! dir.isFile()
    }


    @Test
    void shouldCalculateChecksum()
    {
        def file = new File( 'src/test/resources/apache-maven-3.0.1.zip' )
        assert fileBean.checksum( file )        == fileBean.checksum( file, 'SHA-1' )
        assert fileBean.checksum( file )        == '7db54443784f547a36a7adb293bfeca2d2c9d15c'
        assert fileBean.checksum( file, 'MD5' ) == '3aeeb8b545ae1b6aa8b2015dce24eec7'

        def dir = fileBean.tempDirectory()
        file    = new File( dir, '1.txt' )

        shouldFailAssert { fileBean.checksum( dir  ) }
        shouldFailAssert { fileBean.checksum( null ) }

        writeFile( file, '7db54443784f547a36a7adb293bfeca2d2c9d15c\r\n' )
        assert fileBean.checksum( file, 'MD5' ) == '04ce83c072936118922107babdf6d21a'
        assert fileBean.checksum( file )        == 'fcd551a840d37d3c885db298e893ec77468a81cd'
        assert fileBean.checksum( file, 'MD5' ) == fileBean.checksum( file, 'MD5' )
        assert fileBean.checksum( file, 'MD5' ) != fileBean.checksum( file )

        fileBean.delete( dir )
    }


    @Test
    void testFiles()
    {
        def    allFiles = fileBean.files( constantsBean.USER_DIR_FILE )
        assert allFiles
        assert allFiles.each{ verifyBean.exists( it ) }
        assert allFiles == fileBean.files( constantsBean.USER_DIR_FILE, null, null, true, false )
        assert allFiles != fileBean.files( constantsBean.USER_DIR_FILE, null, null, true, true  )

        def buildDir   = new File( constantsBean.USER_DIR_FILE, 'build' )
        def classFiles = fileBean.files( buildDir, ['**/*.class'] )
        def sources    = fileBean.files( constantsBean.USER_DIR_FILE, ['**/*.groovy'] )
        assert classFiles.every{ it.name.endsWith( '.class'  ) }
        assert sources.every{    it.name.endsWith( '.groovy' ) }
        verifyBean.file( classFiles as File[] )
        verifyBean.file( sources    as File[] )
        assert classFiles.size() > sources.size()

        shouldFailAssert { fileBean.files( buildDir, ['**/*.ppt'] )}
        assert fileBean.files( buildDir, ['**/*.ppt'], null, true, false, false ).isEmpty()

        allFiles = fileBean.files( constantsBean.USER_DIR_FILE, ['**/*.groovy','**/*.class'], ['**/*Test*.*'] )
        assert ! allFiles.any { it.name.contains( 'Test' ) }
        assert allFiles.every { it.name.endsWith( '.groovy' ) || it.name.endsWith( '.class' ) }

        allFiles.findAll{ it.name.endsWith( '.groovy') }.each {
            File groovyFile ->
            assert allFiles.findAll { it.name == groovyFile.name.replace( '.groovy', '.class' ) }.size() < 3 //  1 or 2, Compiled by Gradle or IDEA
        }
    }


    /**
     * Mapping of test archives and their sizes in bytes when unpacked
     */
    private static final Map SMALL_ARCHIVES = [ 'apache-maven-3.0.1' : 3344327  ]
    private static final Map LARGE_ARCHIVES = [ 'gradle-0.9'         : 27848286 ]
    private static final Map TEST_ARCHIVES  = SMALL_ARCHIVES + ( System.properties[ 'slowTests' ] ? LARGE_ARCHIVES : [:] )

    @Test
    void shouldPack()
    {
        def resourcesDir = new File( 'src/test/resources' )

        for ( archiveName in TEST_ARCHIVES.keySet())
        {
            def unpackDir    = testDir( 'unpack' )
            def packDir      = testDir( 'pack' )
            def jarDir       = testDir( 'jar'   )
            def tarDir       = testDir( 'tar'   )
            def tgzDir       = testDir( 'tgz'   )
            def zipDir       = testDir( 'zip'   )
            def tarGzDir     = testDir( 'targz' )

            fileBean.unpack( new File( resourcesDir, "${archiveName}.jar" ), unpackDir )

            fileBean.pack( unpackDir, new File( packDir, "${archiveName}.jar"    ))
            fileBean.pack( unpackDir, new File( packDir, "${archiveName}.tar"    ))
            fileBean.pack( unpackDir, new File( packDir, "${archiveName}.tgz"    ))
            fileBean.pack( unpackDir, new File( packDir, "${archiveName}.zip"    ))
            fileBean.pack( unpackDir, new File( packDir, "${archiveName}.tar.gz" ))

            fileBean.unpack( new File( packDir, "${archiveName}.jar" ),    jarDir   )
            fileBean.unpack( new File( packDir, "${archiveName}.tar" ),    tarDir   )
            fileBean.unpack( new File( packDir, "${archiveName}.tgz" ),    tgzDir   )
            fileBean.unpack( new File( packDir, "${archiveName}.zip" ),    zipDir   )
            fileBean.unpack( new File( packDir, "${archiveName}.tar.gz" ), tarGzDir )

            verifyBean.equal( unpackDir, jarDir )
            verifyBean.equal( jarDir,    tarDir )
            verifyBean.equal( tarDir,    tgzDir )
            verifyBean.equal( tgzDir,    zipDir )
            verifyBean.equal( zipDir,    tarGzDir )
            verifyBean.equal( tarGzDir,  unpackDir )

            assert fileBean.directorySize( unpackDir ) == TEST_ARCHIVES[ archiveName ]
            assert fileBean.directorySize( jarDir )    == TEST_ARCHIVES[ archiveName ]
            assert fileBean.directorySize( tarDir )    == TEST_ARCHIVES[ archiveName ]
            assert fileBean.directorySize( tgzDir )    == TEST_ARCHIVES[ archiveName ]
            assert fileBean.directorySize( zipDir )    == TEST_ARCHIVES[ archiveName ]
            assert fileBean.directorySize( tarGzDir )  == TEST_ARCHIVES[ archiveName ]
            assert fileBean.directorySize( unpackDir, jarDir, tarDir, tgzDir, zipDir, tarGzDir ) == TEST_ARCHIVES[ archiveName ] * 6
        }
    }


    @Test
    void shouldUnpack()
    {
        def resourcesDir = new File( 'src/test/resources' )
        def imageDirZip  = testDir( 'image-3-abc-zip'  )
        def imageDirSima = testDir( 'image-3-abc-sima' )
        fileBean.unpack( new File( resourcesDir, 'image-3-abc.zip'  ),  imageDirZip )
        fileBean.unpack( new File( resourcesDir, 'image-3-abc.sima' ), imageDirSima )
        assert new File( imageDirZip, '1.png' ).size() == 187933
        verifyBean.equal( imageDirZip, imageDirSima )

        def errorMessage = shouldFailWithCause( IllegalArgumentException )
        {
            fileBean.unpack( new File( resourcesDir, 'image-3-abc.sima1' ), imageDirSima )
        }
        assert errorMessage == '"sima1" (no archive driver installed for these suffixes)'

        for ( archiveName in TEST_ARCHIVES.keySet())
        {
            def jarDir       = testDir( 'jar'   )
            def tarDir       = testDir( 'tar'   )
            def tgzDir       = testDir( 'tgz'   )
            def zipDir       = testDir( 'zip'   )
            def tarGzDir     = testDir( 'targz' )

            fileBean.unpack( new File( resourcesDir, "${archiveName}.jar" ),    jarDir   )
            fileBean.unpack( new File( resourcesDir, "${archiveName}.tar" ),    tarDir   )
            fileBean.unpack( new File( resourcesDir, "${archiveName}.tgz" ),    tgzDir   )
            fileBean.unpack( new File( resourcesDir, "${archiveName}.zip" ),    zipDir   )
            fileBean.unpack( new File( resourcesDir, "${archiveName}.tar.gz" ), tarGzDir )

            verifyBean.equal( jarDir,   tarDir )
            verifyBean.equal( tarDir,   tgzDir )
            verifyBean.equal( tgzDir,   zipDir )
            verifyBean.equal( zipDir,   tarGzDir )
            verifyBean.equal( tarGzDir, jarDir )

            assert fileBean.directorySize( jarDir )   == TEST_ARCHIVES[ archiveName ]
            assert fileBean.directorySize( tarDir )   == TEST_ARCHIVES[ archiveName ]
            assert fileBean.directorySize( tgzDir )   == TEST_ARCHIVES[ archiveName ]
            assert fileBean.directorySize( zipDir )   == TEST_ARCHIVES[ archiveName ]
            assert fileBean.directorySize( tarGzDir ) == TEST_ARCHIVES[ archiveName ]
            assert fileBean.directorySize( jarDir, tarDir, tgzDir, zipDir, tarGzDir ) == TEST_ARCHIVES[ archiveName ] * 5
        }
    }


    @Test
    void shouldUnpackZipEntries()
    {
        def resourcesDir = new File( 'src/test/resources' )
        def mavenZip     = new File( resourcesDir, 'apache-maven-3.0.1.zip' )
        def mavenJar     = new File( resourcesDir, 'apache-maven-3.0.1.jar' )
        def mavenTar     = new File( resourcesDir, 'apache-maven-3.0.1.tar' )
        def mavenTgz     = new File( resourcesDir, 'apache-maven-3.0.1.tgz' )
        def mavenTarGz   = new File( resourcesDir, 'apache-maven-3.0.1.tar.gz' )
        def plexusJar    = new File( resourcesDir, 'plexus-component-annotations-1.5.5.jar' )
        def testArchives = TEST_ARCHIVES.keySet().collect { it + '.zip' }
        def mavenDir1    = testDir( 'apache-maven-1'  )
        def mavenDir2    = testDir( 'apache-maven-2'  )
        def mavenDir3    = testDir( 'apache-maven-3'  )
        def mavenDir4    = testDir( 'apache-maven-4'  )
        def mavenDir5    = testDir( 'apache-maven-5'  )
        def mavenDir6    = testDir( 'apache-maven-6'  )
        def mavenDir7    = testDir( 'apache-maven-7'  )
        def mavenDir8    = testDir( 'apache-maven-8'  )
        def mavenDir9    = testDir( 'apache-maven-9'  )

        def entries      = [ 'apache-maven-3.0.1\\lib\\aether-api-1.8.jar',
                             'apache-maven-3.0.1/lib/commons-cli-1.2.jar',
                             '/apache-maven-3.0.1\\bin\\m2.conf',
                             '/apache-maven-3.0.1/bin/mvn',
                             'apache-maven-3.0.1\\lib\\nekohtml-1.9.6.2.jar',
                             'apache-maven-3.0.1/NOTICE.txt',
                             '/apache-maven-3.0.1/NOTICE.txt',
                             'apache-maven-3.0.1\\NOTICE.txt' ]

        def entries2     = [ 'org/codehaus/plexus/component/annotations/Component.class',
                             'org/codehaus/plexus/component/annotations/Configuration.class',
                             'META-INF/MANIFEST.MF',
                             'META-INF/maven/org.codehaus.plexus/plexus-component-annotations/pom.properties',
                             'META-INF/maven/org.codehaus.plexus/plexus-component-annotations/pom.xml',
                             'org/codehaus/plexus/component/annotations/Requirement.class' ]

        fileBean.unpackZipEntries( mavenZip,  mavenDir1, entries )
        fileBean.unpackZipEntries( mavenZip,  mavenDir2, entries, false )
        fileBean.unpackZipEntries( mavenZip,  mavenDir3, entries, true )
        fileBean.unpackZipEntries( mavenJar,  mavenDir4, entries )
        fileBean.unpackZipEntries( mavenJar,  mavenDir5, entries, true )
        fileBean.unpack( plexusJar, mavenDir6 )
        fileBean.unpackZipEntries( plexusJar, mavenDir7, entries2, true )

        testArchives.each {
            def testArchiveFile = new File( resourcesDir, it )
            fileBean.unpack( testArchiveFile,  mavenDir8 )
            fileBean.unpackZipEntries( testArchiveFile,  mavenDir9, new ZipFile( testArchiveFile ).entries*.name, true )
        }

        assert mavenDir1.list().size() == 6
        assert mavenDir2.list().size() == 6
        assert mavenDir4.list().size() == 6

        assert mavenDir3.list().size() == 1
        assert mavenDir5.list().size() == 1

        assert mavenDir6.list().size() == 2
        assert mavenDir7.list().size() == 2

        verifyBean.equal( mavenDir1, mavenDir2 )
        verifyBean.equal( mavenDir2, mavenDir4 )
        verifyBean.equal( mavenDir4, mavenDir1 )
        verifyBean.equal( mavenDir3, mavenDir5 )
        verifyBean.equal( mavenDir6, mavenDir7 )
        verifyBean.equal( mavenDir6, mavenDir7 )
        verifyBean.equal( mavenDir8, mavenDir9 )

        assert fileBean.directorySize( mavenDir1 ) == 235902
        assert fileBean.directorySize( mavenDir2 ) == 235902
        assert fileBean.directorySize( mavenDir3 ) == 235902
        assert fileBean.directorySize( mavenDir4 ) == 235902
        assert fileBean.directorySize( mavenDir5 ) == 235902
        assert fileBean.directorySize( mavenDir6 ) == 3420
        assert fileBean.directorySize( mavenDir7 ) == 3420
        assert fileBean.directorySize( mavenDir8 ) == TEST_ARCHIVES.values().sum()
        assert fileBean.directorySize( mavenDir9 ) == TEST_ARCHIVES.values().sum()

        verifyBean.file( new File( mavenDir1, 'aether-api-1.8.jar' ),
                         new File( mavenDir1, 'commons-cli-1.2.jar' ),
                         new File( mavenDir1, 'm2.conf' ),
                         new File( mavenDir1, 'mvn' ),
                         new File( mavenDir1, 'nekohtml-1.9.6.2.jar' ),
                         new File( mavenDir1, 'NOTICE.txt' ))

        verifyBean.file( new File( mavenDir2, 'aether-api-1.8.jar' ),
                         new File( mavenDir2, 'commons-cli-1.2.jar' ),
                         new File( mavenDir2, 'm2.conf' ),
                         new File( mavenDir2, 'mvn' ),
                         new File( mavenDir2, 'nekohtml-1.9.6.2.jar' ),
                         new File( mavenDir2, 'NOTICE.txt' ))

        verifyBean.file( new File( mavenDir3, 'apache-maven-3.0.1/lib/aether-api-1.8.jar' ),
                         new File( mavenDir3, 'apache-maven-3.0.1/lib/commons-cli-1.2.jar' ),
                         new File( mavenDir3, 'apache-maven-3.0.1/bin/m2.conf' ),
                         new File( mavenDir3, 'apache-maven-3.0.1/bin/mvn' ),
                         new File( mavenDir3, 'apache-maven-3.0.1/lib/nekohtml-1.9.6.2.jar' ),
                         new File( mavenDir3, 'apache-maven-3.0.1/NOTICE.txt' ))

        // Entries that don't exist
        shouldFailWithCause( AssertionError ) { fileBean.unpackZipEntries( plexusJar,  mavenDir7, entries, true )}
        shouldFailWithCause( AssertionError ) { fileBean.unpackZipEntries( plexusJar,  mavenDir7, [ 'org/codehaus/plexus/component'  ], true )}
        shouldFailWithCause( AssertionError ) { fileBean.unpackZipEntries( plexusJar,  mavenDir7, [ '/org/codehaus/plexus/component' ], true )}
        shouldFailWithCause( AssertionError ) { fileBean.unpackZipEntries( plexusJar,  mavenDir7, [ 'META-INF' ], true )}
        shouldFailWithCause( AssertionError ) { fileBean.unpackZipEntries( plexusJar,  mavenDir7, [ '/META-INF' ], true )}
        shouldFailWithCause( AssertionError ) { fileBean.unpackZipEntries( mavenZip,   mavenDir1, [ 'doesnt-exist/entry' ] )}
        shouldFailWithCause( AssertionError ) { fileBean.unpackZipEntries( mavenZip,   mavenDir1, [ '/doesnt-exist/entry' ] )}

        // Not Zip files
        shouldFailAssert { fileBean.unpackZipEntries( mavenTar, mavenDir1, entries )}
        shouldFailAssert { fileBean.unpackZipEntries( mavenTgz,   mavenDir1, entries )}
        shouldFailAssert { fileBean.unpackZipEntries( mavenTarGz, mavenDir1, entries )}

        // Empty list of entries
        shouldFailAssert { fileBean.unpackZipEntries( mavenZip,   mavenDir1, [ null ] )}
        shouldFailAssert { fileBean.unpackZipEntries( mavenZip,   mavenDir1, [ ' ', '',  '  ', null ] )}
        shouldFailAssert { fileBean.unpackZipEntries( mavenZip,   mavenDir1, [ '' ] )}

        // File that doesn't exist
        shouldFailAssert { fileBean.unpackZipEntries( new File( resourcesDir, 'doesnt-exist.file' ), mavenDir1, entries )}

        // Should execute normally and not fail
        shouldFailAssert { shouldFailWith( RuntimeException ) { fileBean.unpackZipEntries( plexusJar,  mavenDir7, [ '/org/codehaus/plexus/component/'], true )}}
        shouldFailAssert { shouldFailWith( RuntimeException ) { fileBean.unpackZipEntries( plexusJar,  mavenDir7, [ 'org/codehaus/plexus/component/' ], true )}}
        shouldFailAssert { shouldFailWith( RuntimeException ) { fileBean.unpackZipEntries( plexusJar,  mavenDir7, [ '/META-INF/' ], true )}}
        shouldFailAssert { shouldFailWith( RuntimeException ) { fileBean.unpackZipEntries( plexusJar,  mavenDir7, [ 'META-INF/' ], true )}}
        shouldFailAssert { shouldFailWith( RuntimeException ) { fileBean.unpackZipEntries( plexusJar,  mavenDir7, entries2, true ) }}
    }


    @Test
    void shouldUnpackZipEntriesWithPattern()
    {
        def resourcesDir = new File( 'src/test/resources' )
        def mavenZip     = new File( resourcesDir, 'apache-maven-3.0.1.zip' )
        def mavenJar     = new File( resourcesDir, 'apache-maven-3.0.1.jar' )
        def mavenDir1    = testDir( 'apache-maven-1' )
        def mavenDir2    = testDir( 'apache-maven-2' )
        def mavenDir3    = testDir( 'apache-maven-3' )
        def mavenDir4    = testDir( 'apache-maven-4' )
        def mavenDir5    = testDir( 'apache-maven-5' )
        def mavenDir6    = testDir( 'apache-maven-6' )
        def mavenDir7    = testDir( 'apache-maven-7' )
        def mavenDir8    = testDir( 'apache-maven-8' )

        fileBean.unpackZipEntries( mavenZip, mavenDir1, [ 'apache-maven-3.0.1/**/*.jar' ], true  )
        fileBean.unpackZipEntries( mavenJar, mavenDir2, [ '**/*.jar' ], true  )
        fileBean.unpackZipEntries( mavenZip, mavenDir3, [ 'apache-maven-3.0.1/**/*.jar' ], false )
        fileBean.unpackZipEntries( mavenJar, mavenDir4, [ '**/*.jar' ], false )
        fileBean.unpackZipEntries( mavenZip, mavenDir5, [ '**/*.xml', '**/conf/**' ], false )
        fileBean.unpackZipEntries( mavenJar, mavenDir6, [ 'apache-maven-3.0.1/conf/settings.xml', '**/*.xml' ], false )
        fileBean.unpack( mavenZip, mavenDir7 )
        fileBean.unpackZipEntries( mavenJar, mavenDir8, [ '**' ], true )

        verifyBean.equal( mavenDir1, mavenDir2 )
        verifyBean.equal( mavenDir3, mavenDir4 )
        verifyBean.equal( mavenDir5, mavenDir6 )
        verifyBean.equal( mavenDir7, mavenDir8 )

        assert fileBean.directorySize( mavenDir1 ) == 3301021
        assert fileBean.directorySize( mavenDir2 ) == 3301021
        assert fileBean.directorySize( mavenDir3 ) == 3301021
        assert fileBean.directorySize( mavenDir4 ) == 3301021
        assert fileBean.directorySize( mavenDir5 ) == 1704
        assert fileBean.directorySize( mavenDir6 ) == 1704
        assert fileBean.directorySize( mavenDir7 ) == 3344327
        assert fileBean.directorySize( mavenDir8 ) == 3344327

        assert mavenDir1.list().size() == 1
        assert mavenDir2.list().size() == 1
        assert mavenDir3.list().size() == 32
        assert mavenDir4.list().size() == 32
        assert mavenDir5.list().size() == 1
        assert mavenDir6.list().size() == 1
        assert mavenDir7.list().size() == 1
        assert mavenDir8.list().size() == 1


        shouldFailWithCause( AssertionError ) {
            fileBean.unpackZipEntries( mavenZip, mavenDir8, [ '**/*.no-such-file' ], true ) }
        shouldFailWithCause( AssertionError ) {
            fileBean.unpackZipEntries( mavenZip, mavenDir8, [ '**/*.jar', '**/*.ppt' ], true ) }
        shouldFailWithCause( AssertionError ) {
            fileBean.unpackZipEntries( mavenZip, mavenDir8, [ '**/*.exe', 'apache-maven-3.0.1/conf/**', ], true ) }
        shouldFailWithCause( AssertionError ) {
            fileBean.unpackZipEntries( mavenZip, mavenDir8, [ '**/*.xml', 'apache-maven-3.3.1/**', ], true ) }
    }

}
