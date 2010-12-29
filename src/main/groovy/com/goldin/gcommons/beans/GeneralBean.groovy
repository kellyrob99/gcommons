package com.goldin.gcommons.beans

import org.springframework.util.AntPathMatcher

/**
 * General usage methods
 */
class GeneralBean extends BaseBean
{

    /**
     * Verifier, set by Spring
     */
    VerifyBean verify



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

        ( path, pattern ) = [ path, pattern ]*.replaceAll( /\\+/, AntPathMatcher.DEFAULT_PATH_SEPARATOR )

        if ( path.startsWith( AntPathMatcher.DEFAULT_PATH_SEPARATOR ) != pattern.startsWith( AntPathMatcher.DEFAULT_PATH_SEPARATOR ))
        {   /**
             * Otherwise, false is returned
             */
            pattern = "${ AntPathMatcher.DEFAULT_PATH_SEPARATOR }${ pattern }"
        }

        new AntPathMatcher().match( pattern, path )
    }
}
