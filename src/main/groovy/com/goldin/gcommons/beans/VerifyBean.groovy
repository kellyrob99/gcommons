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
     * Verifies all objects specified are nulls
     * @param objects objects to check
     */
    void isNull( Object ... objects )
    {
        for ( o in objects )
        {
            assert ( o == null ), "Object specified [$o] is *not* null"
        }
    }

    /**
     * Verifies objects specified are not null
     * @param objects objects to check
     * @return first object checked
     */
    public <T> T notNull( T ... objects )
    {
        for ( o in objects )
        {
            assert ( o != null ), "Object specified [$o] *is* null"
        }

        first( objects )
    }


    /**
     * Verifies that files specified exist.
     * @param files files to check
     * @return  first file checked
     */
    File exists ( File ... files )
    {
        for ( file in files )
        {
            assert file.exists(), "File specified [$file] *does not* exist"
        }

        first( files )
    }


    /**
     * Verifies that files specified are existing files.
     * @param files files to check
     * @return  first file checked
     */
    File file ( File ... files )
    {
        for ( file in files )
        {
            assert file.isFile(), "File specified [$file] is *not* an existing file"
        }

        first( files )
    }

    
    /**
     * Verifies files specified are actual files that are not empty.
     * @param files file to check
     * @return first file checked
     */
    File notEmptyFile ( File ... files )
    {
        for ( file in files )
        {
            assert file.isFile(),    "File specified [$file] is *not* an existing file"
            assert file.size() > 0 , "File specified [$file] *is* empty"
        }

        first( files )
    }

    
    /**
     * Verifies that directories specified are existing directories.
     * @param directories directories to check
     * @return  first directory checked
     */
    File directory ( File ... directories )
    {
        for ( directory in directories )
        {
            assert directory.isDirectory(), "Directory specified [$directory] is *not* an existing directory"
        }

        first( directories )
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


    /**
     * Verifies that Springs specified are not null or empty.
     * @param strings strings to check
     * @return first string checked
     */
    String notNullOrEmpty( String ... strings )
    {
        assert ( strings != null )
        
        for ( s in strings )
        {
            assert s?.trim()?.length(), "String specified [$s] *is* null or empty"
        }

        first( strings )
    }
}