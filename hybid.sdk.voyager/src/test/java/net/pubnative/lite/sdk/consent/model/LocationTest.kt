package net.pubnative.lite.sdk.consent.model

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LocationTest {

    private lateinit var location1: Location
    private lateinit var location2: Location
    private lateinit var location3: Location

    @Before
    fun setup() {
        location1 = Location(latitude = 29.2333, longitude = 31.343343, horizontal_accuracy = 0.1, connection_type = "wifi", session_ID = "")
        location2 = Location(latitude = 29.2333, longitude = 31.343343, horizontal_accuracy = 0.1, connection_type = "wifi", session_ID = "")
        location3 = Location(latitude = 29.2333, longitude = 31.3433, horizontal_accuracy = 0.1, connection_type = "3G", session_ID = "")
    }

    @Test
    fun `test two locations are equal`() {
        Assert.assertEquals(location1, location2)
    }

    @Test
    fun `test two locations are not equal`() {
        Assert.assertNotEquals(location1, location3)
    }
}