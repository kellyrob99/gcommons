package com.goldin.gcommons.beans

import org.apache.commons.net.ftp.FTPClient

/**
 * Network-related helper methods.
 */
class NetBean extends BaseBean
{

    /**
     * Verifier, set by Spring
     */
    VerifyBean verify


    List<String> ftpList( String remotePath, String includePattern, String excludePattern )
    {
        FTPClient client = new FTPClient()
        null
    }

}
