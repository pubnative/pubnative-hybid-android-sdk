package net.pubnative.lite.demo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.pubnative.lite.demo.managers.AdCustomizationPrefs
import net.pubnative.lite.demo.managers.AdCustomizationsManager
import net.pubnative.lite.demo.util.RemoteConfigParamUtilisation
import net.pubnative.lite.sdk.HyBidError
import net.pubnative.lite.demo.api.RemoteConfigApiClient
import net.pubnative.lite.demo.api.RemoteConfigApiClient.OnConfigFetchListener
import net.pubnative.lite.sdk.models.Ad
import net.pubnative.lite.sdk.models.AdSize
import net.pubnative.lite.demo.models.RemoteConfigParam
import net.pubnative.lite.sdk.network.PNHttpClient
import net.pubnative.lite.sdk.utils.AdRequestRegistry

class AdCustomizationViewModel(application: Application) : AndroidViewModel(application) {

    private val _onAdLoadFailed: MutableLiveData<String> = MutableLiveData()
    val onAdLoadFailed: LiveData<String> = _onAdLoadFailed

    private val _onAdLoaded: MutableLiveData<Ad> = MutableLiveData()
    val onAdLoaded: LiveData<Ad> = _onAdLoaded

    private val mRemoteConfigApiClient: RemoteConfigApiClient =
        RemoteConfigApiClient()

    private val remoteConfigFetchListener = object : OnConfigFetchListener {
        override fun onFetchSuccess(ad: Ad, response: String) {
            AdRequestRegistry.getInstance().setLastAdRequest("Customized", response, 0)
            _onAdLoaded.value = ad
        }

        override fun onFetchError(error: HyBidError) {
            _onAdLoadFailed.value = error.message
        }
    }

    private var configs: List<RemoteConfigParam> = ArrayList()

    private fun capsuleRemoteConfigParams() {
        val prefs = AdCustomizationPrefs(getApplication<Application>().applicationContext)
        AdCustomizationsManager.fromJson(prefs.getAdCustomizationData())?.let {
            val obj = it
            formatRemoteConfigParams(obj)
        }
    }

    fun loadCustomizedAd(
        adm: String,
        adSize: AdSize?,
        isRewarded: Boolean,
        format: String,
        admType: String
    ) {

        if (adSize == null) return

        if (adSize == AdSize.SIZE_INTERSTITIAL) {
            if (isRewarded) {
                loadRewarded(adm, format, admType)
            } else {
                loadInterstitial(adm, format, admType)
            }
        } else {
            loadBanner(adm, admType, adSize.width, adSize.height, format)
        }
    }

    fun loadCustomizedAdFromUrl(
        adUrl: String,
        adSize: AdSize?,
        isRewarded: Boolean,
        format: String,
        admType: String
    ) {
        PNHttpClient.makeRequest(getApplication<Application>().applicationContext,
            adUrl,
            null,
            null,
            true,
            object : PNHttpClient.Listener {
                override fun onSuccess(
                    response: String?, headers: MutableMap<String?, MutableList<String>?>?
                ) {
                    loadCustomizedAd(response ?: "", adSize, isRewarded, format, admType)
                }

                override fun onFailure(error: Throwable) {
                    _onAdLoadFailed.value = "Ad request failed"
                }
            })
    }

    private fun loadBanner(adm: String, admType: String, width: Int, height: Int, format: String) {
        val prefs = AdCustomizationPrefs(getApplication())
        mRemoteConfigApiClient.sendBannerRequest(
            getApplication(),
            adm,
            format,
            admType,
            prefs.getCustomCTAIconURL(),
            prefs.getCustomEndCardHTML(),
            width,
            height,
            configs,
            remoteConfigFetchListener
        )
    }

    private fun loadRewarded(adm: String, format: String, admType: String) {
        val prefs = AdCustomizationPrefs(getApplication())
        mRemoteConfigApiClient.sendRewardedRequest(
            getApplication(),
            adm,
            format,
            admType,
            prefs.getCustomCTAIconURL(),
            prefs.getCustomEndCardHTML(),
            configs,
            remoteConfigFetchListener
        )
    }

    private fun loadInterstitial(adm: String, format: String, admType: String) {
        val prefs = AdCustomizationPrefs(getApplication())
        mRemoteConfigApiClient.sendInterstitialRequest(
            getApplication(),
            adm,
            format,
            admType,
            prefs.getCustomCTAIconURL(),
            prefs.getCustomEndCardHTML(),
            configs,
            remoteConfigFetchListener
        )
    }

    private fun formatRemoteConfigParams(adCustomizationsManager: AdCustomizationsManager) {
        configs = RemoteConfigParamUtilisation.convertAdCustomizationToRemoteConfigParam(
            adCustomizationsManager
        )
    }

    fun refetchAdCustomisationParams() {
        capsuleRemoteConfigParams()
    }

    fun getAdCustomizationConfigs(): List<RemoteConfigParam> {
        return configs
    }
}