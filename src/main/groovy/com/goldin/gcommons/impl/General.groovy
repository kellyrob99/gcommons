package com.goldin.gcommons.impl


/**
 * {@link com.goldin.gcommons.api.General} implementation
 */
class General implements com.goldin.gcommons.api.General
{

    @Override void unpack ( File archive, File directory )
    {
        println "unpack"
    }


    @Override void pack ( File archive, File directory )
    {
        println "pack"
    }
}
