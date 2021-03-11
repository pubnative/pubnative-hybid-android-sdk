package net.pubnative.lite.sdk.consent.model

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AdAnalyticsEventAggregatedTest {

    private lateinit var adAnalyticsEventAggregated1: AdAnalyticsEventAggregated
    private lateinit var adAnalyticsEventAggregated1_1: AdAnalyticsEventAggregated
    private lateinit var adAnalyticsEventAggregated2: AdAnalyticsEventAggregated
    private lateinit var adAnalyticsEventAggregated3: AdAnalyticsEventAggregated

    @Before
    fun setup() {
        adAnalyticsEventAggregated1 = AdAnalyticsEventAggregated(creative_id = 0, placement_id = 0, impressions = 10,
                clicks = 100, video_started = 23, video_finished = 300, video_dismissed = 10,
                video_view_time = 1000, interstitial_visible_time = 200, banner_visible_time = 400, video_muted = 10,
                video_unmuted = 30, time_to_click_html = 50, time_to_click_vast = 7, video_position_click = 23, video_position_dismiss = 9)
        adAnalyticsEventAggregated1_1 =AdAnalyticsEventAggregated(creative_id = 0, placement_id = 0, impressions = 10,
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
    fun `test two aggregated categories are equal`() {
        Assert.assertEquals(adAnalyticsEventAggregated1, adAnalyticsEventAggregated1_1)
    }

    @Test
    fun `test two aggregated categories are not equal`() {
        Assert.assertNotEquals(adAnalyticsEventAggregated1, adAnalyticsEventAggregated2)
        Assert.assertNotEquals(adAnalyticsEventAggregated2, adAnalyticsEventAggregated3)
    }
}