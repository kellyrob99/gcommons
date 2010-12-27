package com.goldin.gcommons

import org.junit.Test

/**
 * {@link Verify} tests
 *
 */
class VerifyTest extends BaseTest
{
    @Test
    void shouldVerifyEqualFiles()
    {
        def f1   = tempFile()
        def f2   = tempFile()
        def data = System.currentTimeMillis() as String

        f1.write( data * 10 )
        f2.write( data * 10 )

        verify.verifyEqual( f1, f2 )
    }
}
