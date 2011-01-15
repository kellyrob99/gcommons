package com.goldin.gcommons.beans

import com.goldin.gcommons.GCommons

/**
 * {@link GroovyBean#eval} configuration
 */
class GroovyConfig
{
    String   classpath
    String[] classpaths
    String[] classpaths () { GCommons.general().array( this.classpaths, this.classpath, String ) }

    boolean verbose        = true
    boolean verboseBinding = false
}
