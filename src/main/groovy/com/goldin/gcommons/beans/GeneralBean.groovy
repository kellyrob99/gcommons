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
     * @param resultType expected type of result to be returned by closure,
     *                   if <code>null</code> - result type check is not performed
     * @param c          closure to invoke
     * @return closure execution result
     * @throws RuntimeException if execution fails nTries times
     */
    public <T> T tryIt( int nTries, Class<T> resultType, Closure c )
    {
        assert ( nTries > 0 )
        verify.notNull( c )

        def tries = 0

        while( true )
        {
            try
            {
                Object value = c()
                assert ( resultType == null ) || ( value != null ), \
                       "Result returned is null, should be of type [$resultType]"
                assert ( resultType == null ) || resultType.isInstance( value ), \
                       "Result returned [$value] is of type [${ value.class }], should be of type [$resultType]"
                return (( T ) value )
            }
            catch ( Throwable t )
            {
                assert tries < nTries
                if (( ++tries ) == nTries )
                {
                    throw new RuntimeException( "Failed to perform action after [$tries] attempt${s( tries )}: $t", t )
                }
            }
        }
    }

    
    /**
     * Returns '' if number specified is 1, 's' otherwise. Used for combining plural sentences in log messages.
     * @param n number to check
     * @return '' if number specified is 1, 's' otherwise
     */
    String s( Number n ) { ( n == 1 ) ? '' : 's' }
}
