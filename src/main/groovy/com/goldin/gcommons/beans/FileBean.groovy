package com.goldin.gcommons.beans

import com.goldin.gcommons.util.SingleFileArchiveDetector
import de.schlichtherle.io.GlobalArchiveDriverRegistry
import de.schlichtherle.io.archive.spi.ArchiveDriver
import de.schlichtherle.io.archive.tar.TarDriver
import de.schlichtherle.io.archive.zip.ZipDriver
import groovy.io.FileType
import java.security.MessageDigest
import java.util.zip.ZipEntry
import org.apache.tools.ant.DirectoryScanner
import org.apache.tools.zip.ZipFile
import org.springframework.beans.factory.InitializingBean

 /**
 * File-related helper utilities.
 */
class FileBean extends BaseBean implements InitializingBean
{
    /**
     * Set by Spring
     */
    IOBean io

    /**
     * Archive extensions supported by ZIP and TAR drivers
     */
    Set<String> ZIP_EXTENSIONS
    Set<String> TAR_EXTENSIONS

    @Override
    void afterPropertiesSet ()
    {
        def c =
        {
            def requiredDriverClass ->

            (( Map<String,?> ) GlobalArchiveDriverRegistry.INSTANCE ).findAll
            {
                def extension, driver -> // String => Driver instance or String
                def driverClass = (( driver instanceof ArchiveDriver ) ? driver.class            :
                                   ( driver instanceof String        ) ? Class.forName( driver ) :
                                                                        null )
                driverClass && requiredDriverClass.isAssignableFrom( driverClass )
            }.
            keySet()*.toLowerCase()
        }

        ZIP_EXTENSIONS = c( ZipDriver )
        TAR_EXTENSIONS = c( TarDriver )
    }


    /**
     * Creates a temp file.
     * @return temp file created.
     */
    File tempFile()
    {
        File.createTempFile( GeneralBean.class.name, '' )
    }


    /**
     * Creates a temp directory.
     * @return temp directory created.
     */
    File tempDirectory()
    {
        def file      = tempFile()
        def directory = new File( file.absolutePath )

        delete( file )
        mkdirs( directory )
    }



    /**
     * Deletes files or directories specified. Directories are deleted recursively.
     * @param files files or directories to delete
     * @return first object specified
     */
    File delete ( File ... files )
    {
        for ( f in files )
        {
            if ( f.exists())
            {
                if ( f.isDirectory() && f.listFiles()) { delete( f.listFiles()) }
                assert (( ! f.listFiles()) && f.delete()), "Failed to delete [$f.canonicalPath]"
            }

            assert ( ! f.exists())
        }

        first( files )
    }


    /**
     * {@link File#mkdirs()} wrapper for directories specified.
     *
     * @param directories directories to create
     * @return first directory specified
     */
    File mkdirs ( File ... directories )
    {
        for ( directory in directories )
        {
            def directoryPath = directory.canonicalPath
            assert ( ! directory.isFile()), \
                   "Failed to create directory [$directoryPath] - it is an existing file"
            assert (( directory.isDirectory() || directory.mkdirs()) && ( directory.isDirectory())), \
                   "Failed to create directory [$directoryPath]"
        }

        first( directories )
    }


    /**
     * Generates a checksum for the file specified.
     *
     * @param file file to generate a checksum for
     * @param algorithm checksum algorithm, supported by Ant's {@code <checksum>} task.
     * @return file's checksum
     */
    String checksum ( File file, String algorithm = 'SHA-1' )
    {
        verify.file( file )

        StringBuilder checksum = new StringBuilder()
        MessageDigest md       = MessageDigest.getInstance( verify.notNullOrEmpty( algorithm ))
        file.eachByte( 10 * 1024 ) { byte[] buffer, int n -> md.update( buffer, 0, n ) }

        for ( byte b in md.digest())
        {
            String hex = Integer.toHexString(( 0xFF & b ) as int )
            checksum.append( "${( hex.length() < 2 ) ? '0' : '' }$hex" )
        }

        verify.notNullOrEmpty( checksum.toString())
    }


    /**
     * Retrieves files (and directories, if required) given base directory and inclusion/exclusion patterns.
     * Symbolic links are not followed.
     *
     * @param baseDirectory      files base directory
     * @param includes           patterns to use for including files, all files are included if null
     * @param excludes           patterns to use for excluding files, no files are excluded if null
     * @param caseSensitive      whether or not include and exclude patterns are matched in a case sensitive way
     * @param includeDirectories whether directories included should be returned as well
     * @param failIfNotFound     whether execution should fail if no files were found
     *
     * @return files under base directory specified passing an inclusion/exclusion patterns
     */
    List<File> files ( File         baseDirectory,
                       List<String> includes           = null,
                       List<String> excludes           = null,
                       boolean      caseSensitive      = true,
                       boolean      includeDirectories = false,
                       boolean      failIfNotFound     = true )
    {
        verify.directory( baseDirectory )

        def ds    = new DirectoryScanner()
        def files = []

        ds.setBasedir( baseDirectory )
        ds.setIncludes( includes as String[] )
        ds.setExcludes( excludes as String[] )
        ds.setCaseSensitive( caseSensitive )
        ds.setErrorOnMissingDir( true )
        ds.setFollowSymlinks( false )
        ds.scan()

        for ( String filePath in ds.getIncludedFiles())
        {
            files << verify.file( new File( baseDirectory, filePath ))
        }

        if ( includeDirectories )
        {
            for ( String directoryPath in ds.getIncludedDirectories())
            {
                if ( directoryPath )
                {
                    files << verify.directory( new File ( baseDirectory, directoryPath ))
                }
            }
        }

        assert ( files || ( ! failIfNotFound )), \
               "No files are included by parent dir [$baseDirectory] and include/exclude patterns ${ includes ?: [] }/${ excludes ?: [] }"

        files
    }


    /**
     * Retrieves a "compression" value for Ant's tar/untar tasks.
     *
     * @param archiveExtension archive extension
     * @return archive compression according to
     *         http://evgeny-goldin.org/javadoc/ant/CoreTasks/tar.html
     *         http://evgeny-goldin.org/javadoc/ant/CoreTasks/untar.html
     */
    private String tarCompression( String archiveExtension )
    {
        switch ( verify.notNullOrEmpty( archiveExtension ))
        {
            case 'tar'     : return 'none'

            case 'tgz'     :
            case 'tar.gz'  : return 'gzip'

            case 'tbz2'    :
            case 'tar.bz2' : return 'bzip2'
        }

        throw new RuntimeException( "Unknown tar extension [$archiveExtension]" )
    }


    /**
     * Archives directory to archive specified. Empty directories are not archived!
     *
     * @param sourceDirectory    directory to archive
     * @param destinationArchive archive to pack the directory to
     * @param includes           patterns to use for including files, all files are included if null
     * @param excludes           patterns to use for excluding files, no files are excluded if null
     * @param caseSensitive      whether or not include and exclude patterns are matched in a case sensitive way
     * @param failIfNotFound     whether execution should fail if no files were found
     *
     * @return archive packed
     */
    File pack ( File         sourceDirectory,
                File         destinationArchive,
                List<String> includes       = null,
                List<String> excludes       = null,
                boolean      caseSensitive  = true,
                boolean      failIfNotFound = true )
    {
        verify.directory( sourceDirectory )
        verify.notNull( destinationArchive )

        String sourceDirectoryPath    = sourceDirectory.canonicalPath
        String destinationArchivePath = destinationArchive.canonicalPath

        try
        {
            if ( destinationArchive.exists()){ delete( destinationArchive ) }

            assert ! destinationArchive.exists()
            File archiveDir = destinationArchive.getParentFile()
            assert ( archiveDir != null ), "Destination archive [$archiveDir] has no parent folder"
            assert ( archiveDir.isDirectory() || archiveDir.mkdirs())

            def patterns = "${ includes ?: '' }/${ excludes ?: '' }"
            patterns     = (( patterns == '/' ) ? '' : " ($patterns)" )

            getLog( this ).info( "Packing [$sourceDirectoryPath$patterns] to [$destinationArchivePath]" )

            def time             = System.currentTimeMillis()
            def archiveExtension = extension( destinationArchive )

            if ( ZIP_EXTENSIONS.contains( archiveExtension ))
            {  // http://evgeny-goldin.org/javadoc/ant/CoreTasks/zip.html
                new AntBuilder().zip( destfile  : destinationArchivePath,
                                      basedir   : sourceDirectoryPath,
                                      includes  : ( includes ?: [] ).join( ',' ),
                                      excludes  : ( excludes ?: [] ).join( ',' ),
                                      whenempty : failIfNotFound ? 'fail' : 'skip' )
            }
            else if ( TAR_EXTENSIONS.contains( archiveExtension ))
            {   // http://evgeny-goldin.org/javadoc/ant/CoreTasks/tar.html
                new AntBuilder().tar( destfile    : destinationArchivePath,
                                      basedir     : sourceDirectoryPath,
                                      includes    : ( includes ?: [] ).join( ',' ),
                                      excludes    : ( excludes ?: [] ).join( ',' ),
                                      longfile    : 'gnu',
                                      compression : tarCompression( archiveExtension ))
            }
            else
            {
                for ( File file in files( sourceDirectory, includes, excludes, caseSensitive, false, failIfNotFound ))
                {   /**
                     * https://truezip.dev.java.net/manual-6.html
                     * http://evgeny-goldin.org/javadoc/truezip/
                     */
                    def relativePath = verify.notNullOrEmpty( file.canonicalPath.substring( sourceDirectoryPath.length()))
                    de.schlichtherle.io.File.cp_p( file, new de.schlichtherle.io.File( destinationArchive, relativePath ))
                }

                de.schlichtherle.io.File.umount()
            }

            verify.notEmptyFile( destinationArchive )

            getLog( this ).info( "[$sourceDirectory$patterns] packed to [$destinationArchivePath] " +
                                 "(${( System.currentTimeMillis() - time ).intdiv( 1000 )} sec)" )

            destinationArchive
        }
        catch ( Throwable t )
        {
            throw new RuntimeException( "Failed to pack [$sourceDirectoryPath] to [$destinationArchivePath]: $t",
                                        t )
        }
    }


    /**
     * Unpacks an archive file to the directory specified.
     * Note: target directory is deleted before operation starts!
     *
     * @param sourceArchive        archive file to unpack
     * @param destinationDirectory directory to unpack the file to
     *
     * @return destination directory where archive was unpacked
     */
    File unpack ( File sourceArchive,
                  File destinationDirectory )
    {
        verify.notEmptyFile( sourceArchive )
        verify.notNull( destinationDirectory )

        def sourceArchivePath        = sourceArchive.canonicalPath
        def destinationDirectoryPath = destinationDirectory.canonicalPath

        try
        {
            if ( destinationDirectory.isFile()) { delete( destinationDirectory ) }
            mkdirs( destinationDirectory )

            getLog( this ).info( "Unpacking [$sourceArchivePath] to [$destinationDirectoryPath]" )

            def time             = System.currentTimeMillis()
            def archiveExtension = extension( sourceArchive )

            if ( ZIP_EXTENSIONS.contains( archiveExtension ))
            {
                // http://evgeny-goldin.org/javadoc/ant/CoreTasks/unzip.html
                new AntBuilder().unzip( src  : sourceArchivePath,
                                        dest : destinationDirectoryPath )
            }
            else if ( TAR_EXTENSIONS.contains( archiveExtension ))
            {   // http://evgeny-goldin.org/javadoc/ant/CoreTasks/unzip.html
                new AntBuilder().untar( src         : sourceArchivePath,
                                        dest        : destinationDirectoryPath,
                                        compression : tarCompression( archiveExtension ))
            }
            else
            {
                /**
                 * https://truezip.dev.java.net/manual-6.html
                 * http://evgeny-goldin.org/javadoc/truezip/
                 * {@link de.schlichtherle.io.File#archiveCopyAllTo(File)}
                 */
                def detector = new SingleFileArchiveDetector( sourceArchive, archiveExtension )
                de.schlichtherle.io.Files.cp_r( true,
                                                new de.schlichtherle.io.File( sourceArchive, detector ),
                                                destinationDirectory, detector, detector );
                de.schlichtherle.io.File.umount()
            }

            verify.directory( destinationDirectory )
            getLog( this ).info( "[$sourceArchivePath] unpacked to [$destinationDirectoryPath] " +
                                 "(${( System.currentTimeMillis() - time ).intdiv( 1000 )} sec)" )

            destinationDirectory
        }
        catch ( Throwable t )
        {
            throw new RuntimeException( "Failed to unpack [$sourceArchivePath] to [$destinationDirectoryPath]: $t",
                                        t )
        }
    }

    /**
     * Unpack ZIP entries specified to the directory provided.
     *
     * @param sourceArchive          ZIP file to unpack
     * @param destinationDirectory   directory to unpack the file to
     * @param zipEntries             ZIP entries to unpack, should contain non-empty entries
     * @param preservePath whether entry path should be preserved, when unpacking
     * @return
     */
    File unpackZipEntries ( File         sourceArchive,
                            File         destinationDirectory,
                            List<String> zipEntries   = [],
                            boolean      preservePath = false,
                            boolean      verbose      = true )
    {
        verify.notEmptyFile( sourceArchive )
        verify.notNull( destinationDirectory )

        def sourceArchivePath        = sourceArchive.canonicalPath
        def destinationDirectoryPath = destinationDirectory.canonicalPath
        def archiveExtension         = extension( sourceArchive )
        def entries                  = new HashSet<String>( /* Cleanup and normalize: '\' => '/', no leading slash */
            zipEntries*.trim().findAll{ it }*.replace( '\\', '/' )*.replaceAll( /^\//, '' ))

        assert entries, \
               "Zip entries list is empty: $zipEntries => $entries"
        assert ZIP_EXTENSIONS.contains( archiveExtension ), \
               "Extension [$archiveExtension] is not recognized as ZIP file, zip entries $zipEntries cannot be used"

        def entriesWord = (( entries.size() == 1 ) ? 'entry' : 'entries' )

        try
        {
            if ( destinationDirectory.isFile()) { delete( destinationDirectory ) }
            mkdirs( destinationDirectory )

            getLog( this ).info( "Unpacking [$sourceArchivePath] $entriesWord $entries to [$destinationDirectoryPath]" )
            
            def time    = System.currentTimeMillis()
            def zipFile = new ZipFile( sourceArchive )

            for ( entry in entries )
            {
                assert   entry,                  "Empty or null entry [$entry]"
                ZipEntry zipEntry = zipFile.getEntry( entry )
                assert   zipEntry,               "Zip entry [$entry] doesn't exist in [$sourceArchivePath]"
                assert   zipEntry.name == entry, "Zip entry [$entry] doesn't equal to entry name [$zipEntry.name]"

                if ( zipEntry.name.endsWith( '/' ))
                {   // Directory entry
                    assert zipEntry.size == 0, "Zip entry [$entry] ends with '/' but it's size is not zero [$zipEntry.size]"
                    continue
                }

                def    is = zipFile.getInputStream( zipEntry )
                assert is, "Failed to read entry [$entry] InputStream from [$sourceArchivePath]"

                def targetFile = delete( new File( destinationDirectory,
                                                   ( preservePath ? zipEntry.name : zipEntry.name.replaceAll( /^.*\//, '' ))))
                mkdirs( targetFile.parentFile )

                def os           = new BufferedOutputStream( new FileOutputStream( targetFile ))
                def bytesWritten = 0

                os.withStream { is.eachByte( 10240 ) {
                    byte[] buffer, int length ->
                    bytesWritten += length
                    os.write( buffer, 0, length ) }
                }

                verify.file( targetFile )
                assert ( bytesWritten == zipEntry.size ) && ( targetFile.size() == zipEntry.size ), \
                       "Zip entry [$entry]: size is [$zipEntry.size], [$bytesWritten] bytes written, " +
                       "[${ targetFile.size() }] file size of [$targetFile.canonicalPath]"
                
                if ( verbose )
                {
                    getLog( this ).info( "[$sourceArchivePath]/[$entry] is written to [$targetFile.canonicalPath], " +
                                         "[$bytesWritten] byte${ general.s( bytesWritten ) }" )
                }
            }

            verify.directory( destinationDirectory )
            getLog( this ).info( "[$sourceArchivePath] $entriesWord $entries unpacked to [$destinationDirectoryPath] " +
                                 "(${( System.currentTimeMillis() - time ).intdiv( 1000 )} sec)" )

            destinationDirectory
        }
        catch ( Throwable t )
        {
            throw new RuntimeException( "Failed to unpack [$sourceArchivePath] $entriesWord $entries to [$destinationDirectoryPath]: $t",
                                        t )
        }
    }


    /**
     * Retrieves file's extension.
     *
     * @param f file to retrieve its extension
     * @return file extension or null if it is missing
     */
    String extension ( File f )
    {
        def name = f.name.toLowerCase()

        if ( name.endsWith( '.tar.gz'  )) { return 'tar.gz'  }
        if ( name.endsWith( '.tar.bz2' )) { return 'tar.bz2' }

        def dotIndex = name.lastIndexOf( '.' )
        ( dotIndex > 0 ) ? name.substring( dotIndex + 1 ) : null
    }


    /**
     * Calculates total size of directories specified.
     *
     * @param directories directories to read
     * @return total size of all files, iterated with
     *         {@link org.codehaus.groovy.runtime.DefaultGroovyMethods#eachFileRecurse(File, Closure)}
     */
    long directorySize( File ... directories )
    {
        long size = 0

        for ( directory in directories )
        {
            verify.directory( directory ).eachFileRecurse( FileType.FILES ){ size += it.size() }
        }

        size
    }
}
