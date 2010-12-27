package com.goldin.gcommons

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Verification methods
 */
class Verify
{
    private static final Logger LOG = LoggerFactory.getLogger( Verify.class )

    void exists    ( File f ) { assert f.exists(),      "[$f] doesn't exist"      }
    void file      ( File f ) { assert f.isFile(),      "[$f] is not a file"      }
    void directory ( File f ) { assert f.isDirectory(), "[$f] is not a directory" }



    void equal ( File file1, File file2, boolean verifyChecksum = true, String pattern = null )
    {
        file1.exists()
        file2.exists()

        LOG.info( "aaaaaaaaaaaaaa" )
    }
}
