package net.pubnative.lite.sdk.consent.model

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class POITest {

    private lateinit var poi1: POI
    private lateinit var poi2: POI
    private lateinit var poi3: POI

    @Before
    fun setup() {
        poi1 = POI(1,
                "POI1",
                11,
                12,
                "zip_code1",
                "zip_code_suffix1",
                "Germany",
                31.3343232,
                30.2323232, true)

        poi2 = POI(1,
                "POI1",
                11,
                12,
                "zip_code1",
                "zip_code_suffix1",
                "Germany",
                31.3343232,
                30.2323232, true)

        poi3 = POI(1,
                "POI3",
                11,
                20,
                "zip_code3",
                "zip_code_suffix3",
                "Egypt",
                31.3343232,
                29.32323030,
                false)
    }

    @Test
    fun `test two POIs are equal`() {
        Assert.assertEquals(poi1, poi2)
    }

    @Test
    fun `test two POIs are not equal`() {
        Assert.assertNotEquals(poi1, poi3)
    }
}