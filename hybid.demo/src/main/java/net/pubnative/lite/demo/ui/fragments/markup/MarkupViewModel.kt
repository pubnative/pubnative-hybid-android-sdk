package net.pubnative.lite.demo.ui.fragments.markup

import android.app.Application
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.Constants.Format
import net.pubnative.lite.demo.api.RemoteConfigApiClient
import net.pubnative.lite.demo.api.RemoteConfigApiClient.OnConfigFetchListener
import net.pubnative.lite.demo.managers.AdCustomizationPrefs
import net.pubnative.lite.demo.managers.AdCustomizationsManager
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.demo.util.RemoteConfigParamUtilisation
import net.pubnative.lite.demo.models.RemoteConfigParam
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.HyBidError
import net.pubnative.lite.sdk.models.APIAsset
import net.pubnative.lite.sdk.models.Ad
import net.pubnative.lite.sdk.models.AdSize
import net.pubnative.lite.sdk.models.EndCardData
import net.pubnative.lite.sdk.network.PNHttpClient
import net.pubnative.lite.sdk.utils.AdRequestRegistry
import net.pubnative.lite.sdk.utils.MarkupUtils
import net.pubnative.lite.sdk.vpaid.VideoAdCacheItem
import net.pubnative.lite.sdk.vpaid.VideoAdProcessor
import net.pubnative.lite.sdk.vpaid.response.AdParams

class MarkupViewModel(application: Application) : AndroidViewModel(application) {

    private var width: Int = AdSize.SIZE_300x50.width
    private var height: Int = AdSize.SIZE_300x50.height

    private val ADM_MACRO = "{[{ .Adm | base64EncodeString | safeHTML }]}"

    private var customMarkup: MarkupType = MarkupType.CUSTOM_MARKUP
    private var customMarkupSize: MarkupSize = MarkupSize.BANNER
    private var urWrap: Boolean = false
    private var urTemplate: String = ""

    private val _clipboard: MutableLiveData<String> = MutableLiveData()
    val clipboard: LiveData<String> = _clipboard

    private val _loadInterstitial: MutableLiveData<String> = MutableLiveData()
    val loadInterstitial: LiveData<String> = _loadInterstitial

    private val _loadAdBanner: MutableLiveData<Ad?> = MutableLiveData()
    val loadAdBanner: LiveData<Ad?> = _loadAdBanner

    private val _loadAdInterstitial: MutableLiveData<Ad?> = MutableLiveData()
    val loadAdInterstitial: LiveData<Ad?> = _loadAdInterstitial

    private val _loadRewarded: MutableLiveData<String> = MutableLiveData()
    val loadRewarded: LiveData<String> = _loadRewarded

    private val _loadAdRewarded: MutableLiveData<Ad?> = MutableLiveData()
    val loadAdRewarded: LiveData<Ad?> = _loadAdRewarded

    private val _creativeId: MutableLiveData<String> = MutableLiveData()
    val creativeId: LiveData<String> = _creativeId

    private val _adapterUpdate: MutableLiveData<String> = MutableLiveData()
    val adapterUpdate: LiveData<String> = _adapterUpdate

    private val _listVisibility: MutableLiveData<Boolean> = MutableLiveData()
    val listVisibility: LiveData<Boolean> = _listVisibility

    private val _showButtonVisibility: MutableLiveData<Boolean> = MutableLiveData()
    val showButtonVisibility: LiveData<Boolean> = _showButtonVisibility

    private val _creativeIdVisibillity: MutableLiveData<Boolean> = MutableLiveData()
    val creativeIdVisibillity: LiveData<Boolean> = _creativeIdVisibillity

    private var configs: List<RemoteConfigParam> = ArrayList()

    private val mRemoteConfigApiClient: RemoteConfigApiClient =
        RemoteConfigApiClient()

    private val _onAdLoaded: MutableLiveData<Ad> = MutableLiveData()
    val onAdLoaded: LiveData<Ad> = _onAdLoaded

    private val _onAdLoadFailed: MutableLiveData<String> = MutableLiveData()
    val onAdLoadFailed: LiveData<String> = _onAdLoadFailed

    private val prefs = AdCustomizationPrefs(getApplication())

    fun pasteFromClipboard() {
        val clipboardText =
            ClipboardUtils.copyFromClipboard(getApplication<Application>().applicationContext)
        if (!TextUtils.isEmpty(clipboardText)) {
            _clipboard.value = clipboardText
        }
    }

    fun setURWrap(wrapInUR: Boolean) {
        this.urWrap = wrapInUR
    }

    fun setURTemplate(urTemplate: String) {
        this.urTemplate = urTemplate
    }

    fun setMarkupType(customMarkup: MarkupType) {
        this.customMarkup = customMarkup
    }

    fun setMarkupSize(markupSize: MarkupSize) {
        this.customMarkupSize = markupSize
        val isFullscreen =
            customMarkupSize == MarkupSize.INTERSTITIAL || customMarkupSize == MarkupSize.REWARDED
        _listVisibility.value = !isFullscreen
        _showButtonVisibility.value = isFullscreen

        when (customMarkupSize) {
            MarkupSize.BANNER -> {
                width = AdSize.SIZE_300x50.width
                height = AdSize.SIZE_300x50.height
            }

            MarkupSize.MEDIUM -> {
                width = AdSize.SIZE_300x250.width
                height = AdSize.SIZE_300x250.height
            }

            MarkupSize.LEADERBOARD -> {
                width = AdSize.SIZE_728x90.width
                height = AdSize.SIZE_728x90.height
            }

            else -> {

            }
        }
    }

    fun loadMarkup(markupText: String) {
        when (customMarkup) {
            MarkupType.CUSTOM_MARKUP -> {
                processMarkup(markupText)
            }

            MarkupType.URL -> {
                loadCreativeUrl(markupText)
            }

            MarkupType.ORTB_BODY -> {
                //Do nothing
            }
        }
    }

    private fun loadCreativeUrl(creativeURL: String) {
        PNHttpClient.makeRequest(getApplication<Application>().applicationContext,
            creativeURL,
            null,
            null,
            true,
            object : PNHttpClient.Listener {
                override fun onSuccess(
                    response: String?, headers: MutableMap<String?, MutableList<String>?>?
                ) {
                    AdRequestRegistry.getInstance().setLastAdRequest(creativeURL, response, 0)
                    Log.d("onSuccess", response ?: "")
                    if (!headers.isNullOrEmpty() && headers.containsKey("Creative_id")) {
                        val headerValues = headers["Creative_id"]
                        if (!headerValues.isNullOrEmpty()) {
                            processMarkup(response, headerValues.first())
                        } else {
                            processMarkup(response)
                        }
                    } else {
                        processMarkup(response)
                    }
                }

                override fun onFailure(error: Throwable) {
                    Log.d("onFailure", error.toString())
                    Toast.makeText(
                        getApplication<Application>().applicationContext,
                        "Creative request failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun processMarkup(markup: String?, creativeId: String = "") {
        if (creativeId.isNotEmpty()) {
            _creativeIdVisibillity.value = true
            _creativeId.value = creativeId
        } else {
            _creativeIdVisibillity.value = false
            _creativeId.value = ""
        }

        if (markup?.isEmpty() == true) {
            Toast.makeText(
                getApplication<Application>().applicationContext,
                "Please input some markup",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val renderMarkup =
                if (urWrap && urTemplate.isNotEmpty()) wrapInUR(markup!!) else markup!!
            when (customMarkupSize) {
                MarkupSize.INTERSTITIAL -> {
                    _loadInterstitial.value = renderMarkup
                }

                MarkupSize.REWARDED -> {
                    _loadRewarded.value = renderMarkup
                }

                else -> {
                    _adapterUpdate.value = renderMarkup
                }
            }
        }
    }

    fun getMarkupSize(): MarkupSize {
        return customMarkupSize
    }

    private fun wrapInUR(adm: String): String {
        val encodedAdm = Base64.encodeToString(adm.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
        return urTemplate.replace(ADM_MACRO, encodedAdm, false)
    }

    private fun capsuleRemoteConfigParams() {
        val prefs = AdCustomizationPrefs(getApplication<Application>().applicationContext)
        val adCustomisationData = AdCustomizationsManager.fromJson(prefs.getAdCustomizationData())
        if (adCustomisationData != null) {
            formatRemoteConfigParams(adCustomisationData)
        }
    }

    private fun formatRemoteConfigParams(adCustomizationsManager: AdCustomizationsManager) {
        configs = RemoteConfigParamUtilisation.convertAdCustomizationToRemoteConfigParam(
            adCustomizationsManager
        )
    }

    fun getRemoteConfigParams(): List<RemoteConfigParam> {
        if (configs.isEmpty()) {
            capsuleRemoteConfigParams()
        }
        return configs
    }

    fun refetchAdCustomisationParams() {
        capsuleRemoteConfigParams()
    }

    fun loadBannerRemoteConfig(adm: String?) {
        val format: String = if (MarkupUtils.isVastXml(adm)) {
            Format.VIDEO
        } else {
            Format.HTML
        }

        mRemoteConfigApiClient.sendBannerRequest(
            getApplication(),
            adm,
            format,
            Constants.AdmType.MARKUP,
            prefs.getCustomCTAIconURL(),
            prefs.getCustomEndCardHTML(),
            width,
            height,
            configs,
            object : OnConfigFetchListener {
                override fun onFetchSuccess(ad: Ad?, response: String?) {
                    if (ad != null) {
                        ad.zoneId = getZoneIdBySize(ad)
                        if (isVideoAd(ad)) {
                            runCacheProcessForVideoAd(ad, _loadAdBanner)
                        } else {
                            HyBid.getAdCache().put(ad.zoneId, ad)
                            _loadAdBanner.value = ad
                        }
                    }
                }

                override fun onFetchError(error: HyBidError?) {
                }
            }
        )
    }

    fun loadInterstitialRemoteConfig(adm: String) {
        customMarkupSize = MarkupSize.INTERSTITIAL
        val format: String = if (MarkupUtils.isVastXml(adm)) {
            Format.VIDEO
        } else {
            Format.HTML
        }

        mRemoteConfigApiClient.sendInterstitialRequest(
            getApplication(), adm, format, Constants.AdmType.MARKUP, prefs.getCustomCTAIconURL(),
            prefs.getCustomEndCardHTML(),
            configs, object : OnConfigFetchListener {
                override fun onFetchSuccess(ad: Ad?, response: String?) {
                    AdRequestRegistry.getInstance().setLastAdRequest("Customized", response, 0)
                    if (ad != null) {
                        ad.zoneId = getZoneIdForInterstitial(ad)
                        if (isVideoAd(ad)) {
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

    private fun isVideoAd(ad: Ad): Boolean {
        return ad.getAsset(APIAsset.VAST) != null
    }

    fun loadRewardedRemoteConfig(adm: String) {
        customMarkupSize = MarkupSize.REWARDED

        val format: String = if (MarkupUtils.isVastXml(adm)) {
            Format.VIDEO
        } else {
            Format.HTML
        }

        mRemoteConfigApiClient.sendRewardedRequest(
            getApplication(), adm, format, Constants.AdmType.MARKUP, prefs.getCustomCTAIconURL(),
            prefs.getCustomEndCardHTML(),
            configs, object : OnConfigFetchListener {
                override fun onFetchSuccess(ad: Ad?, response: String?) {
                    AdRequestRegistry.getInstance().setLastAdRequest("Customized", response, 0)
                    if (ad != null) {
                        ad.zoneId = getZoneIdForInterstitial(ad)
                        if (isVideoAd(ad)) {
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
                    _loadLiveData.value = ad
                }

                override fun onCacheError(error: Throwable) {
                    _onAdLoadFailed.value = "Can't parse video ad response"
                }
            })
    }

    private fun getZoneIdForInterstitial(ad: Ad): String {
        return if (isVideoAd(ad)) "4" else "3"
    }

    private fun getZoneIdBySize(ad: Ad): String {
        return when (customMarkupSize) {
            MarkupSize.BANNER -> "2"
            MarkupSize.MEDIUM -> if (isVideoAd(ad)) "6" else "5"
            MarkupSize.LEADERBOARD -> "8"
            else -> ""
        }
    }
}