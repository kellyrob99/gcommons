package com.goldin.gcommons

import org.springframework.util.AntPathMatcher

 /**
 * General useage methods
 */
class General
{

    /**
     * Verifier, set by Spring
     */
    Verify verify


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
        File.createTempFile( General.class.name, '' )
    }


    File tempDirectory()
    {
        def file      = tempFile()
        def directory = new File( file.absolutePath )
        assert ! delete( file ).exists()
        assert directory.mkdirs()

        directory
    }

    
    File delete ( File ... files )
    {
        for ( f in files )
        {
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
     * {@link org.springframework.util.PathMatcher#match(String, String)} wrapper
     * @param path    path to match
     * @param pattern pattern to use, prepended with {@link org.springframework.util.AntPathMatcher#DEFAULT_PATH_SEPARATOR}
     *                                if path start with {@link org.springframework.util.AntPathMatcher#DEFAULT_PATH_SEPARATOR}
     *
     * @return true if path specified matches the pattern,
     *         false otherwise
     */
    boolean match ( String path, String pattern )
    {
        verify.notNullOrEmpty( path, pattern )

        if ( path.startsWith( AntPathMatcher.DEFAULT_PATH_SEPARATOR ) != pattern.startsWith( AntPathMatcher.DEFAULT_PATH_SEPARATOR ))
        {   /**
             * Otherwise, false is returned
             */
            pattern = "${ AntPathMatcher.DEFAULT_PATH_SEPARATOR }${ pattern }"
        }

        new AntPathMatcher().match( pattern, path )
    }


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
