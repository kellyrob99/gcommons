package com.goldin.gcommons.beans

/**
 * File-related helper utilities.
 */
class FileBean extends BaseBean
{
    /**
     * Verifier, set by Spring
     */
    VerifyBean verify

    
    File unpack ( File archive, File directory )
    {
        println "unpack"
        null
    }


    File pack ( File archive, File directory )
    {
        println "pack"
        null
    }


    File tempFile()
    {
        File.createTempFile( GeneralBean.class.name, '' )
    }


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
            verify.exists( f )
            if ( f.isDirectory())
            {
                f.eachFileRecurse {
                    if ( ! it.delete())
                    {
                        Thread.sleep( 1000 )
                        assert it.delete()
                    }
                }
            }

            assert ( f.delete()) && ( ! f.exists())
        }

        files[ 0 ]
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
}
