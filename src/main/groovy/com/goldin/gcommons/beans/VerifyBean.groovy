package com.goldin.gcommons.beans

/**
 * Verification methods
 */
class VerifyBean extends BaseBean
{
    /**
     * Set by Spring
     */
    VerifyEqualHelper verifyEqualHelper


    /**
     * Verifies that file specified exists.
     * @param f file to check
     * @return  file checked
     */
    File exists ( File f )
    {
        assert f.exists(), "[$f] doesn't exist"
        f
    }


    /**
     * Verifies that file specified is existing file.
     * @param f file to check
     * @return  file checked
     */
    File file ( File f )
    {
        assert f.isFile(), "[$f] is not a file"
        f
    }

    
    /**
     * Verifies that file specified is existing directory.
     * @param f directory to check
     * @return  directory checked
     */
    File directory ( File f )
    {
        assert f.isDirectory(), "[$f] is not a directory"
        f
    }

    
    /**
     * Verifies two files or directories specified are equal.
     * 
     * @param file1          file or directory
     * @param file2          another file or directory
     * @param verifyChecksum whether content checksum verification should be performed
     * @param pattern        include pattern, like "*.xml".
     *                       Only files matching the include pattern will be verified.
     *                       All files are verified if <code>null</code>.
     * @param endOfLine      Whether all and of lines should be normalized before comparing files.
     *                       Nothing is done if <code>null</code>,
     *                       normalized to "\r\n" if <code>"windows"</code>,
     *                       normalized to "\n" if any other value
     * 
     * @return number of files checked and verified
     */
    int equal ( File    file1,
                File    file2,
                boolean verifyChecksum = true,
                String  pattern        = null,
                String  endOfLine      = null )
    {
        assert file1.exists() && file2.exists()

        ( file1.isFile() && file2.isFile()) ?
            verifyEqualHelper.verifyEqualFiles       ( file1, file2, pattern, verifyChecksum, endOfLine ) :
            verifyEqualHelper.verifyEqualDirectories ( file1, file2, pattern, verifyChecksum, endOfLine )
    }


    String notNullOrEmpty( String ... strings )
    {
        assert ( strings != null )
        
        for ( s in strings )
        {
            assert s?.trim()?.length(), "String specified [$s] *is* null or empty"
        }
        
        strings[ 0 ]
    }
}
