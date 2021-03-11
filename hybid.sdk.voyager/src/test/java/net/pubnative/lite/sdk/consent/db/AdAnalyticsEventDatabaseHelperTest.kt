package net.pubnative.lite.sdk.consent.db


import net.pubnative.lite.sdk.consent.model.AdAnalyticsEvent
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class AdAnalyticsEventDatabaseHelperTest {

    lateinit var database: DatabaseHelper

    private lateinit var adAnalyticsEvent1: AdAnalyticsEvent
    private lateinit var adAnalyticsEvent1_1: AdAnalyticsEvent
    private lateinit var adAnalyticsEvent2: AdAnalyticsEvent
    private lateinit var adAnalyticsEvent3: AdAnalyticsEvent

    @Before
    fun setup() {
        database = DatabaseHelper(RuntimeEnvironment.application)
        database.createTable(AdAnalyticsEvent::class)

        adAnalyticsEvent1 = AdAnalyticsEvent(placement_id = "placement_id1", event_type = "video_started",
                creative_type = "creative_type",
                ad_format = "ad_format",
                ad_size = "adSize",
                datetime = 843984934938,
                time_from_load = 0L,
                time_from_show = 0L,
                video_position = 0)

        adAnalyticsEvent1_1 = AdAnalyticsEvent(placement_id = "placement_id1_1", event_type = "video_started",
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
    fun `test insert AdAnalyticsEvent then return count`() {
        Assert.assertEquals(database.count(AdAnalyticsEvent::class), 0)
        database.insert(adAnalyticsEvent1)
        Assert.assertEquals(database.count(AdAnalyticsEvent::class), 1)
        database.insert(adAnalyticsEvent2)
        Assert.assertEquals(database.count(AdAnalyticsEvent::class), 2)
    }

    @Test
    fun `test update AdAnalyticsEvent then return count`() {
        Assert.assertEquals(database.count(AdAnalyticsEvent::class), 0)
        database.insert(adAnalyticsEvent1)
        val items = database.get(AdAnalyticsEvent::class)
        items?.let {
            Assert.assertEquals(database.count(AdAnalyticsEvent::class), 1)
            items[0].creative_type = "creative_type11111"
            database.update(items)
        }
    }

    @Test
    fun `test delete AdAnalyticsEvent from database`() {
        Assert.assertEquals(database.count(AdAnalyticsEvent::class), 0)
        database.insert(adAnalyticsEvent1)
        Assert.assertEquals(database.count(AdAnalyticsEvent::class), 1)
        database.insert(adAnalyticsEvent2)
        Assert.assertEquals(database.count(AdAnalyticsEvent::class), 2)
        val items = database.get(AdAnalyticsEvent::class)
        if (items != null)
            database.delete(items)
        Assert.assertEquals(database.count(AdAnalyticsEvent::class), 0)
    }

    @Test
    fun `test fetch AdAnalyticsEvent from database`() {
        Assert.assertEquals(database.count(AdAnalyticsEvent::class), 0)
        database.insert(adAnalyticsEvent1)
        Assert.assertEquals(database.count(AdAnalyticsEvent::class), 1)
        database.insert(adAnalyticsEvent3)
        val items = database.get(AdAnalyticsEvent::class)
        Assert.assertEquals(items?.size, 2)
        Assert.assertEquals(items?.get(0)?.creative_type, "creative_type")
        Assert.assertEquals(items?.get(1)?.creative_type, "creative_type3")
    }
}