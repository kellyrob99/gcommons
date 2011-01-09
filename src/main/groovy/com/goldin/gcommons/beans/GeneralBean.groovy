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


    /**
     * Attempts to execute a closure specified and return its result.
     *
     * @param nTries     number of time execution will be attempted
     * @param resultType type of result returned by closure
     * @param c          closure to invoke
     * @return closure execution result
     * @throws RuntimeException if execution fails nTries times
     */
    public <T> T tryIt( int nTries, Class<T> resultType, Closure c )
    {
        assert ( nTries > 0 )
        verify.notNull( resultType, c )

        def tries = 0

        while( true )
        {
            try
            {
                Object value = c()
                assert ( value != null ),              "Result returned is null, should be of type [$resultType]"
                assert resultType.isInstance( value ), "Result returned [$value] is of type [${ value.class }], should be of type [$resultType]"
                return (( T ) value )
            }
            catch ( Throwable t )
            {
                assert tries < nTries
                if (( ++tries ) == nTries )
                {
                    throw new RuntimeException(
                        "Failed to perform action after [$tries] attempt${( tries == 1 ) ? '' : 's' }: $t", t )
                }
            }
        }
    }


    String s( Number n ) { ( n == 1 ) ? '' : 's' }
}
