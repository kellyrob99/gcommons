package com.goldin.gcommons

import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
 * Base class for the tests
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( "/application-context.xml" )
abstract class BaseTest
{
    @Autowired
    General general

    @Autowired
    Verify verify


    File tempFile() { File.createTempFile( BaseTest.class.name, '' ) }
}
