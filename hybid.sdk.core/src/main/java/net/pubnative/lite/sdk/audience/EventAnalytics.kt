package net.pubnative.lite.sdk.audience

import android.content.Context
import net.pubnative.lite.sdk.consent.db.DatabaseHelper
import net.pubnative.lite.sdk.consent.model.AdAnalyticsEvent
import net.pubnative.lite.sdk.consent.model.AdAnalyticsEventAggregated
import net.pubnative.lite.sdk.consent.model.Location
import net.pubnative.lite.sdk.models.Ad

class EventAnalytics(val context: Context?) {

    private var databaseHelper: DatabaseHelper? = null

    init {
        buildHybidDb(context)
    }

    private fun buildHybidDb(context: Context?) {
        context?.let {
            databaseHelper = DatabaseHelper(context)
            databaseHelper?.let {
                it.createTable(AdAnalyticsEvent::class)
                it.createTable(AdAnalyticsEventAggregated::class)
                it.createTable(Location::class)
            }
        }
    }

    fun fireVideoStartedEvent(ad: Ad?, showTime: Long, creative_type: String?, adFormat: String, adSize: String) {
        val placement = AdAnalyticsEvent(placement_id = ad?.zoneId, event_type = "video_started",
                creative_type = creative_type,
                ad_format = adFormat,
                ad_size = adSize,
                datetime = System.currentTimeMillis(),
                time_from_load = 0L,
                time_from_show = getTimeDifference(showTime),
                video_position = 0)

        databaseHelper?.insert(placement)
    }

    fun fireVideoDismissedEvent(ad: Ad?, showTime: Long, creative_type: String?, adFormat: String, adSize: String) {
        val placement = AdAnalyticsEvent(placement_id = ad?.zoneId, event_type = "video_dismissed",
                creative_type = creative_type,
                ad_format = adFormat,
                ad_size = adSize,
                datetime = System.currentTimeMillis(),
                time_from_load = 0L,
                time_from_show = getTimeDifference(showTime),
                video_position = 0)

        databaseHelper?.insert(placement)
    }

    fun fireVideoFinishedEvent(ad: Ad?, showTime: Long, creative_type: String?, adFormat: String, adSize: String) {

        val placement = AdAnalyticsEvent(placement_id = ad?.zoneId, event_type = "video_finished",
                creative_type = creative_type,
                ad_format = adFormat,
                ad_size = adSize,
                datetime = System.currentTimeMillis(),
                time_from_load = 0L,
                time_from_show = getTimeDifference(showTime),
                video_position = 0)

        databaseHelper?.insert(placement)
    }

    fun fireClickEvent(ad: Ad?, showTime: Long, creative_type: String?, adFormat: String, adSize: String) {
        val placement = AdAnalyticsEvent(placement_id = ad?.zoneId, event_type = "click",
                creative_type = creative_type,
                ad_format = adFormat,
                ad_size = adSize,
                datetime = System.currentTimeMillis(),
                time_from_load = 0L,
                time_from_show = getTimeDifference(showTime),
                video_position = 0)

        databaseHelper?.insert(placement)
    }

    fun fireImpressionEvent(ad: Ad?, showTime: Long, creative_type: String?, adFormat: String, adSize: String) {
        val placement = AdAnalyticsEvent(placement_id = ad?.zoneId, event_type = "impression",
                creative_type = creative_type,
                ad_format = adFormat,
                ad_size = adSize,
                datetime = System.currentTimeMillis(),
                time_from_load = 0L,
                time_from_show = getTimeDifference(showTime),
                video_position = 0)

        databaseHelper?.insert(placement)
    }

    private fun getTimeDifference(showTime: Long): Long {
        return System.currentTimeMillis() - showTime
    }

    fun fireMuteEvent(ad: Ad?, showTime: Long, videoPosition: Int?, creative_type: String?, adFormat: String, adSize: String) {
        val placement = AdAnalyticsEvent(placement_id = ad?.zoneId, event_type = "video_mute",
                creative_type = creative_type,
                ad_format = adFormat,
                ad_size = adSize,
                datetime = System.currentTimeMillis(),
                time_from_load = 0L,
                time_from_show = getTimeDifference(showTime),
                video_position = videoPosition)

        databaseHelper?.insert(placement)
    }

    fun fireUnMuteEvent(ad: Ad?, showTime: Long, videoPosition: Int?, creative_type: String?, adFormat: String, adSize: String) {
        val placement = AdAnalyticsEvent(placement_id = ad?.zoneId, event_type = "video_unmute",
                creative_type = creative_type,
                ad_format = adFormat,
                ad_size = adSize,
                datetime = System.currentTimeMillis(),
                time_from_load = 0L,
                time_from_show = getTimeDifference(showTime),
                video_position = videoPosition)

        databaseHelper?.insert(placement)
    }

    companion object {
        private var instance: EventAnalytics? = null

        fun getInstance(context: Context?): EventAnalytics? {
            if (instance == null) {
                instance = EventAnalytics(context)
            }
            return instance
        }
    }
}