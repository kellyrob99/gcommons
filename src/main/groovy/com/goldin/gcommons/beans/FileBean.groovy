package com.goldin.gcommons.beans

import org.apache.tools.ant.DirectoryScanner

 /**
 * File-related helper utilities.
 */
class FileBean extends BaseBean
{
    /**
     * Verifier, set by Spring
     */
    VerifyBean verify

    
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
     *
     * @param baseDirectory      files base directory
     * @param includes           patterns to use for including files, all files are included if null
     * @param excludes           patterns to use for excluding files, no files are excluded if null
     * @param includeDirectories whether directories included should be returned as well
     * @param failIfNotFound     whether execution should fail if no files were found
     *
     * @return files under base directory specified passing an inclusion/exclusion patterns
     */
    List<File> files ( File         baseDirectory,
                       List<String> includes           = null,
                       List<String> excludes           = null,
                       boolean      includeDirectories = true,
                       boolean      failIfNotFound     = true )
    {
        verify.directory( baseDirectory )

        def ds    = new DirectoryScanner()
        def files = []

        ds.setBasedir( baseDirectory )
        ds.setIncludes( includes as String[] )
        ds.setExcludes( excludes as String[] )

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
     * Archives directory to archive specified.
     *  
     * @param sourceDirectory    directory to archive
     * @param destinationArchive archive to pack the directory to
     * @param includes           patterns to use for including files, all files are included if null
     * @param excludes           patterns to use for excluding files, no files are excluded if null
     * @param failIfNotFound     whether execution should fail if no files were found
     */
    void pack ( File         sourceDirectory,
                File         destinationArchive,
                List<String> includes       = null,
                List<String> excludes       = null,
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

        File archiveDir = destinationArchive.getParentFile();
        assert ( archiveDir != null ), "Destination archive [$archiveDir] has no parent folder"

        assert ( archiveDir.isDirectory() || archiveDir.mkdirs())

        getLog( this ).info( "Packing [$sourceDirectoryPath ($includes/$excludes)] to [$destinationArchivePath]" )

        final long time = System.currentTimeMillis();

        for ( File file in files( sourceDirectory, includes, excludes, false, failIfNotFound ))
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
    }
}
