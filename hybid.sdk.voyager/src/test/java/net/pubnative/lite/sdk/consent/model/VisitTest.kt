package net.pubnative.lite.sdk.consent.model

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class VisitTest {

    private lateinit var visit1: Visit
    private lateinit var visit2: Visit
    private lateinit var visit3: Visit

    @Before
    fun setup() {
        visit1 = Visit(1,
                "Session1",
                1134332,
                12323232,
                29.232321,
                31.434332,
                10000.0,
                31.0,
                1130.2323232)

        visit2 = Visit(1,
                "Session1",
                1134332,
                12323232,
                29.232321,
                31.434332,
                10000.0,
                31.0,
                1130.2323232)

        visit3 = Visit(3,
                "Session3",
                1134332,
                12323232,
                29.232321,
                31.434332,
                10000.0,
                31.0,
                1130.2323232)
    }

    @Test
    fun `test two Visits are equal`() {
        Assert.assertEquals(visit1, visit2)
    }

    @Test
    fun `test two Visits are not equal`() {
        Assert.assertNotEquals(visit1, visit3)
    }
}