package com.goldin.gcommons

import com.goldin.gcommons.api.General
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner


/**
 * {@link GCommons} tests
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( "/test-application-context.xml" )
class GeneralTest
{
    @Autowired
    General general

    @Test
    void shouldPack()
    {
        general.pack( null, null )
    }
}
