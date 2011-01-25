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
    void shouldRetrieveBeans()
    {
        assert GCommons.constants()
        assert GCommons.constants( false )
        assert GCommons.constants( true )

        assert GCommons.verify()
        assert GCommons.verify( false )
        assert GCommons.verify( true )

        assert GCommons.general()
        assert GCommons.general( false )
        assert GCommons.general( true )

        assert GCommons.file()
        assert GCommons.file( false )
        assert GCommons.file( true )

        assert GCommons.io()
        assert GCommons.io( false )
        assert GCommons.io( true )

        assert GCommons.net()
        assert GCommons.net( false )
        assert GCommons.net( true )

    }

    @Test
    void shouldRefresh()
    {
        assert GCommons.general() == GCommons.general()
        assert GCommons.general() == GCommons.general( false )

        assert GCommons.verify() == GCommons.verify()
        assert GCommons.verify() == GCommons.verify( false )

// http://evgeny-goldin.org/youtrack/issue/gc-9
//        assert GCommons.general() != GCommons.general( true  )
//        assert GCommons.verify()  != GCommons.verify( true  )
//        assert GCommons.net()     != GCommons.net( true  )
    }
}
