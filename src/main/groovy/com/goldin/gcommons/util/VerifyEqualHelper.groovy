package com.goldin.gcommons.util

import com.goldin.gcommons.beans.BaseBean
import com.goldin.gcommons.beans.FileBean

/**
 * {@link com.goldin.gcommons.beans.VerifyBean#equal(File, File, boolean, String)} helper class
 */
class VerifyEqualHelper extends BaseBean
{
    /**
     * Set by Spring
     */
    FileBean file


    /**
     * Verifies two files specified are equal, considering the pattern and returns 1 or 0
     * 1 if files matched the pattern
     * 0 otherwise
     */
    int verifyEqualFiles( File    file1,
                          File    file2,
                          String  pattern,
                          boolean verifyChecksum,
                          String  endOfLine,
                          boolean log = true )
    {
        assert file1.isFile() && file2.isFile()

        def file1Path = file1.canonicalPath
        def file2Path = file2.canonicalPath

        if (( ! pattern ) || ( [ file1Path, file2Path ].every{ general.match( it, pattern ) } ))
        {
            if ( endOfLine )
            {
                boolean windows = ( 'windows' == endOfLine )
                String  crlf    = ( windows ? '\r\n' : '\n' )
                file1.write( file1.text.replaceAll( /\r?\n/, crlf ))
                file2.write( file2.text.replaceAll( /\r?\n/, crlf ))

                getLog( this ).debug( "[$file1Path] and [$file2Path] have CrLf normalized to \"${ windows ? '\\r\\n' : '\\n' }\"" )
            }

            def  ( file1Length,   file2Length ) = [ file1, file2 ]*.length()
            assert file1Length == file2Length,  "( [$file1Path] length [$file1Length] ) != ( [$file2Path] length [$file2Length] )"

            if ( verifyChecksum )
            {
                def ( file1Checksum,  file2Checksum ) = [ file1, file2 ].collect { file.checksum( it, 'MD5' ) }

                assert file1Checksum  == file2Checksum,  \
                       "( [$file1Path] SHA-1 checksum [$file1Checksum] ) != ( [$file2Path] SHA-1 checksum [$file2Checksum] )"
            }

            if ( log )
            {   // File equality is not logged when verifying directories
                getLog( this ).info ( "[$file1Path] = [$file2Path]" + ( pattern ? " ($pattern)" : '' ))
            }

            1
        }
        else
        {
            0
        }
    }


   /**
    * Verifies two directories specified are equal, considering the pattern and returns number of files verified
    */
    int verifyEqualDirectories( File    dir1,
                                File    dir2,
                                String  pattern,
                                boolean verifyChecksum,
                                String  endOfLine  )
    {
        assert dir1.isDirectory() && dir2.isDirectory()

        def dir1Path          = dir1.canonicalPath
        def dir2Path          = dir2.canonicalPath
        def dir2VerifiedFiles = new HashSet<File>()
        int filesChecked      = 0

        /**
         * Verifying that each file in 'dir1' has a corresponding and equal file in 'dir2'
         */
        dir1.recurse {

            File dir1File ->
            File dir2File = new File( dir2, dir1File.canonicalPath.replace( dir1Path, '' ))

            filesChecked += checkFiles( dir1File, dir2File, pattern, verifyChecksum, endOfLine )

            dir2VerifiedFiles << dir2File
        }


        /**
         * If no pattern is applied - verifying that each file in 'dir2' was verified by a previous check
         * and has a corresponding file in 'dir1'
         */
        if ( ! pattern )
        {
            dir2.recurse {

                File dir2File ->

                if ( ! dir2VerifiedFiles.contains( dir2File ))
                {
                    File dir1File = new File( dir1, dir2File.canonicalPath.replace( dir2Path, '' ))

                    def dir1FilePath  = dir1File.canonicalPath
                    def dir2FilePath  = dir2File.canonicalPath

                    if ( dir1File.exists())
                    {
                        /**
                         * The file wasn't verified when we iterated "dir1" but probably it was added later
                         */

                        filesChecked += checkFiles( dir1File, dir2File, pattern, verifyChecksum, endOfLine )
                    }
                    else
                    {
                        if ( dir2File.isFile() && (( ! pattern ) || general.match( dir2File.canonicalPath, pattern )))
                        {
                            assert false, "There's no file [$dir1FilePath] corresponding to file [$dir2FilePath]"
                        }
                        else if ( dir2File.isDirectory() && ( ! pattern ))
                        {
                            // Directory can only be missed when a pattern is applied
                            assert false, "There's no directory [$dir1FilePath] corresponding to directory [$dir2FilePath]"
                        }
                    }
                }
            }
        }

        getLog( this ).info( "[$dir1Path] = [$dir2Path]" +
                             ( pattern ? " ($pattern)" : '' ))
        filesChecked
    }


    /**
     * Checks two files/directories specified, considering the pattern and returns number of files checked:
     * 1 for two files, 0 for two directories
     */
    private int checkFiles ( File    file1,
                             File    file2,
                             String  pattern,
                             boolean verifyChecksum,
                             String  endOfLine )
    {
        def file1Path  = file1.canonicalPath
        def file2Path  = file2.canonicalPath

        if ( file1.isFile())
        {
            if (( ! pattern ) || general.match( file1Path, pattern ))
            {
                if ( file2.isFile())
                {
                    assert (( ! pattern ) || general.match( file2Path, pattern )), \
                           "[$file2Path] doesn't match pattern [$pattern] while [$file1Path] does"

                    return verifyEqualFiles ( file1, file2, pattern, verifyChecksum, endOfLine, false )
                }
                else
                {
                    assert false, "There's no file [$file2Path] corresponding to file [$file1Path]"
                }
            }
        }
        else if ( file1.isDirectory())
        {
            // Directories allowed to be missed if pattern is applied
            assert ( file2.isDirectory() || ( pattern )), \
                   "There's no directory [$file2Path] corresponding to directory [$file1Path]"
        }

        0
    }
}
