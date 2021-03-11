package net.pubnative.lite.sdk.consent.db

import net.pubnative.lite.sdk.consent.model.Zipcode
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ZipcodeDataHelperTest {

    private lateinit var zipcode1: Zipcode
    private lateinit var zipcode2: Zipcode
    private lateinit var zipcode3: Zipcode

    lateinit var database: DatabaseHelper

    @Before
    fun setup() {

        database = DatabaseHelper(RuntimeEnvironment.application)
        database.createTable(Zipcode::class)

        zipcode1 = Zipcode(
                zipcode = "Zipcode1",
                zipcode_suffix = "zipcode_suffix1",
                date_created = "date_created1",
                date_updated = "date_updated1",
                poi_count = 12)

        zipcode2 = Zipcode(
                zipcode = "Zipcode2",
                zipcode_suffix = "zipcode_suffix2",
                date_created = "date_created2",
                date_updated = "date_updated2",
                poi_count = 20)

        zipcode3 = Zipcode(
                zipcode = "Zipcode3",
                zipcode_suffix = "zipcode_suffix3",
                date_created = "date_created3",
                date_updated = "date_updated3",
                poi_count = 25)

    }


    @Test
    fun `test insert Zipcode then return count`() {
        Assert.assertEquals(database.count(Zipcode::class), 0)
        database.insert(zipcode1)
        Assert.assertEquals(database.count(Zipcode::class), 1)
        database.insert(zipcode2)
        Assert.assertEquals(database.count(Zipcode::class), 2)
    }

    @Test
    fun `test update Zipcode then return count`() {
        Assert.assertEquals(database.count(Zipcode::class), 0)
        database.insert(zipcode1)
        val items = database.get(Zipcode::class)
        items?.let {
            Assert.assertEquals(database.count(Zipcode::class), 1)
            items[0].poi_count = 123
            database.update(items)
            Assert.assertEquals(database.count(Zipcode::class), 1)
        }
    }

    @Test
    fun `test delete Zipcode from database`() {
        Assert.assertEquals(database.count(Zipcode::class), 0)
        database.insert(zipcode1)
        Assert.assertEquals(database.count(Zipcode::class), 1)
        database.insert(zipcode2)
        Assert.assertEquals(database.count(Zipcode::class), 2)
        val items = database.get(Zipcode::class)
        if (items != null)
            database.delete(items)
        Assert.assertEquals(database.count(Zipcode::class), 0)
    }

    @Test
    fun `test fetch Zipcode from database`() {
        Assert.assertEquals(database.count(Zipcode::class), 0)
        database.insert(zipcode1)
        Assert.assertEquals(database.count(Zipcode::class), 1)
        database.insert(zipcode3)
        val items = database.get(Zipcode::class)
        Assert.assertEquals(items?.size, 2)
        Assert.assertEquals(items?.get(0)?.poi_count, 12)
        Assert.assertEquals(items?.get(1)?.poi_count, 25)
    }
}