package com.goldin.gcommons.beans

import com.goldin.gcommons.BaseTest
import org.junit.Test



 /**
 * {@link com.goldin.gcommons.beans.VerifyBean} tests
 */
class VerifyBeanTest extends BaseTest
{
    @Test
    void shouldVerifyExists()
    {
        verifyBean.exists( constantsBean.USER_DIR_FILE  )
        verifyBean.exists( constantsBean.USER_HOME_FILE )

        def f = fileBean.tempFile()
        verifyBean.exists( f )
        fileBean.delete( f )

        shouldFailAssert { verifyBean.exists( f ) }
        shouldFailAssert { verifyBean.exists( new File( "Doesn't exist" )) }
        shouldFailWith( NullPointerException ) { verifyBean.exists( new File( System.getProperty( "aaa" ))) }
    }


    @Test
    void shouldVerifyFile()
    {
        def f = fileBean.tempFile()
        verifyBean.file( f )
        shouldFailAssert { verifyBean.file( f.getParentFile() ) }
        shouldFailAssert { verifyBean.file( f.getParentFile().getParentFile()) }

        fileBean.delete( f )

        shouldFailAssert { verifyBean.file( f ) }
    }


    @Test
    void shouldVerifyDirectory()
    {
        verifyBean.directory( constantsBean.USER_DIR_FILE  )
        verifyBean.directory( constantsBean.USER_HOME_FILE )

        def f = fileBean.tempFile()
        verifyBean.file( f )
        verifyBean.directory( f.getParentFile())
        verifyBean.directory( f.getParentFile().getParentFile())

        fileBean.delete( f )

        shouldFailAssert { verifyBean.file( f ) }

        verifyBean.directory( f.getParentFile())
        verifyBean.directory( f.getParentFile().getParentFile())
    }
}
