package net.pubnative.lite.sdk.consent.db

import net.pubnative.lite.sdk.consent.model.AdAnalyticsEventAggregated
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class AdAnalyticsEventAggregatedDatabaseHelperTest {

    lateinit var database: DatabaseHelper

    private lateinit var adAnalyticsEventAggregated1: AdAnalyticsEventAggregated
    private lateinit var adAnalyticsEventAggregated11: AdAnalyticsEventAggregated
    private lateinit var adAnalyticsEventAggregated2: AdAnalyticsEventAggregated
    private lateinit var adAnalyticsEventAggregated3: AdAnalyticsEventAggregated

    @Before
    fun setup() {
        database = DatabaseHelper(RuntimeEnvironment.application)
        database.createTable(AdAnalyticsEventAggregated::class)

        adAnalyticsEventAggregated1 = AdAnalyticsEventAggregated(creative_id = 0, placement_id = 0, impressions = 10,
                clicks = 100, video_started = 23, video_finished = 300, video_dismissed = 10,
                video_view_time = 1000, interstitial_visible_time = 200, banner_visible_time = 400, video_muted = 10,
                video_unmuted = 30, time_to_click_html = 50, time_to_click_vast = 7, video_position_click = 23, video_position_dismiss = 9)
        adAnalyticsEventAggregated11 = AdAnalyticsEventAggregated(creative_id = 0, placement_id = 0, impressions = 10,
                clicks = 100, video_started = 23, video_finished = 300, video_dismissed = 10,
                video_view_time = 1000, interstitial_visible_time = 200, banner_visible_time = 400, video_muted = 10,
                video_unmuted = 30, time_to_click_html = 50, time_to_click_vast = 7, video_position_click = 23, video_position_dismiss = 9)

        adAnalyticsEventAggregated2 = AdAnalyticsEventAggregated(creative_id = 0, placement_id = 0, impressions = 10,
                clicks = 100, video_started = 23, video_finished = 300, video_dismissed = 10,
                video_view_time = 1000, interstitial_visible_time = 100, banner_visible_time = 400, video_muted = 10,
                video_unmuted = 30, time_to_click_html = 30, time_to_click_vast = 17, video_position_click = 23, video_position_dismiss = 9)

        adAnalyticsEventAggregated3 = AdAnalyticsEventAggregated(creative_id = 0, placement_id = 0, impressions = 10,
                clicks = 100, video_started = 23, video_finished = 300, video_dismissed = 10,
                video_view_time = 1000, interstitial_visible_time = 200, banner_visible_time = 400, video_muted = 10,
                video_unmuted = 30, time_to_click_html = 800, time_to_click_vast = 17, video_position_click = 29, video_position_dismiss = 9)
    }

    @Test
    fun `test insert AdAnalyticsEventAggregated then return count`() {
        Assert.assertEquals(database.count(AdAnalyticsEventAggregated::class), 0)

        database.insert(adAnalyticsEventAggregated1)
        Assert.assertEquals(database.count(AdAnalyticsEventAggregated::class), 1)
        database.insert(adAnalyticsEventAggregated2)
        Assert.assertEquals(database.count(AdAnalyticsEventAggregated::class), 2)
    }

    @Test
    fun `test update AdAnalyticsEventAggregated to database`() {
        Assert.assertEquals(database.count(AdAnalyticsEventAggregated::class), 0)

        database.insert(adAnalyticsEventAggregated1)
        database.insert(adAnalyticsEventAggregated2)

        Assert.assertEquals(database.count(AdAnalyticsEventAggregated::class), 2)

        val items = database.get(AdAnalyticsEventAggregated::class)
        if (items != null) {
            items[0].impressions = 12
            items[1].impressions = 90
            database.update(items)
            Assert.assertEquals(database.count(AdAnalyticsEventAggregated::class), 2)
        }
    }

    @Test
    fun `test delete AdAnalyticsEventAggregated then return count`() {
        Assert.assertEquals(database.count(AdAnalyticsEventAggregated::class), 0)

        database.insert(adAnalyticsEventAggregated1)
        database.insert(adAnalyticsEventAggregated2)

        Assert.assertEquals(database.count(AdAnalyticsEventAggregated::class), 2)

        val items = database.get(AdAnalyticsEventAggregated::class)
        if (items != null)
            database.delete(items)

        Assert.assertEquals(database.count(AdAnalyticsEventAggregated::class), 0)
    }

    @Test
    fun `test fetch aggregated AdAnalyticsEventAggregated items from database`() {
        Assert.assertEquals(database.count(AdAnalyticsEventAggregated::class), 0)
        database.insert(adAnalyticsEventAggregated1)
        Assert.assertEquals(database.count(AdAnalyticsEventAggregated::class), 1)
        database.insert(adAnalyticsEventAggregated3)

        val items = database.get(AdAnalyticsEventAggregated::class)

        Assert.assertEquals(items?.size, 2)

        Assert.assertEquals(items?.get(0)?.clicks, 100)
        Assert.assertEquals(items?.get(0)?.impressions, 10)
    }
}