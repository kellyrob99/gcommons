package com.goldin.gcommons.beans

import com.goldin.gcommons.BaseTest
import org.junit.Test

/**
 * {@link NetBean} tests
 */
class NetBeanTest extends BaseTest
{

    @Test
    void shouldListFtpFile()
    {
        netBean.listFiles( 'ftp://cf_ftpuser:AllCl3@r@ftp.gem.tfn.com:/',
                           'SDI_Prod/Full/2.0/ThomsonReuters/EntityMaster.ThomsonReuters.Organization.*,        SDI_Prod/Full/2.0/ThomsonReuters/EntityMaster.Identification.Organization.PI.*, SDI_Prod/Full/2.0/ThomsonReuters/EntityMaster.Identification.Organization.TmtCompanyId.*, SDI_Prod/Full/2.0/ThomsonReuters/EntityMaster.Identification.Organization.Edcoid.*, SDI_Prod/Full/2.0/ThomsonReuters/EntityMaster.Identification.Organization.Cusip6.*, SDI_Prod/Full/2.0/ThomsonReuters/EntityMaster.Identification.Quotation.RIC.*, SDI_Prod/Full/2.0/ThomsonReuters/EntityMaster.Relationship.Organization.IsUltimateParentOf.*, SDI_Prod/Full/2.0/ThomsonReuters/EntityMaster.Relationship.Organization.IsParentOf.*, SDI_Prod/Full/2.0/ThomsonReuters/EntityMaster.Relationship.Organization.IsPrimaryTRBCSectorOf.*, SDI_Prod/Full/2.0/ThomsonReuters/EntityMaster.Relationship.Instrument.IsPrimarySecurityOf.*, SDI_Prod/Full/2.0/ThomsonReuters/EntityMaster.Relationship.Quotation.IsPrimaryQuoteOf.*' )

        netBean.listFiles( 'ftp://cf_ftpuser:AllCl3@r@ftp.gem.tfn.com:/',
                           'SDI_Prod/Incremental/2.0/ThomsonReuters/EntityMaster.ThomsonReuters.Organization.*, SDI_Prod/Incremental/2.0/ThomsonReuters/EntityMaster.Identification.Organization.PI.*, SDI_Prod/Incremental/2.0/ThomsonReuters/EntityMaster.Identification.Organization.TmtCompanyId.*, SDI_Prod/Incremental/2.0/ThomsonReuters/EntityMaster.Identification.Organization.Edcoid.*, SDI_Prod/Incremental/2.0/ThomsonReuters/EntityMaster.Identification.Organization.Cusip6.*, SDI_Prod/Incremental/2.0/ThomsonReuters/EntityMaster.Identification.Quotation.RIC.*, SDI_Prod/Incremental/2.0/ThomsonReuters/EntityMaster.Relationship.Organization.IsUltimateParentOf.*, SDI_Prod/Incremental/2.0/ThomsonReuters/EntityMaster.Relationship.Organization.IsParentOf.*, SDI_Prod/Incremental/2.0/ThomsonReuters/EntityMaster.Relationship.Organization.IsPrimaryTRBCSectorOf.*, SDI_Prod/Incremental/2.0/ThomsonReuters/EntityMaster.Relationship.Instrument.IsPrimarySecurityOf.*, SDI_Prod/Incremental/2.0/ThomsonReuters/EntityMaster.Relationship.Quotation.IsPrimaryQuoteOf.*' )

        netBean.listFiles( 'ftp://sdideals_ma:ma_321@10.82.64.97:/',
                           'mnt/sdi/deals/Deals/MA/COMPANIES/FULL/*.FULL.xml.zip,               mnt/sdi/deals/Deals/MA/FULL/*.FULL.xml.zip' )

        netBean.listFiles( 'ftp://sdideals_ma:ma_321@10.82.64.97:/',
                           'mnt/sdi/deals/Deals/MA/COMPANIES/INCREMENTAL/*.INCREMENTAL.xml.zip, mnt/sdi/deals/Deals/MA/INCREMENTAL/*.INCREMENTAL.xml.zip' )

        netBean.listFiles( 'ftp://calais:EXA71821@rkd.knowledge.reuters.com:/',
                           'OfficersDirectors03_GL_f_*.xml.zip, officersdirectors03_gl_f_*.xml.end' )

        netBean.listFiles( 'ftp://calais:EXA71821@rkd.knowledge.reuters.com:/',
                           'OfficersDirectors03_GL_i_*.xml.zip, officersdirectors03_gl_i_*.xml.end' )
    }
}
