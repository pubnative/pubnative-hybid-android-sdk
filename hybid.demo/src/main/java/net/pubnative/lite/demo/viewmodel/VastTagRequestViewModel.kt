// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.api.RemoteConfigApiClient
import net.pubnative.lite.demo.managers.AdCustomizationPrefs
import net.pubnative.lite.demo.managers.AdCustomizationsManager
import net.pubnative.lite.demo.models.RemoteConfigParam
import net.pubnative.lite.demo.util.RemoteConfigParamUtilisation
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.HyBidError
import net.pubnative.lite.sdk.models.APIAsset
import net.pubnative.lite.sdk.models.Ad
import net.pubnative.lite.sdk.models.EndCardData
import net.pubnative.lite.sdk.network.PNHttpClient
import net.pubnative.lite.sdk.utils.AdRequestRegistry
import net.pubnative.lite.sdk.utils.Logger
import net.pubnative.lite.sdk.utils.MarkupUtils
import net.pubnative.lite.sdk.vpaid.VideoAdCacheItem
import net.pubnative.lite.sdk.vpaid.VideoAdProcessor
import net.pubnative.lite.sdk.vpaid.response.AdParams
import net.pubnative.lite.sdk.vpaid.vast.VastUrlUtils

class VastTagRequestViewModel(application: Application) : AndroidViewModel(application) {

    val tagClassName: String = this::class.java.simpleName

    private var configs: List<RemoteConfigParam> = ArrayList()

    private var vast: VAST = VAST.INTERSTITIAL

    private val mRemoteConfigApiClient: RemoteConfigApiClient =
        RemoteConfigApiClient()

    private val _loadAdInterstitial: MutableLiveData<Ad?> = MutableLiveData()
    val loadAdInterstitial: LiveData<Ad?> = _loadAdInterstitial

    private val _loadAdRewarded: MutableLiveData<Ad?> = MutableLiveData()
    val loadAdRewarded: LiveData<Ad?> = _loadAdRewarded

    private val _onAdLoadFailed: MutableLiveData<String> = MutableLiveData()
    val onAdLoadFailed: LiveData<String> = _onAdLoadFailed

    fun fetchAdCustomisationConfigs() {
        capsuleRemoteConfigParams()
    }

    private fun capsuleRemoteConfigParams() {
        val prefs = AdCustomizationPrefs(getApplication())
        AdCustomizationsManager.fromJson(prefs.getAdCustomizationData())?.let {
            val obj = it
            formatRemoteConfigParams(obj)
        }
    }

    private fun formatRemoteConfigParams(adCustomizationsManager: AdCustomizationsManager) {
        configs = RemoteConfigParamUtilisation.convertAdCustomizationToRemoteConfigParam(
            adCustomizationsManager
        )
    }

    fun interstitialRadioButtonSelected() {
        vast = VAST.INTERSTITIAL
    }

    fun rewardedRadioButtonSelected() {
        vast = VAST.REWARDED
    }

    fun prepareVideoTag(zoneId: String?, adValue: String?) {
        val params = VastUrlUtils.buildParameters()
        val url = VastUrlUtils.formatURL(adValue, params)
        val headers: MutableMap<String, String> = HashMap()
        val userAgent = HyBid.getDeviceInfo().userAgent
        if (!TextUtils.isEmpty(userAgent)) {
            headers["User-Agent"] = userAgent
        }
        val initTime = System.currentTimeMillis()
        PNHttpClient.makeRequest(
            getApplication(),
            url,
            headers,
            null,
            object : PNHttpClient.Listener {
                override fun onSuccess(response: String, headers: Map<String, List<String>>) {
                    registerAdRequest(url, response, initTime)
                    if (!TextUtils.isEmpty(response)) {
                        if (vast == VAST.INTERSTITIAL) {
                            loadInterstitialRemoteConfig(response, zoneId)
                        } else if (vast == VAST.REWARDED) {
                            loadRewardedRemoteConfig(response, zoneId)
                        }
                    }
                }

                override fun onFailure(error: Throwable) {
                    Logger.e(tagClassName, "Request failed: $error")
//                invokeOnLoadFailed(HyBidError(HyBidErrorCode.INVALID_ASSET))
                }
            })
    }

    private fun registerAdRequest(url: String, response: String, initTime: Long) {
        val responseTime = System.currentTimeMillis() - initTime
        AdRequestRegistry.getInstance().setLastAdRequest(url, response, responseTime)
    }

    fun getSelectedVast(): VAST {
        return vast
    }

    fun loadInterstitialRemoteConfig(adm: String, zoneId: String?) {
        val prefs = AdCustomizationPrefs(getApplication())
        val format: String = if (MarkupUtils.isVastXml(adm)) {
            Constants.Format.VIDEO
        } else {
            Constants.Format.HTML
        }

        mRemoteConfigApiClient.sendInterstitialRequest(
            getApplication(),
            adm,
            format,
            Constants.AdmType.MARKUP,
            prefs.getCustomCTAIconURL(),
            prefs.getCustomCTAAppName(),
            prefs.getBundleId(),
            prefs.getCustomEndCardHTML(),
            configs,
            object : RemoteConfigApiClient.OnConfigFetchListener {
                override fun onFetchSuccess(ad: Ad?, response: String?) {
                    AdRequestRegistry.getInstance().setLastAdRequest("Customized", response, 0)
                    if (ad != null) {
                        ad.zoneId = zoneId
                        if (isVideoAd(ad) || MarkupUtils.isVastXml(ad.vast)) {
                            runCacheProcessForVideoAd(ad, _loadAdInterstitial)
                        } else {
                            HyBid.getAdCache().put(ad.zoneId, ad)
                            _loadAdInterstitial.value = ad
                        }
                    }
                }

                override fun onFetchError(error: HyBidError?) {
                    _onAdLoadFailed.value = error?.message
                }
            }
        )
    }

    fun loadRewardedRemoteConfig(adm: String, zoneId: String?) {
        val prefs = AdCustomizationPrefs(getApplication())

        val format: String = if (MarkupUtils.isVastXml(adm)) {
            Constants.Format.VIDEO
        } else {
            Constants.Format.HTML
        }

        mRemoteConfigApiClient.sendRewardedRequest(
            getApplication(),
            adm,
            format,
            Constants.AdmType.MARKUP,
            prefs.getCustomCTAIconURL(),
            prefs.getCustomCTAAppName(),
            prefs.getBundleId(),
            prefs.getCustomEndCardHTML(),
            configs,
            object : RemoteConfigApiClient.OnConfigFetchListener {
                override fun onFetchSuccess(ad: Ad?, response: String?) {
                    AdRequestRegistry.getInstance().setLastAdRequest("Customized", response, 0)
                    if (ad != null) {
                        ad.zoneId = zoneId
                        if (isVideoAd(ad) || MarkupUtils.isVastXml(ad.vast)) {
                            runCacheProcessForVideoAd(ad, _loadAdRewarded)
                        } else {
                            HyBid.getAdCache().put(ad.zoneId, ad)
                            _loadAdRewarded.value = ad
                        }
                    }
                }

                override fun onFetchError(error: HyBidError?) {
                    _onAdLoadFailed.value = error?.message
                }
            }
        )
    }

    private fun runCacheProcessForVideoAd(ad: Ad, loadLiveData: MutableLiveData<Ad?>) {

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
                    loadLiveData.postValue(ad)
                }

                override fun onCacheError(error: Throwable) {
                    _onAdLoadFailed.value = "Can't parse video ad response"
                }
            })
    }

    private fun isVideoAd(ad: Ad): Boolean {
        return ad.getAsset(APIAsset.VAST) != null
    }

    enum class VAST {
        INTERSTITIAL, REWARDED
    }
}