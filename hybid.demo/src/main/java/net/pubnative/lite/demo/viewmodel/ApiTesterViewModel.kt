// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.viewmodel

import android.app.Application
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.pubnative.lite.demo.ui.fragments.apitester.LegacyApiTesterSize
import net.pubnative.lite.demo.ui.fragments.apitester.LegacyApiTesterSize.*
import net.pubnative.lite.demo.ui.fragments.markup.MarkupType
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.demo.util.JsonUtils
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.api.ApiClient
import net.pubnative.lite.sdk.api.OpenRTBApiClient
import net.pubnative.lite.sdk.api.PNApiClient
import net.pubnative.lite.sdk.models.APIAsset
import net.pubnative.lite.sdk.models.Ad
import net.pubnative.lite.sdk.models.AdResponse
import net.pubnative.lite.sdk.models.AdSize
import net.pubnative.lite.sdk.models.EndCardData
import net.pubnative.lite.sdk.network.PNHttpClient
import net.pubnative.lite.sdk.utils.AdRequestRegistry
import net.pubnative.lite.sdk.vpaid.VideoAdCacheItem
import net.pubnative.lite.sdk.vpaid.VideoAdProcessor
import net.pubnative.lite.sdk.vpaid.response.AdParams
import org.json.JSONObject

class ApiTesterViewModel(application: Application) : AndroidViewModel(application) {
    val TAG = this::class.java.simpleName

    private var adSize: LegacyApiTesterSize = BANNER
    private var customMarkup: MarkupType = MarkupType.CUSTOM_MARKUP

    private val _clipboard: MutableLiveData<String> = MutableLiveData()
    val clipboard: LiveData<String> = _clipboard

    private val _clipboardBody: MutableLiveData<String> = MutableLiveData()
    val clipboardBody: LiveData<String> = _clipboardBody

    private val _loadInterstitial: MutableLiveData<Ad?> = MutableLiveData()
    val loadInterstitial: LiveData<Ad?> = _loadInterstitial

    private val _loadRewarded: MutableLiveData<Ad?> = MutableLiveData()
    val loadRewarded: LiveData<Ad?> = _loadRewarded

    private val _adapterUpdate: MutableLiveData<Ad?> = MutableLiveData()
    val adapterUpdate: LiveData<Ad?> = _adapterUpdate

    private val _listVisibility: MutableLiveData<Boolean> = MutableLiveData()
    val listVisibility: LiveData<Boolean> = _listVisibility

    private val _showButtonVisibility: MutableLiveData<Boolean> = MutableLiveData()
    val showButtonVisibility: LiveData<Boolean> = _showButtonVisibility

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String> = _errorMessage

    private var apiClient: PNApiClient
    private var oRtbApiClient: OpenRTBApiClient

    init {
        apiClient = PNApiClient(application)
        oRtbApiClient = OpenRTBApiClient(application)
    }

    fun pasteFromClipboard() {
        val clipboardText =
            ClipboardUtils.copyFromClipboard(getApplication<Application>().applicationContext)
        if (!TextUtils.isEmpty(clipboardText)) {
            _clipboard.value = clipboardText
        }
    }

    fun pasteFromClipboardBody() {
        val clipboardText =
            ClipboardUtils.copyFromClipboard(getApplication<Application>().applicationContext)
        if (!TextUtils.isEmpty(clipboardText)) {
            _clipboardBody.value = clipboardText
        }
    }

    fun setAdSize(adSize: LegacyApiTesterSize) {
        this.adSize = adSize
        val isFullscreen = adSize == INTERSTITIAL || adSize == REWARDED
        _listVisibility.value = !isFullscreen
        _showButtonVisibility.value = isFullscreen
    }

    fun setMarkupType(customMarkup: MarkupType) {
        this.customMarkup = customMarkup
    }

    fun getMarkupType(): MarkupType {
        return this.customMarkup
    }

    fun loadApiAd(apiAd: String, body: String?) {
        if (HyBid.isInitialized()) {
            when (customMarkup) {
                MarkupType.CUSTOM_MARKUP -> {
                    loadAdFromResponse(apiAd)
                }

                MarkupType.URL -> {
                    loadAdFromUrl(apiAd)
                }

                MarkupType.ORTB_BODY -> {
                    if (apiAd.isNullOrEmpty() || body.isNullOrEmpty()) {
                        Log.v(TAG, "Please enter a valid URL and body for the request")
                        _errorMessage.value = "Please enter a valid URL and body for the request"
                        return
                    } else {
                        loadOrtbAd(apiAd, body)
                    }
                }
            }
        } else {
            Log.v(
                TAG,
                "HyBid SDK is not initiated yet. Please initiate it before attempting to load an ad from URL or api response"
            )
        }
    }

    private fun loadAdFromResponse(response: String?) {
        if (!isValidResponse(response)) {
            _errorMessage.value = "Please input valid response"
            return
        }

        AdRequestRegistry.getInstance().setLastAdRequest("", response, 0)
        processAd(response)
    }

    private fun loadAdFromUrl(adUrl: String) {
        PNHttpClient.makeRequest(getApplication<Application>().applicationContext,
            adUrl,
            null,
            null,
            true,
            object : PNHttpClient.Listener {
                override fun onSuccess(
                    response: String?, headers: MutableMap<String?, MutableList<String>?>?
                ) {
                    AdRequestRegistry.getInstance().setLastAdRequest(adUrl, response, 0)
                    Log.d("onSuccess", response ?: "")
                    loadAdFromResponse(response)
                }

                override fun onFailure(error: Throwable) {
                    Log.d("onFailure", error.toString())
                    _errorMessage.value = "Ad request failed"
                }
            })
    }

    private fun loadOrtbAd(adUrl: String, adBody: String) {
        val userAgent = HyBid.getDeviceInfo().userAgent
        val headers: MutableMap<String, String> = HashMap()
        headers["x-openrtb-version"] = "2.3"
        headers["Content-Type"] = "application/json"
        headers["Accept-Charset"] = "utf-8"
        headers["User-Agent"] = userAgent

        PNHttpClient.makeRequest(getApplication<Application>().applicationContext,
            adUrl,
            headers,
            adBody,
            true,
            object : PNHttpClient.Listener {
                override fun onSuccess(
                    response: String?, headers: MutableMap<String?, MutableList<String>?>?
                ) {
                    AdRequestRegistry.getInstance().setLastAdRequest(adUrl, response, 0)
                    Log.d("onSuccess", response ?: "")
                    loadAdFromResponse(response)
                }

                override fun onFailure(error: Throwable) {
                    Log.d("onFailure", error.toString())
                    _errorMessage.value = "Ad request failed"
                }
            })
    }

    private fun processAd(response: String?) {

        apiClient.processStream(response, object : ApiClient.AdRequestListener {

            override fun onSuccess(ad: Ad?) {
                handleAdResult(ad)
            }

            override fun onFailure(exception: Throwable?) {
                val size = when (adSize) {
                    BANNER -> AdSize.SIZE_320x50
                    MEDIUM -> AdSize.SIZE_300x250
                    LEADERBOARD -> AdSize.SIZE_728x90
                    INTERSTITIAL -> AdSize.SIZE_320x480
                    REWARDED -> AdSize.SIZE_320x480
                    else -> AdSize.SIZE_320x50
                }
                oRtbApiClient.processStream(
                    response,
                    null,
                    size.width,
                    size.height,
                    object : ApiClient.AdRequestListener {
                        override fun onSuccess(ad: Ad?) {
                            handleAdResult(ad)
                        }

                        override fun onFailure(exception: Throwable?) {
                            _errorMessage.value = "Can't parse ad response"
                        }
                    })
            }
        })
    }

    fun handleAdResult(ad: Ad?) {

        if (ad == null) return

        if (adSize == INTERSTITIAL || adSize == REWARDED) {
            ad.zoneId = getZoneIdForInterstitial(ad)
            val livedata = if (adSize == INTERSTITIAL) _loadInterstitial else _loadRewarded
            if (isVideoAd(ad)) {
                runCacheProcessForVideoAd(ad, livedata)
            } else {
                HyBid.getAdCache().put(ad.zoneId, ad)
                livedata.value = ad
            }
        } else {
            ad.zoneId = getZoneIdBySize(ad)
            if (isVideoAd(ad)) {
                runCacheProcessForVideoAd(ad, _adapterUpdate)
            } else {
                HyBid.getAdCache().put(ad.zoneId, ad)
                _adapterUpdate.value = ad
            }
        }
    }

    private fun isValidResponse(response: String?): Boolean {

        if (response == null || response.isEmpty() || !JsonUtils.isValidJson(response)) {
            return false
        }

        try {
            AdResponse(JSONObject(response))
        } catch (e: Exception) {
            return false
        }

        return true
    }

    private fun getZoneIdForInterstitial(ad: Ad): String {
        return if (isVideoAd(ad)) "4" else "3"
    }

    private fun getZoneIdBySize(ad: Ad): String {

        return when (getAdSize()) {
            BANNER -> "2"
            MEDIUM -> if (isVideoAd(ad)) "6" else "5"
            NATIVE -> "7"
            LEADERBOARD -> "8"
            else -> ""
        }
    }

    private fun isVideoAd(ad: Ad): Boolean {
        return ad.getAsset(APIAsset.VAST) != null
    }

    private fun runCacheProcessForVideoAd(ad: Ad, _loadLiveData: MutableLiveData<Ad?>) {

        val videoAdProcessor = VideoAdProcessor()

        videoAdProcessor.process(
            getApplication(),
            ad.vast,
            null,
            object : VideoAdProcessor.Listener {
                override fun onCacheSuccess(
                    adParams: AdParams,
                    videoFilePath: String,
                    endCardData: EndCardData?,
                    endCardFilePath: String?,
                    omidVendors: List<String>
                ) {
                    val hasEndCard =
                        adParams.endCardList != null && adParams.endCardList.isNotEmpty()
                    val adCacheItem =
                        VideoAdCacheItem(adParams, videoFilePath, endCardData, endCardFilePath)
                    ad.setHasEndCard(hasEndCard)
                    HyBid.getAdCache().put(ad.zoneId, ad)
                    HyBid.getVideoAdCache().put(ad.zoneId, adCacheItem)
                    _loadLiveData.postValue(ad)
                }

                override fun onCacheError(error: Throwable) {
                    _errorMessage.value = "Can't parse video ad response"
                }
            })
    }

    fun getAdSize(): LegacyApiTesterSize {
        return adSize
    }
}