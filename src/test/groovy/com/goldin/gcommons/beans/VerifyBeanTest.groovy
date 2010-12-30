package com.goldin.gcommons.beans

import com.goldin.gcommons.BaseTest
import org.junit.Test
import static com.goldin.gcommons.Constants.*


 /**
 * {@link com.goldin.gcommons.beans.VerifyBean} tests
 */
class VerifyBeanTest extends BaseTest
{
    @Test
    void shouldVerifyExists()
    {
        verifyBean.exists( USER_DIR  )
        verifyBean.exists( USER_HOME )

        def f = fileBean.tempFile()
        verifyBean.exists( f )
        fileBean.delete( f )

        shouldFailAssert { verifyBean.exists( f ) }
        shouldFailAssert { verifyBean.exists( new File( "Doesn't exist" )) }
        shouldFailWith( NullPointerException.class ) { verifyBean.exists( new File( System.getProperty( "aaa" ))) }
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
        verifyBean.directory( USER_DIR  )
        verifyBean.directory( USER_HOME )

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
