package com.goldin.gcommons.beans

import com.goldin.gcommons.Constants
import java.util.regex.Matcher
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPFile
import org.apache.commons.net.ftp.FTPReply

 /**
 * Network-related helper methods.
 */
class NetBean extends BaseBean
{

    /**
     * Other beans, set by Spring
     */
    GeneralBean general
    VerifyBean  verify


    boolean isHttp ( String s ) { s.toLowerCase().startsWith( 'http://' ) }
    boolean isScp  ( String s ) { s.toLowerCase().startsWith( 'scp://'  ) }
    boolean isFtp  ( String s ) { s.toLowerCase().startsWith( 'ftp://'  ) }
    boolean isNet  ( String s ) { isHttp( s ) || isScp( s ) || isFtp( s ) }


    /**
     * Parses network path in the following format:
     * {@code "(http|scp|ftp)://user:password@server:/path/to/file"}
     *
     * @param path network path to parse
     * @return map with following entries: "protocol", "username", "password", "host", "directory"
     */
    Map<String, String> parseNetworkPath( String path )
    {
        assert isNet( verify.notNullOrEmpty( path ))
        Matcher matcher = ( path =~ Constants.NETWORK_PATTERN )

        assert ( matcher.find() && ( matcher.groupCount() == 5 )), \
               "Unable to parse [$path] as network path: it should be in format [<protocol>://<user>:<password>@<host>:<path>]. " +
               "Regex pattern is [${ Constants.NETWORK_PATTERN }]"

        def ( String protocol, String username, String password, String host, String directory ) =
            matcher[ 0 ][ 1 .. 5 ].collect{ verify.notNullOrEmpty( it ) }

        [
            protocol  : protocol,
            username  : username,
            password  : password,
            host      : host,
            directory : directory.replace( '\\', '/' )
        ]
    }


    /**
     * Initializes and connects an {@link FTPClient} using remote path specified of form:
     * {@code ftp://<user>:<password>@<host>:<path>}
     *
     * @param remotePath remote path to establish ftp connection to: {@code ftp://<user>:<password>@<host>:<path>}
     * @return client instance initialized and connected to FTP server specified
     */
    FTPClient ftpClient( String remotePath )
    {
        Map       data   = parseNetworkPath( remotePath )
        FTPClient client = new FTPClient()

        getLog( this ).info( "Connecting to FTP server [$data.host:$data.directory] as [$data.username] .." )

        try
        {
            client.connect( data.host )
            int reply = client.getReplyCode()
            assert FTPReply.isPositiveCompletion( reply ),          "Failed to connect to FTP server [$data.host], reply code is [$reply]"
            assert client.login( data.username, data.password ),    "Failed to connect to FTP server [$data.host] as [$data.username]"
            assert client.changeWorkingDirectory( data.directory ), "Failed to change FTP server [$data.host] directory to [$data.directory]"
            client.setFileType( FTP.BINARY_FILE_TYPE )
            client.enterLocalPassiveMode()
        }
        catch ( Throwable t )
        {
            client.logout()
            client.disconnect()
            throw new RuntimeException( "Failed to connect to FTP server [$remotePath]: $t", t )
        }

        getLog( this ).info( "Connected to FTP server [$data.host:$data.directory] as [$data.username]. " +
                             "Remote system is [$client.systemName], status is [$client.status]" )
        client
    }


    /**
     * Initializes and connects an {@link FTPClient} using remote path specified of form:
     * {@code ftp://<user>:<password>@<host>:<path>}. When connected, invokes the closure specified, passing
     * it {@link FTPClient} instance connected, and disconnects the client.
     *
     * @param remotePath remote path to establish ftp connection to: {@code ftp://<user>:<password>@<host>:<path>}
     * @param resultType closure expected result type,
     *                   if <code>null</code> - result type check is not performed
     * @param c closure to invoke and pass {@link FTPClient} instance
     * @return closure invocation result
     */
    public <T> T ftpClient( String remotePath, Class<T> resultType, Closure c )
    {
        verify.notNullOrEmpty( remotePath )
        verify.notNull( c, resultType )

        FTPClient client = null

        try
        {
            client = ftpClient( remotePath )
            return general.tryIt( 1, resultType ){ c( client ) }
        }
        finally
        {
            if ( client )
            {
                client.logout()
                client.disconnect()
            }
        }
    }


    /**
     * Lists files on the FTP server specified.
     *
     * @param remotePath remote path to establish ftp connection to: {@code ftp://<user>:<password>@<host>:<path>}
     * @param globPatterns glob patterns of files to list: {@code "*.*"} or {@code "*.zip"}
     * @param tries number of attempts
     * @return FTP files listed by remote FTP server using glob patterns specified
     */
    List<FTPFile> listFiles( String remotePath, List<String> globPatterns = ['*'], int tries = 5 )
    {
        verify.notNullOrEmpty( remotePath )
        assert tries > 0

        /**
         * Trying "tries" times to list files
         */
        general.tryIt( tries, List.class,
        {   /**
             * Getting a list of files for remote path
             */
            ftpClient( remotePath, List.class )
            {
                FTPClient client ->

                List<FTPFile> result = []

                getLog( this ).info( "Listing $globPatterns files .." )

                for ( String globPattern in globPatterns*.trim().collect{ verify.notNullOrEmpty( it ) } )
                {
                    FTPFile[] files = client.listFiles( globPattern )

                    if ( getLog( this ).isDebugEnabled())
                    {
                        getLog( this ).debug( "[$globPattern]:\n${ files*.name.join( '\n' ) }" )
                    }

                    result.addAll( files )
                }

                getLog( this ).info( "[${ result.size() }] file${ general.s( result.size()) }" )
                result
            }
        })
    }
}
