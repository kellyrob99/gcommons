package com.goldin.gcommons.util

import com.goldin.gcommons.GCommons

/**
 * {@link com.goldin.gcommons.beans.GroovyBean#eval} configuration
 */
class GroovyConfig
{
    String   classpath
    String[] classpaths
    String[] classpaths () { GCommons.general().array( this.classpaths, this.classpath, String ) }

    boolean verbose        = true
    boolean verboseBinding = false
}
