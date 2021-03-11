package net.pubnative.lite.sdk.consent.db

import net.pubnative.lite.sdk.consent.model.Visit
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class VisitDataHelperTest {

    private lateinit var visit1: Visit
    private lateinit var visit2: Visit
    private lateinit var visit3: Visit

    lateinit var database: DatabaseHelper

    @Before
    fun setup() {

        database = DatabaseHelper(RuntimeEnvironment.application)
        database.createTable(Visit::class)

        visit1 = Visit(
                session_id = "Session1",
                start_time = 1134332,
                end_time = 12323232,
                cluster_longitude = 29.232321,
                cluster_latitude = 31.434332,
                distance = 10000.0,
                usability_score = 31.0,
                final_score = 1130.2323232)

        visit2 = Visit(
                session_id = "Session2",
                start_time = 1134434332,
                end_time = 123243423232,
                cluster_longitude = 29.232321,
                cluster_latitude = 31.434332,
                distance = 10000.0,
                usability_score = 35.0,
                final_score = 1130.2323232)

        visit3 = Visit(session_id = "Session3",
                start_time = 1134332,
                end_time = 12323232,
                cluster_longitude = 29.2323217838943,
                cluster_latitude = 31.434332,
                distance = 10000.0,
                usability_score = 311.0,
                final_score = 1130.2323232)
    }


    @Test
    fun `test insert Visit then return count`() {
        Assert.assertEquals(database.count(Visit::class), 0)
        database.insert(visit1)
        Assert.assertEquals(database.count(Visit::class), 1)
        database.insert(visit2)
        Assert.assertEquals(database.count(Visit::class), 2)
    }

    @Test
    fun `test update Visit then return count`() {
        Assert.assertEquals(database.count(Visit::class), 0)
        database.insert(visit1)
        val items = database.get(Visit::class)
        items?.let {
            Assert.assertEquals(database.count(Visit::class), 1)
            items[0].start_time = 1234343
            database.update(items)
            Assert.assertEquals(database.count(Visit::class), 1)
        }
    }

    @Test
    fun `test delete Visit from database`() {
        Assert.assertEquals(database.count(Visit::class), 0)
        database.insert(visit1)
        Assert.assertEquals(database.count(Visit::class), 1)
        database.insert(visit2)
        Assert.assertEquals(database.count(Visit::class), 2)
        val items = database.get(Visit::class)
        if (items != null)
            database.delete(items)
        Assert.assertEquals(database.count(Visit::class), 0)
    }

    @Test
    fun `test fetch Visit from database`() {
        Assert.assertEquals(database.count(Visit::class), 0)
        database.insert(visit1)
        Assert.assertEquals(database.count(Visit::class), 1)
        database.insert(visit3)
        val items = database.get(Visit::class)
        Assert.assertEquals(items?.size, 2)
        Assert.assertEquals(items?.get(0)?.cluster_longitude, 29.232321)
        Assert.assertEquals(items?.get(1)?.cluster_longitude, 29.2323217838943)
    }
}