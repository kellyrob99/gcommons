package com.goldin.gcommons.beans

import org.apache.tools.ant.DirectoryScanner
import org.apache.tools.zip.ZipEntry
import org.apache.tools.zip.ZipFile

/**
 * File-related helper utilities.
 */
class FileBean extends BaseBean
{
    /**
     * Set by Spring
     */
    VerifyBean verify
    IOBean     io

    
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
        assert ! delete( file ).exists()
        assert directory.mkdirs()

        directory
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
            if ( f.isDirectory() && f.listFiles()) { delete( f.listFiles()) }
            assert ! f.listFiles()
            assert ( f.delete()) && ( ! f.exists())
        }

        first( files )
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
        assert file.isFile(), "File [$file] doesn't exist"
        verify.notNullOrEmpty( algorithm )

        File   tempDir      = tempDirectory()
        File   checksumFile = new File( tempDir, "${ file.name }.${ algorithm }" )

        assert ( ! checksumFile.isFile()) || ( checksumFile.delete())

        new AntBuilder().checksum( file      : file.absolutePath,
                                   algorithm : algorithm,
                                   todir     : tempDir )

        def checksum = verify.file( checksumFile ).text.trim()
        delete( tempDir )
        
        checksum
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
               "No files are included by parent dir [$baseDirectory] and include/exclude patterns $includes/$excludes"

        files
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

        if ( destinationArchive.isFile())
        {
            delete( destinationArchive )
        }

        File archiveDir = destinationArchive.getParentFile()
        assert ( archiveDir != null ), "Destination archive [$archiveDir] has no parent folder"

        assert ( archiveDir.isDirectory() || archiveDir.mkdirs())

        getLog( this ).info( "Packing [$sourceDirectoryPath ($includes/$excludes)] to [$destinationArchivePath]" )

        final long time = System.currentTimeMillis()

        for ( File file in files( sourceDirectory, includes, excludes, caseSensitive, false, failIfNotFound ))
        {
            String relativePath = verify.notNullOrEmpty( file.canonicalPath.substring( sourceDirectory.canonicalPath.length()))
            assert ( relativePath.startsWith( '/' ) || relativePath.startsWith( '\\' ))

            String destinationPath = ( destinationArchive + relativePath )
            de.schlichtherle.io.File.cp_p( file, new de.schlichtherle.io.File( destinationPath ))
        }

        de.schlichtherle.io.File.umount()
        verify.file( destinationArchive )

        int timeInSec = ( System.currentTimeMillis() - time ).intdiv( 1000 )
        getLog( this ).info( "[$sourceDirectory ($includes/$excludes)] packed to [$destinationArchivePath] ($timeInSec sec)" )

        destinationArchive
    }


    /**
     * Unpacks an archive file to the directory specified
     *
     * @param sourceArchive        archive file to unpack
     * @param destinationDirectory directory to unpack the file to
     *
     * @return destination directory where archive was unpacked
     */
    File unpack ( File sourceArchive, File destinationDirectory )
    {
        verify.file( sourceArchive )
        assert sourceArchive.size() > 0, "Archive [$sourceArchive] is empty"
        assert ( destinationDirectory.isDirectory() || destinationDirectory.mkdirs())

        String sourceArchivePath        = sourceArchive.canonicalPath
        String destinationDirectoryPath = destinationDirectory.canonicalPath

        getLog( this ).info( "Unpacking [$sourceArchivePath] to [$destinationDirectoryPath]" )
        final long time = System.currentTimeMillis()

        def dotIndex         = sourceArchivePath.lastIndexOf( '.' )
        def archiveExtension = ( dotIndex > 0 ) ? sourceArchivePath.substring( dotIndex + 1 ).toLowerCase() : null

        if ( [ 'zip', 'jar', 'war', 'ear' ].contains( archiveExtension ))
        {
            /**
             * Using Ant's "unzip" implementation - TrueZip modifies some files when unpacking them :(
             * Try packing and unpacking "apache-maven-3.0.1" - you'll get folders of different size, some *.jar
             * files are slightly smaller than their original versions, though they all unpack Ok.
             */
            ZipFile     zipFile = new ZipFile( sourceArchive )
            Enumeration entries = zipFile.getEntries()

            while( entries.hasMoreElements())
            {
                ZipEntry entry    = ( ZipEntry ) entries.nextElement()
                File     destFile = new File( destinationDirectory, entry.getName())

                if ( entry.getName().endsWith( '/' ))
                {
                    assert ( destFile.isDirectory() || destFile.mkdirs())
                }
                else
                {
                    assert ( destFile.getParentFile().isDirectory() || destFile.getParentFile().mkdirs())

                    OutputStream os          = new FileOutputStream( destFile )
                    long         bytesCopied = io.copy( zipFile.getInputStream( entry ), os )

                    io.close( os )

                    assert ( bytesCopied == entry.getSize()), \
                           "[$sourceArchivePath]/[$entry.name]: size is [$entry.size] but [$bytesCopied] bytes copied"
                }
            }

            zipFile.close()
        }
        else
        {
            de.schlichtherle.io.File.cp_p( sourceArchive, destinationDirectory )
            de.schlichtherle.io.File.umount()
        }

        verify.directory( destinationDirectory )
        int timeInSec = ( System.currentTimeMillis() - time ).intdiv( 1000 )
        getLog( this ).info( "[$sourceArchivePath] unpacked to [$destinationDirectoryPath] ($timeInSec sec)" )

        destinationDirectory
    }
}
