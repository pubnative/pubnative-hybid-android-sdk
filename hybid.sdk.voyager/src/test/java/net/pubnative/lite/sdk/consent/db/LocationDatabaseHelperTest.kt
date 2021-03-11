package net.pubnative.lite.sdk.consent.db

import net.pubnative.lite.sdk.consent.model.Location
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class LocationDatabaseHelperTest {

    private lateinit var location1: Location
    private lateinit var location2: Location
    private lateinit var location3: Location
    lateinit var database: DatabaseHelper

    @Before
    fun setup() {
        database = DatabaseHelper(RuntimeEnvironment.application)
        database.createTable(Location::class)

        location1 = Location(latitude = 29.2333, longitude = 31.343343, horizontal_accuracy = 0.1, connection_type = "wifi", session_ID = "")
        location2 = Location(latitude = 29.2333, longitude = 31.343343, horizontal_accuracy = 0.1, connection_type = "wifi", session_ID = "")
        location3 = Location(latitude = 29.2333, longitude = 31.3433, horizontal_accuracy = 0.1, connection_type = "3G", session_ID = "")
    }

    @Test
    fun `test insert location then return count`() {
        Assert.assertEquals(database.count(Location::class), 0)
        database.insert(location1)
        Assert.assertEquals(database.count(Location::class), 1)
        database.insert(location2)
        Assert.assertEquals(database.count(Location::class), 2)
    }

    @Test
    fun `test update location then return count`() {
        Assert.assertEquals(database.count(Location::class), 0)
        database.insert(location1)
        Assert.assertEquals(database.count(Location::class), 1)
        location1.connection_type = "3G"
        database.update(location1)
        Assert.assertEquals(database.count(Location::class), 1)
    }

    @Test
    fun `test delete location then return count`() {
        Assert.assertEquals(database.count(Location::class), 0)
        database.insert(location1)
        Assert.assertEquals(database.count(Location::class), 1)
        val items = database.get(Location::class)
        if (items != null)
            database.delete(items)
        Assert.assertEquals(database.count(Location::class), 0)
    }
}