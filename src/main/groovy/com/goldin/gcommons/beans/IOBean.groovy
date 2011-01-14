package com.goldin.gcommons.beans


/**
 * I/O-related utilities
 */
class IOBean extends BaseBean
{
    long copy ( InputStream input, OutputStream output, long bytesExpected = -1 )
    {
        byte[] buffer         = new byte[ 2 * 1024 ]
        long   totalBytesRead = 0

        for ( int bytesRead = 0; (( bytesRead = input.read( buffer )) != -1 ); totalBytesRead += bytesRead )
        {
            output.write( buffer, 0, bytesRead )
        }

        close( input, output )

        if ( bytesExpected > -1 )
        {
            assert ( totalBytesRead == bytesExpected ), "[$bytesExpected] bytes should be read but [$totalBytesRead] bytes were read"
        }

        totalBytesRead
    }

    
    Closeable close ( Closeable ... closeables )
    {
        for ( c in closeables )
        {
            try { if ( c != null ) { c.close() }}
            catch ( IOException ignored ) {}
        }

        first( closeables )
    }
}
