package net.pubnative.lite.demo.viewmodel

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.analytics.FirebaseAnalytics
import net.pubnative.lite.demo.models.RequestDebugInfo
import net.pubnative.lite.demo.util.JsonUtils
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.analytics.Reporting
import net.pubnative.lite.sdk.analytics.ReportingEvent
import net.pubnative.lite.sdk.analytics.ReportingEventCallback
import net.pubnative.lite.sdk.analytics.tracker.ReportingTracker
import net.pubnative.lite.sdk.analytics.tracker.ReportingTrackerCallback
import net.pubnative.lite.sdk.utils.AdRequestRegistry

class DebugViewModel(application: Application) : AndroidViewModel(application),
    ReportingEventCallback, ReportingTrackerCallback {

    private var isReportingCallbackActive: Boolean = true

    // Live data
    private val _requestDebugInfo: MutableLiveData<RequestDebugInfo> = MutableLiveData()
    val requestDebugInfo: LiveData<RequestDebugInfo> = _requestDebugInfo
    private var _eventList: ArrayList<ReportingEvent> = arrayListOf()
    private var _trackerList: ArrayList<ReportingTracker> = arrayListOf()

    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    init {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(application)
        mFirebaseAnalytics!!.setAnalyticsCollectionEnabled(true)
        HyBid.getReportingController().addCallback(this)
        HyBid.getReportingController().addTrackerCallback(this)
    }

    fun updateLogs() {
        val registryItem = AdRequestRegistry.getInstance().lastAdRequest
        if (registryItem != null && registryItem.response != null) {
            val response =
                if (JsonUtils.isValidJson(registryItem.response)) JsonUtils.toFormattedJson(
                    registryItem.response
                ) else registryItem.response.toString()

            val debugInfo = RequestDebugInfo(
                registryItem.url ?: "", registryItem.latency ?: 0, response
            )

            _requestDebugInfo.value = debugInfo
            AdRequestRegistry.getInstance().setLastAdRequest("", "", 0)
        }
    }

    fun cacheEventList() {
        HyBid.getReportingController().cacheAdEventList(_eventList)
    }

    fun clearEventList() {
        _eventList = arrayListOf()
    }

    fun clearTrackerList(){
        _trackerList = arrayListOf()
    }

    fun clearLogs() {
        _requestDebugInfo.value = RequestDebugInfo("", 0, "")
    }

    override fun onEvent(event: ReportingEvent?) {
        if (event != null) {
            if (event.eventType != null && event.eventType.equals(Reporting.EventType.REQUEST)){
                clearEventList()
                clearTrackerList()
            }

            _eventList.add(event)

            event.eventObject?.let {
                val bundle = Bundle()
                for (key in event.eventObject.keys()) {
                    bundle.putString(key, event.eventObject[key].toString())
                }
                mFirebaseAnalytics?.setDefaultEventParameters(bundle)
                mFirebaseAnalytics?.logEvent(event.eventType, bundle)
            }
        }
    }

    override fun onFire(firedTracker: ReportingTracker?) {
        firedTracker?.let { _trackerList.add(it) }
    }

    fun getEventList(): MutableLiveData<List<ReportingEvent>> {
        val liveData: MutableLiveData<List<ReportingEvent>> = MutableLiveData()
        liveData.value = _eventList
        return liveData
    }

    fun getFiredTrackersList(): MutableLiveData<List<ReportingTracker>>{
        val liveData: MutableLiveData<List<ReportingTracker>> = MutableLiveData()
        liveData.value = _trackerList
        return liveData
    }

    override fun onCleared() {
        super.onCleared()
        HyBid.getReportingController().removeCallback(this)
    }

    fun registerReportingCallbacks() {
        if (!isReportingCallbackActive){
            HyBid.getReportingController().addCallback(this)
            HyBid.getReportingController().addTrackerCallback(this)
        }
    }

    fun removeReportingCallbacks() {
        HyBid.getReportingController().removeCallback(this)
        HyBid.getReportingController().removeTrackerCallback(this)
        isReportingCallbackActive = false
    }
}