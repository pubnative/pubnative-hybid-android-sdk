package net.pubnative.lite.sdk.api

import android.text.TextUtils
import net.pubnative.lite.sdk.HyBid

object ApiManager {
    fun getApiUrl() = HyBid.getApiClient().apiUrl

    fun setApiUrl(url: String) {
        if (!TextUtils.isEmpty(url)) {
            HyBid.getApiClient().apiUrl = url
        }
    }
}