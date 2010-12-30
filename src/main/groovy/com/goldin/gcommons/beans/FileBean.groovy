package com.goldin.gcommons.beans

import com.goldin.gcommons.SingleFileArchiveDetector
import groovy.io.FileType
import java.security.MessageDigest
import org.apache.tools.ant.DirectoryScanner

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
            if ( f.exists())
            {
                if ( f.isDirectory() && f.listFiles()) { delete( f.listFiles()) }
                assert ! f.listFiles()
                assert f.delete() && ( ! f.exists())
            }
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
        if ( destinationArchive.exists()){ delete( destinationArchive ) }

        assert ! destinationArchive.exists()
        File archiveDir = destinationArchive.getParentFile()
        assert ( archiveDir != null ), "Destination archive [$archiveDir] has no parent folder"
        assert ( archiveDir.isDirectory() || archiveDir.mkdirs())

        def patterns = "${ includes ?: '' }/${ excludes ?: '' }"
        patterns     = (( patterns == '/' ) ? '' : " ($patterns)" )

        getLog( this ).info( "Packing [${ sourceDirectory.canonicalPath }$patterns] to [${ destinationArchive.canonicalPath }]" )
        final long time = System.currentTimeMillis()

        for ( File file in files( sourceDirectory, includes, excludes, caseSensitive, false, failIfNotFound ))
        {
            String relativePath = verify.notNullOrEmpty( file.canonicalPath.substring( sourceDirectory.canonicalPath.length()))
            assert ( relativePath.startsWith( '/' ) || relativePath.startsWith( '\\' ))

            /**
             * https://truezip.dev.java.net/manual-6.html
             */
            de.schlichtherle.io.File.cp_p( file, new de.schlichtherle.io.File( destinationArchive.canonicalPath + relativePath ))
        }

        de.schlichtherle.io.File.umount()
        verify.notEmptyFile( destinationArchive )
        getLog( this ).info( "[$sourceDirectory$patterns] packed to [${ destinationArchive.canonicalPath }] " +
                             "(${( System.currentTimeMillis() - time ).intdiv( 1000 )} sec)" )

        destinationArchive
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
    File unpack ( File sourceArchive, File destinationDirectory )
    {
        verify.notEmptyFile( sourceArchive )
        if ( destinationDirectory.exists()) { delete( destinationDirectory ) }

        mkdirs( destinationDirectory )

        getLog( this ).info( "Unpacking [${ sourceArchive.canonicalPath }] to [${ destinationDirectory.canonicalPath }]" )
        final long time = System.currentTimeMillis()

        /**
         * https://truezip.dev.java.net/manual-6.html
         */
        def detector = new SingleFileArchiveDetector( sourceArchive, extension( sourceArchive ))
        assert new de.schlichtherle.io.File( sourceArchive, detector ).archiveCopyAllTo( destinationDirectory )
        de.schlichtherle.io.File.umount()

        verify.directory( destinationDirectory )
        getLog( this ).info( "[${ sourceArchive.canonicalPath }] unpacked to [${ destinationDirectory.canonicalPath }] " +
                             "(${( System.currentTimeMillis() - time ).intdiv( 1000 )} sec)" )
        
        destinationDirectory
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
            assert ( directory.isDirectory() || directory.mkdirs())
        }

        first( directories )
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
