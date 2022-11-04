package net.pubnative.lite.demo.viewmodel

import android.app.Application
import android.text.TextUtils
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.pubnative.lite.demo.ui.fragments.apitester.LegacyApiTesterSize
import net.pubnative.lite.demo.ui.fragments.apitester.LegacyApiTesterSize.*
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.demo.util.JsonUtils
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.api.PNApiClient
import net.pubnative.lite.sdk.models.APIAsset
import net.pubnative.lite.sdk.models.Ad
import net.pubnative.lite.sdk.models.AdResponse
import net.pubnative.lite.sdk.utils.AdRequestRegistry
import net.pubnative.lite.sdk.vpaid.VideoAdCacheItem
import net.pubnative.lite.sdk.vpaid.VideoAdProcessor
import net.pubnative.lite.sdk.vpaid.models.EndCardData
import net.pubnative.lite.sdk.vpaid.response.AdParams
import org.json.JSONObject

class ApiTesterViewModel(application: Application) : AndroidViewModel(application) {

    private var adSize: LegacyApiTesterSize = BANNER

    private val _clipboard: MutableLiveData<String> = MutableLiveData()
    val clipboard: LiveData<String> = _clipboard

    private val _loadInterstitial: MutableLiveData<Ad?> = MutableLiveData()
    val loadInterstitial: LiveData<Ad?> = _loadInterstitial

    private val _adapterUpdate: MutableLiveData<Ad?> = MutableLiveData()
    val adapterUpdate: LiveData<Ad?> = _adapterUpdate

    private val _listVisibillity: MutableLiveData<Boolean> = MutableLiveData()
    val listVisibillity: LiveData<Boolean> = _listVisibillity

    private lateinit var apiClient: PNApiClient

    init {
        apiClient = PNApiClient(application)
    }

    fun pasteFromClipboard() {
        val clipboardText =
            ClipboardUtils.copyFromClipboard(getApplication<Application>().applicationContext)
        if (!TextUtils.isEmpty(clipboardText)) {
            _clipboard.value = clipboardText
        }
    }

    fun setAdSize(adSize: LegacyApiTesterSize) {
        this.adSize = adSize
        _listVisibillity.value = adSize != LegacyApiTesterSize.INTERSTITIAL
    }

    fun loadAdFromResponse(response: String) {

        if (!isValidResponse(response)) {
            Toast.makeText(
                getApplication<Application>().applicationContext,
                "Please input valid response",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        AdRequestRegistry.getInstance().setLastAdRequest("", response, 0)
        processAd(response)
    }

    private fun processAd(response: String?) {

        apiClient.processStream(response, object : PNApiClient.AdRequestListener {

            override fun onSuccess(ad: Ad?) {
                handleAdResult(ad)
            }

            override fun onFailure(exception: Throwable?) {
                Toast.makeText(
                    getApplication<Application>().applicationContext,
                    "Can't parse ad response",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun handleAdResult(ad: Ad?) {

        if (ad == null) return

        if (adSize == LegacyApiTesterSize.INTERSTITIAL) {
            ad.zoneId = getZoneIdForInterstitial(ad)
            if (isVideoAd(ad)) {
                runCacheProcessForVideoAd(ad, _loadInterstitial)
            } else {
                HyBid.getAdCache().put(ad.zoneId, ad)
                _loadInterstitial.value = ad
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
                    val hasEndCard = adParams.endCardList != null && !adParams.endCardList.isEmpty()
                    val adCacheItem =
                        VideoAdCacheItem(adParams, videoFilePath, endCardData, endCardFilePath)
                    ad.setHasEndCard(hasEndCard)
                    HyBid.getAdCache().put(ad.zoneId, ad)
                    HyBid.getVideoAdCache().put(ad.zoneId, adCacheItem)
                    _loadLiveData.value = ad
                }

                override fun onCacheError(error: Throwable) {
                    Toast.makeText(
                        getApplication<Application>().applicationContext,
                        "Can't parse video ad response",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    fun getAdSize(): LegacyApiTesterSize {
        return adSize
    }
}