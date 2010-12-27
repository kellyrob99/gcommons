package com.goldin.gcommons

import org.junit.Test

/**
 * {@link GCommons} entry points test
 */
class GCommonsTest extends BaseTest
{
    @Test
    void shouldRefreshContext()
    {
        assert GCommons.context() == GCommons.context()
        assert GCommons.context() == GCommons.context( false )
        assert GCommons.context() != GCommons.context( true  )
    }


    @Test
    void shouldRefreshGeneral()
    {
        assert GCommons.general() == GCommons.general()
        assert GCommons.general() == GCommons.general( false )
        assert GCommons.general() != GCommons.general( true  )
    }


    @Test
    void shouldRefreshVerify()
    {
        assert GCommons.verify() == GCommons.verify()
        assert GCommons.verify() == GCommons.verify( false )
        assert GCommons.verify() != GCommons.verify( true  )
    }
}
