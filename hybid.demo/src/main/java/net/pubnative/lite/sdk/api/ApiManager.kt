// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.api

import android.text.TextUtils
import net.pubnative.lite.sdk.HyBid

object ApiManager {

    fun getApiUrl() = HyBid.getApiClient().apiUrl

    fun setApiUrl(url: String) {
        if (!TextUtils.isEmpty(url) && HyBid.getApiClient() != null) {
            HyBid.getApiClient().apiUrl = url
        }
    }

    fun setSDKConfigURL(url: String?) {
        if (HyBid.isInitialized() && HyBid.getSDKConfigApiClient() != null) {
            HyBid.setSDKConfigURL(url)
        }
    }

    fun fetchConfigs() {
        if (HyBid.isInitialized() && HyBid.getSDKConfigApiClient() != null) {
            HyBid.validateAtom()
        }
    }

    fun getSDKConfigURL() = HyBid.getSDKConfigApiClient().url
    fun getSDKConfigType() = HyBid.getSDKConfigApiClient().configType
}