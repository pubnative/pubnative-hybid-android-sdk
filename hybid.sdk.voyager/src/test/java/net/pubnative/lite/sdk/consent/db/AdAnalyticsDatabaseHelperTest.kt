package net.pubnative.lite.sdk.consent.db

import net.pubnative.lite.sdk.consent.model.AdAnalytics
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class AdAnalyticsDatabaseHelperTest {

    private lateinit var adAnalytics1: AdAnalytics
    private lateinit var adAnalytics2: AdAnalytics
    private lateinit var adAnalytics3: AdAnalytics

    lateinit var database: DatabaseHelper

    @Before
    fun setup() {
        database = DatabaseHelper(RuntimeEnvironment.application)
        database.createTable(AdAnalytics::class)

        adAnalytics1 = AdAnalytics(impressions = 100, clicks = 10,
                time_between_impression_and_click = "", average_view_time = "",
                time_to_close = "", percentage_of_view = 30.0,
                percentage_before_skip = 20.0)

        adAnalytics2 = AdAnalytics(impressions = 100, clicks = 10,
                time_between_impression_and_click = "", average_view_time = "",
                time_to_close = "", percentage_of_view = 30.0,
                percentage_before_skip = 20.0)

        adAnalytics3 = AdAnalytics(impressions = 200, clicks = 10,
                time_between_impression_and_click = "", average_view_time = "",
                time_to_close = "", percentage_of_view = 40.0,
                percentage_before_skip = 20.0)
    }

    @Test
    fun `test insert ad analytics then return count`() {
        Assert.assertEquals(database.count(AdAnalytics::class), 0)
        database.insert(adAnalytics1)
        Assert.assertEquals(database.count(AdAnalytics::class), 1)
        database.insert(adAnalytics2)
        Assert.assertEquals(database.count(AdAnalytics::class), 2)
    }

    @Test
    fun `test update ad analytics then return count`() {
        Assert.assertEquals(database.count(AdAnalytics::class), 0)
        database.insert(adAnalytics1)
        database.insert(adAnalytics2)
        Assert.assertEquals(database.count(AdAnalytics::class), 2)
        val items = database.get(AdAnalytics::class)
        if (items != null) {
            items[0].clicks = 30
            items[1].clicks = 1300
            database.update(items)
        }
        Assert.assertEquals(database.count(AdAnalytics::class), 2)
    }

    @Test
    fun `test delete ad analytics then return count`() {
        Assert.assertEquals(database.count(AdAnalytics::class), 0)
        database.insert(adAnalytics1)
        database.insert(adAnalytics2)
        Assert.assertEquals(database.count(AdAnalytics::class), 2)
        val items = database.get(AdAnalytics::class)
        if (items != null) {
            database.delete(items)
            Assert.assertEquals(database.count(AdAnalytics::class), 0)
        }
    }

    @Test
    fun `test fetch ad analytics then return count`() {
        Assert.assertEquals(database.count(AdAnalytics::class), 0)
        database.insert(adAnalytics1)
        database.insert(adAnalytics2)
        val items = database.get(AdAnalytics::class)
        if (items != null) {
            Assert.assertEquals(database.count(AdAnalytics::class), 2)
        }
    }
}