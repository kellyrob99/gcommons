package com.goldin.gcommons.beans

import org.codehaus.groovy.control.CompilerConfiguration
import com.goldin.gcommons.util.GroovyConfig

/**
 * Groovy-related helper methods.
 */
class GroovyBean extends BaseBean
{

    /**
     * Set by Spring
     */
    GeneralBean general


    /**
     * Evaluates Groovy expression provided and casts it to the class specified.
     *
     * @param expression   Groovy expression to evaluate, if null or empty - null is returned
     * @param resultType   result's type,
     *                     if <code>null</code> - no verification is made for result's type and <code>null</code>
     *                     value is allowed to be returned from eval()-ing the expression
     * @param binding      binding to use,
     *                     if <code>null</code> - no binding is specified when creating {@link groovy.lang.GroovyShell}
     * @param config       {@link GroovyConfig} object to use, allowed to be <code>null</code>
     *
     * @param <T>        result's type
     * @return           expression evaluated and casted to the type specified
     *                   (after verifying compliance with {@link Class#isInstance(Object)}
     */
    public <T> T eval ( String       expression,
                        Class<T>     resultType   = null,
                        Binding      binding      = null,
                        GroovyConfig config       = null )
    {
        if (( ! expression ) || ( expression.trim().length() < 1 ))
        {
            return null
        }

        if (( expression.startsWith( '{{' )) && ( expression.endsWith( '}}' )))
        {
            expression = expression.replace( '{{', '' ).replace( '}}', '' )
        }

        expression = expression.trim()

        CompilerConfiguration cc = new CompilerConfiguration()

        if ( config )
        {
            cc.setClasspathList( config.classpaths().toList())
        }

        if ( config?.verboseBinding )
        {
            getLog( this ).info( "Groovy: evaluating [$expression] with ${ binding ? """following binding: ${binding.variables}""" : 'empty binding' }" )
        }

        GroovyShell shell = ( binding ? new GroovyShell( binding, cc ) : new GroovyShell( cc ))
        Object      value = shell.evaluate( expression )

        assert (( resultType == null ) || ( value != null )), "Result of Groovy expression [$expression] is null"

        /**
         * If result type requested is String - we get a String value of the object (regardless of its type)
         * Otherwise, we verify that result's type matches the one requested
         */

        Class valueType = (( value != null ) ? value.getClass() : null )

        if ( String.class == resultType )
        {
            value = String.valueOf( value )
        }
        else
        {
            assert (( resultType == null ) || ( resultType.isInstance( value ))), \
                   "Result of Groovy expression [$expression] is [$value] - an instanceof [$valueType] instead of [$resultType]"
        }

        if ( config?.verbose )
        {
            getLog( this ).info( "Groovy: [$expression] => [$value] (type: [${( value != null ) ? value.class.name : null }])" )
        }

        (( T ) value )
    }


    /**
     * Creates a Groovy {@link groovy.lang.Binding} instance (to be used for
     * {@link #eval(String, Class<T>, Binding, GroovyConfig)} call) using pairs of bindings provided.
     *
     * @param bindingObjects pairs of object to copy to result binding:
     *        {@code "propertyName", propertyValue, "anotherPropertyName", anotherValue, ... }
     *
     * @return {@link groovy.lang.Binding} instance created
     */
    Binding binding( Map map = [:], Object ... bindingObjects )
    {
        verify.notNull( map )
        def bindingMap = new HashMap( map )

        if ( bindingObjects )
        {
            assert (( bindingObjects.size() % 2 ) == 0 ), \
                   "[${ bindingObjects.size() }] binding objects specified - should be even number"

            for ( def j = 0; j < bindingObjects.size(); j += 2 )
            {
                bindingMap[ bindingObjects[ j ]] = bindingObjects[ j + 1 ]
            }
        }

        new Binding( fixNames( bindingMap ))
    }


    /**
     * {@link #binding(Map, Object[])} helper - fixes names in a Map specified
     * by creating a new Map with all previous keys being Groovy-normalized.
     *
     * @param map properties to read
     */
    private Map<String, Object> fixNames( Map<String, Object> map )
    {
        verify.notNull( map )

        Map<String, Object> result      = [:]
        def                 isForbidden = { ( it == '.' ) || ( it == '-' ) }

        map.each {

            String propertyName, Object propertyValue ->

            if ( propertyName.toCharArray().any( isForbidden ))
            {
                StringBuilder fixedPropertyName = new StringBuilder()
                boolean       capitalize        = false

                for ( ch in propertyName.toCharArray())
                {
                    if ( isForbidden( ch ))
                    {
                        capitalize = true
                    }
                    else
                    {
                        fixedPropertyName << (( capitalize && fixedPropertyName.length()) ? ch.toUpperCase() : ch )
                        capitalize = false
                    }
                }

                propertyName = fixedPropertyName.toString()
            }

            result[ propertyName ] = propertyValue
        }

        result
    }

}
