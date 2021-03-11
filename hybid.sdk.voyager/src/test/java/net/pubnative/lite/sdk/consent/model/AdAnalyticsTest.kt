package net.pubnative.lite.sdk.consent.model

import net.pubnative.lite.sdk.consent.db.Schema
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AdAnalyticsTest {

    private lateinit var adAnalytics1: AdAnalytics
    private lateinit var adAnalytics2: AdAnalytics
    private lateinit var adAnalytics3: AdAnalytics

    @Before
    fun setup() {
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
    fun `test two Ad analytics are equal`() {
        Assert.assertEquals(adAnalytics1, adAnalytics2)
    }

    @Test
    fun `test two Ad analytics are not equal`() {
        Assert.assertNotEquals(adAnalytics1, adAnalytics3)
    }
}