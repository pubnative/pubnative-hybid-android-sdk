package net.pubnative.lite.demo.viewmodel

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.pubnative.lite.sdk.CacheListener
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.HyBidError
import net.pubnative.lite.sdk.VideoListener
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd
import java.util.*

class InterstitialViewModel(application: Application) : AndroidViewModel(application) {

    val TAG = this::class.java.simpleName

    private var interstitial: HyBidInterstitialAd? = null
    var cachingEnabled: Boolean = true

    // Listeners
    private val interstitialAdListener: HyBidInterstitialAd.Listener = object : HyBidInterstitialAd.Listener {

            override fun onInterstitialLoaded() {
                Log.d(TAG, "onInterstitialLoaded")
                _interstitialLoadLiveData.value = true
                _creativeIdLiveData.value = if(interstitial?.creativeId?.isNotEmpty() == true)
                    interstitial?.creativeId else ""
            }

            override fun onInterstitialLoadFailed(error: Throwable?) {
                _interstitialLoadLiveData.value = false
                _creativeIdLiveData.value = ""
                handleError(error)
            }

            override fun onInterstitialImpression() {
                Log.d(TAG, "onInterstitialImpression")
                if (HyBid.getDiagnosticsManager() != null) {
                    HyBid.getDiagnosticsManager().printPlacementDiagnosticsLog(
                        application,
                        interstitial?.placementParams
                    )
                }
            }

            override fun onInterstitialDismissed() {
                Log.d(TAG, "onInterstitialDismissed")
                interstitial = null
                _interstitialLoadLiveData.value = false
            }

            override fun onInterstitialClick() {
                Log.d(TAG, "onInterstitialClick")
            }
        }

    private val cacheListener: CacheListener = object : CacheListener {

        override fun onCacheSuccess() {
            Log.d(TAG, "onCacheSuccess")
            _cacheLiveData.value = true
        }

        override fun onCacheFailed(error: Throwable?) {
            _cacheLiveData.value = false
            handleError(error)
        }
    }

    private val videoListener: VideoListener = object : VideoListener {

        override fun onVideoError(progressPercentage: Int) {
            Log.d(TAG, String.format(Locale.ENGLISH, "onVideoError progress: %d", progressPercentage))
        }

        override fun onVideoStarted() {
            Log.d(TAG, "onVideoStarted")
        }

        override fun onVideoDismissed(progressPercentage: Int) {
            Log.d(TAG, String.format(Locale.ENGLISH, "onVideoDismissed progress: %d", progressPercentage))
        }

        override fun onVideoFinished() {
            Log.d(TAG, "onVideoFinished")
        }

        override fun onVideoSkipped() {
            Log.d(TAG, String.format(Locale.ENGLISH, "onVideoSkipped", ""))
        }
    }

    // Live data
    private val _interstitialLoadLiveData = MutableLiveData<Boolean>()
    val interstitialLoadLiveData = _interstitialLoadLiveData
    private val _cacheLiveData = MutableLiveData<Boolean>()
    val cacheLiveData: LiveData<Boolean> = _cacheLiveData
    private val _errorMessageLiveData = MutableLiveData<String>()
    val errorMessageLiveData: LiveData<String> = _errorMessageLiveData
    private val _errorCodeLiveData = MutableLiveData<String>()
    val errorCodeLiveData: LiveData<String> = _errorCodeLiveData
    private val _creativeIdLiveData = MutableLiveData<String>()
    val creativeIdLiveData: LiveData<String> = _creativeIdLiveData

    fun loadAd(activity: Activity, zoneId: String?){
        clearErrors()
        interstitial = HyBidInterstitialAd(activity, zoneId, interstitialAdListener)
        interstitial?.isAutoCacheOnLoad = cachingEnabled
        //Optional to track video events
        interstitial?.setVideoListener(videoListener)
        interstitial?.load()
    }

    fun prepareAd(){
        interstitial?.prepare(cacheListener)
    }

    fun showAd(){
        interstitial?.show()
    }

    fun reset(){
        clearErrors()
        _interstitialLoadLiveData.value = false
        interstitial?.destroy()
        interstitial = null
    }

    override fun onCleared() {
        interstitial?.destroy()
        interstitial = null
        super.onCleared()
    }

    private fun handleError(error: Throwable?){

        if (error != null && error is HyBidError) {
            Log.e(TAG, error.message ?: " - ")
            _errorCodeLiveData.value = error.errorCode.code.toString()
            _errorMessageLiveData.value = error.message ?: " - "
        } else {
            _errorCodeLiveData.value = " - "
            _errorMessageLiveData.value = " - "
        }
    }

    private fun clearErrors(){
        _errorCodeLiveData.value = ""
        _errorMessageLiveData.value = ""
    }
}