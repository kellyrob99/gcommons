package com.goldin.gcommons.beans

import com.goldin.gcommons.util.VerifyEqualHelper

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
        assert objects != null

        for ( o in objects )
        {
            assert ( o == null ), "Object specified [$o] is not null"
        }
    }

    /**
     * Verifies objects specified are not null
     * @param objects objects to check
     * @return first object checked
     */
    public <T> T notNull( T ... objects )
    {
        assert objects != null

        for ( o in objects )
        {
            assert ( o != null ), "Object specified [$o] is null"
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
        assert files != null

        for ( file in files )
        {
            assert file.exists(), "[$file.canonicalPath] does not exist"
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
        assert files != null

        for ( file in files )
        {
            assert file.isFile(), "File [$file.canonicalPath] does not exist"
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
        assert files != null

        for ( file in files )
        {
            assert file.isFile(),    "File [$file.canonicalPath] does not exist"
            assert file.size() > 0 , "File [$file.canonicalPath] is empty"
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
        assert directories != null

        for ( directory in directories )
        {
            assert directory.isDirectory(), "Directory [$directory.canonicalPath] does not exist"
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

        int nFiles =  [ file1, file2 ].every { it.isFile() } ?
            verifyEqualHelper.verifyEqualFiles       ( file1, file2, pattern, verifyChecksum, endOfLine ) :
            verifyEqualHelper.verifyEqualDirectories ( file1, file2, pattern, verifyChecksum, endOfLine )

        if (( ! pattern ) && ( [ file1, file2 ].every { it.isDirectory() } ))
        {
            assert file1.directorySize() == file2.directorySize(), \
                   "Directory sizes of [$file1.canonicalPath] and [$file2.canonicalPath] are not the same"
        }

        nFiles
    }


    /**
     * Verifies that Springs specified are not null or empty.
     * @param strings strings to check
     * @return first string checked
     */
    String notNullOrEmpty( String ... strings )
    {
        assert strings != null

        for ( s in strings )
        {
            assert s?.trim()?.length(), "String specified [$s] is null or empty"
        }

        first( strings )
    }


    /**
     * Verifies that Collections specified are not null or empty.
     * @param collections collections to check
     * @return first collection checked
     */
    public <T> Collection<T> notNullOrEmpty( Collection<T> ... collections )
    {
        assert collections != null

        for ( c in collections )
        {
            assert c?.size(), "Collection specified $c is null or empty"
        }

        first( collections )
    }
}
