package com.goldin.gcommons

import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.goldin.gcommons.beans.*


/**
 * Base class for the tests
 */
class BaseTest
{
    /**
     * Initializing all beans
     */
    final ConstantsBean  constantsBean = GCommons.constants()
    final VerifyBean     verifyBean    = GCommons.verify()
    final GeneralBean    generalBean   = GCommons.general()
    final FileBean       fileBean      = GCommons.file()
    final NetBean        netBean       = GCommons.net()
    final GroovyBean     groovyBean    = GCommons.groovy()

    
    /**
     * Retrieves test archives map: name => unpacked size.
     * @return test archives map: name => unpacked size.
     */
    Map<String, Long> testArchives()
    {
        [ 'apache-maven-3.0.1' : 3344327L ] + ( System.getProperty( 'slowTests' ) ? [ 'gradle-0.9' : 27848286L ] :
                                                                                     Collections.emptyMap())
    }


    /**
     * Retrieves test resource specified.
     * 
     * @param path resource path
     * @return test resource specified
     */
    File testResource( String path ) { verifyBean.file( new File( 'src/test/resources', path )) }


    /**
     * Providing a public access to {@link GroovyTestCase#shouldFail(Class, Closure)}
     */
    static class MyGroovyTestCase extends GroovyTestCase
    {
        @Override
        public String shouldFail ( Class c, Closure code ) { super.shouldFail( c, code ) }

        @Test
        void testNothing(){} // Fails otherwise: "No tests found in com.goldin.gcommons.BaseTest$MyGroovyTestCase"
    }


    /**
     * {@link GroovyTestCase} wrappers
     */
    String shouldFailWith     ( Class cl, Closure c ) { new MyGroovyTestCase().shouldFail( cl, c ) }
    String shouldFailWithCause( Class cl, Closure c ) { new MyGroovyTestCase().shouldFailWithCause( cl, c ) }
    String shouldFailAssert   ( Closure c )           { new MyGroovyTestCase().shouldFail( AssertionError, c ) }


    /**
     * Retrieves test dir to be used for temporal output
     * @param dirName test directory name
     * @return test directory to use
     */
    File testDir( String dirName = System.currentTimeMillis() as String )
    {
        def caller  = ( StackTraceElement ) new Throwable().stackTrace.findAll { it.className.startsWith( 'com.goldin' ) }[ -1 ]
        def testDir = new File( "build/test/${ this.class.name }/${ caller.methodName }/$dirName" )
        fileBean.mkdirs( fileBean.delete( testDir ))
    }


    /**
     * Verifies both lists specified contain identical elements.
     * 
     * @param l1 first list to check
     * @param l2 second list to check
     */
    void assertSameLists( List l1, List l2 )
    {
        assert l1.size() == l2.size()
        assert l1.every { l2.contains( it ) }
        assert l2.every { l1.contains( it ) }
    }

    /**
     * Verifies both maps specified contain identical elements.
     *
     * @param m1 first map to check
     * @param m2 second map to check
     */
    void assertSameMaps( Map m1, Map m2 )
    {
        assert m1.size() == m2.size()
        assert m1.every{ key, value -> m2[ key ] == value }
        assert m2.every{ key, value -> m1[ key ] == value }
    }

    /**
     * Map of loggers for each bean
     */
    private static final Map<Class<? extends BaseTest>, Logger> LOGGERS = [:]

    /**
     * Retrieves logger for the bean class specified.
     * @param o bean class
     * @return logger to use
     */
    static Logger getLog( BaseTest test)
    {
        LOGGERS[ test.class ] = LOGGERS[ test.class ] ?: LoggerFactory.getLogger( test.class )
    }
}
