package com.goldin.gcommons.beans

import com.goldin.gcommons.BaseTest
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
    private static final Map TEST_ARCHIVES  = SMALL_ARCHIVES +
                                              ( System.properties[ 'slowTests' ] ? LARGE_ARCHIVES : [:] )

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

        def errorMessage = shouldFailWithCause( IllegalArgumentException.class )
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
}
