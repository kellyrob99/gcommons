package com.goldin.gcommons.beans

import com.goldin.gcommons.BaseTest
import org.junit.Test

/**
 * {@link NetBean} tests
 */
class NetBeanTest extends BaseTest
{

    @Test
    void shouldListFtpFile()
    {
        netBean.ftpList( 'ftp://calais:EXA71821@rkd.knowledge.reuters.com:/', '', '' )
    }

}
