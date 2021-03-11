package net.pubnative.lite.sdk.audience

import android.content.Context
import net.pubnative.lite.sdk.models.Ad

class AdAnalyticsSession(val context: Context?, val ad: Ad?, private val mCreativeType: String, private val mAdFormat: String,
                         private val mAdSize: String) {

    private var showTime: Long = 0
    private val eventAnalytics: EventAnalytics? = EventAnalytics.getInstance(context)

    fun onAdShowed() {
        showTime = System.currentTimeMillis()
    }

    fun onAdImpression() {
        eventAnalytics?.fireImpressionEvent(ad, showTime, mCreativeType, mAdFormat, mAdSize)
    }

    fun onAdClicked() {
        eventAnalytics?.fireClickEvent(ad, showTime, mCreativeType, mAdFormat, mAdSize)
    }

    fun onAdFinished() {
        eventAnalytics?.fireVideoFinishedEvent(ad, showTime, mCreativeType, mAdFormat, mAdSize)
    }

    fun onAdDismissed() {
        eventAnalytics?.fireVideoDismissedEvent(ad, showTime, mCreativeType, mAdFormat, mAdSize)
    }

    fun onAdStarted() {
        eventAnalytics?.fireVideoStartedEvent(ad, showTime, mCreativeType, mAdFormat, mAdSize)
    }

    fun onAdMuted(videoPosition: Int) {
        eventAnalytics?.fireMuteEvent(ad, showTime, videoPosition, mCreativeType, mAdFormat, mAdSize)
    }

    fun onAdUnmuted(videoPosition: Int) {
        eventAnalytics?.fireUnMuteEvent(ad, showTime, videoPosition, mCreativeType, mAdFormat, mAdSize)
    }
}