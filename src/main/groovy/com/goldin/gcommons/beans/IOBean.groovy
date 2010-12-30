package com.goldin.gcommons.beans


/**
 * I/O-related utilities
 */
class IOBean extends BaseBean
{
    /**
     * Verifier, set by Spring
     */
    VerifyBean verify


    long copy ( InputStream input, OutputStream output )
    {
        byte[] buffer = new byte[ 1024 ]
        long   count = 0
        for ( int n = 0; ( n = input.read( buffer )) != -1; count += n )
        {
            output.write( buffer, 0, n )
        }

        count
    }

    
    Closeable close ( Closeable c )
    {
        try { if ( c!= null ) { c.close() }} 
        catch ( IOException ignored ) {}

        c
    }
}
