package com.goldin.gcommons

import de.schlichtherle.io.DefaultArchiveDetector
import de.schlichtherle.io.archive.spi.ArchiveDriver


/**
 * {@link DefaultArchiveDetector} extension detecting only the original archive.
 * 
 * Used in {@link com.goldin.gcommons.beans.FileBean#unpack(File, File)} so that
 * only the original archive is processed by TrueZip and if archive being unpacked
 * contains other archives - they are not detected as archives and are not processed by TrueZip.
 *
 * Otherwise, nested archives are repacked (I think) and their original size is modified
 * which makes comparing contents impossible.
 */
class SingleFileArchiveDetector extends DefaultArchiveDetector
{
    String archivePath

    SingleFileArchiveDetector ( File archive, String extension )
    {
        super( GCommons.verify().notNullOrEmpty( extension ))
        archivePath = normalizePath( GCommons.verify().file( archive ).path )
    }

    
    private static String normalizePath( String path ){ path.replaceAll( /\\+/, '/' ).toLowerCase() }

    
    @Override
    public ArchiveDriver getArchiveDriver ( String path )
    {
        ( normalizePath( path ).endsWith( archivePath )) ? super.getArchiveDriver( path ) :
                                                           null
    }
}
