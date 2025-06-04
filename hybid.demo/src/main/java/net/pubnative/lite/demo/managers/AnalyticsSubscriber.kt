// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.managers

import net.pubnative.lite.sdk.analytics.ReportingEvent
import net.pubnative.lite.sdk.analytics.ReportingEventCallback

object AnalyticsSubscriber {
    val eventList = mutableListOf<ReportingEvent>()

    val eventCallback = ReportingEventCallback { event ->
        if (!eventList.contains(event)) {
            eventList.add(event)
        }
    }
}