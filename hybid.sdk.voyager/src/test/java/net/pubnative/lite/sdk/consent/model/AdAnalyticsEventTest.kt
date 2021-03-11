package net.pubnative.lite.sdk.consent.model

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AdAnalyticsEventTest {

    private lateinit var adAnalyticsEvent1: AdAnalyticsEvent
    private lateinit var adAnalyticsEvent1_1: AdAnalyticsEvent
    private lateinit var adAnalyticsEvent2: AdAnalyticsEvent
    private lateinit var adAnalyticsEvent3: AdAnalyticsEvent

    @Before
    fun setup() {
        adAnalyticsEvent1 = AdAnalyticsEvent(placement_id = "placement_id1", event_type = "video_started",
                creative_type = "creative_type",
                ad_format = "ad_format",
                ad_size = "adSize",
                datetime = 843984934938,
                time_from_load = 0L,
                time_from_show = 0L,
                video_position = 0)

        adAnalyticsEvent1_1 = AdAnalyticsEvent(placement_id = "placement_id1", event_type = "video_started",
                creative_type = "creative_type",
                ad_format = "ad_format",
                ad_size = "adSize",
                datetime = 843984934938,
                time_from_load = 0L,
                time_from_show = 0L,
                video_position = 0)

        adAnalyticsEvent2 = AdAnalyticsEvent(placement_id = "placement_id2", event_type = "video_muted",
                creative_type = "creative_type2",
                ad_format = "ad_format2",
                ad_size = "adSize2",
                datetime = System.currentTimeMillis(),
                time_from_load = 0L,
                time_from_show = 0L,
                video_position = 0)

        adAnalyticsEvent3 = AdAnalyticsEvent(placement_id = "placement_id1", event_type = "video_dismissed",
                creative_type = "creative_type3",
                ad_format = "ad_format3",
                ad_size = "adSize3",
                datetime = System.currentTimeMillis(),
                time_from_load = 0L,
                time_from_show = 0L,
                video_position = 0)
    }

    @Test
    fun `test two ad analytics events are equal`() {
        Assert.assertEquals(adAnalyticsEvent1, adAnalyticsEvent1_1)
    }

    @Test
    fun `test two ad analytics events are not equal`() {
        Assert.assertNotEquals(adAnalyticsEvent1, adAnalyticsEvent2)
        Assert.assertNotEquals(adAnalyticsEvent2, adAnalyticsEvent3)
    }
}