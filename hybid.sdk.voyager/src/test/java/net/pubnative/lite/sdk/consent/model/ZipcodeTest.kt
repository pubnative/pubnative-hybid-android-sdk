package net.pubnative.lite.sdk.consent.model

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ZipcodeTest {

    private lateinit var zipcode1: Zipcode
    private lateinit var zipcode2: Zipcode
    private lateinit var zipcode3: Zipcode

    @Before
    fun setup() {
        zipcode1 = Zipcode(
                0,
                "Zipcode1",
                "zipcode_suffix1",
                "date_created1",
                "date_updated1",
                12)

        zipcode2 = Zipcode(
                0,
                "Zipcode1",
                "zipcode_suffix1",
                "date_created1",
                "date_updated1",
                12)

        zipcode3 = Zipcode(
                0,
                "Zipcode3",
                "zipcode_suffix3",
                "date_created3",
                "date_updated3",
                25)
    }


    @Test
    fun `test two Zipcodes are equal`() {
        Assert.assertEquals(zipcode1, zipcode2)
    }

    @Test
    fun `test two Zipcodes are not equal`() {
        Assert.assertNotEquals(zipcode1, zipcode3)
    }
}