package net.pubnative.lite.sdk.consent.db

import net.pubnative.lite.sdk.consent.model.POI
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class POIDataHelperTest {

    lateinit var database: DatabaseHelper

    private lateinit var poi1: POI
    private lateinit var poi2: POI
    private lateinit var poi3: POI

    @Before
    fun setup() {

        database = DatabaseHelper(RuntimeEnvironment.application)
        database.createTable(POI::class)

        poi1 = POI(
                name = "POI1",
                place_taxonomy2_id = 11,
                place_taxonomy3_id = 12,
                zipcode = "zip_code1",
                zipcode_suffix = "zip_code_suffix1",
                country = "Germany",
                latitude = 31.3343232,
                longitude = 30.2323232,
                is_active = true)

        poi2 = POI(
                name = "POI2",
                place_taxonomy2_id = 1122,
                place_taxonomy3_id = 1322,
                zipcode = "zip_code2",
                zipcode_suffix = "zip_code_suffix1",
                country = "Germany",
                latitude = 31.3343232,
                longitude = 30.2323232,
                is_active = true)

        poi3 = POI(
                name = "POI3",
                place_taxonomy2_id = 112232,
                place_taxonomy3_id = 134322,
                zipcode = "zip_code3",
                zipcode_suffix = "zip_code_suffix3",
                country = "Spain",
                latitude = 31.3343232,
                longitude = 30.2323232,
                is_active = true)
    }


    @Test
    fun `test insert POI then return count`() {
        Assert.assertEquals(database.count(POI::class), 0)
        database.insert(poi1)
        Assert.assertEquals(database.count(POI::class), 1)
        database.insert(poi2)
        Assert.assertEquals(database.count(POI::class), 2)
    }

    @Test
    fun `test update POI then return count`() {
        Assert.assertEquals(database.count(POI::class), 0)
        database.insert(poi1)
        val items = database.get(POI::class)
        items?.let {
            Assert.assertEquals(database.count(POI::class), 1)
            items[0].country = "USA"
            database.update(items)
            Assert.assertEquals(database.count(POI::class), 1)
        }
    }

    @Test
    fun `test delete POI from database`() {
        Assert.assertEquals(database.count(POI::class), 0)
        database.insert(poi1)
        Assert.assertEquals(database.count(POI::class), 1)
        database.insert(poi2)
        Assert.assertEquals(database.count(POI::class), 2)
        val items = database.get(POI::class)
        if (items != null)
            database.delete(items)
        Assert.assertEquals(database.count(POI::class), 0)
    }

    @Test
    fun `test fetch POI from database`() {
        Assert.assertEquals(database.count(POI::class), 0)
        database.insert(poi1)
        Assert.assertEquals(database.count(POI::class), 1)
        database.insert(poi3)
        val items = database.get(POI::class)
        Assert.assertEquals(items?.size, 2)
        Assert.assertEquals(items?.get(0)?.country, "Germany")
        Assert.assertEquals(items?.get(1)?.country, "Spain")
    }
}