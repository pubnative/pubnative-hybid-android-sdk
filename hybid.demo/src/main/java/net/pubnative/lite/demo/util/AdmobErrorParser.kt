package net.pubnative.lite.demo.util

import com.google.android.gms.ads.AdRequest

object AdmobErrorParser {

    fun getErrorMessage(errorCode: Int) : String {
        when (errorCode) {
            AdRequest.ERROR_CODE_INTERNAL_ERROR -> {
                return "Internal error"
            }
            AdRequest.ERROR_CODE_INVALID_REQUEST -> {
                return "Invalid request"
            }
            AdRequest.ERROR_CODE_NETWORK_ERROR -> {
                return "Network error"
            }
            AdRequest.ERROR_CODE_NO_FILL -> {
                return "No fill"
            }
            else -> {
                return "Unknown error"
            }
        }
    }
}