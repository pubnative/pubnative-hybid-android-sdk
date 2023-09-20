package net.pubnative.lite.demo.util

import android.content.Context
import android.util.Log
import com.onetrust.otpublishers.headless.Public.OTCallback
import com.onetrust.otpublishers.headless.Public.OTPublishersHeadlessSDK
import com.onetrust.otpublishers.headless.Public.Response.OTResponse
import net.pubnative.lite.demo.BuildConfig

class OneTrustManager private constructor(context: Context) {
    private var otPublishersHeadlessSDK: OTPublishersHeadlessSDK? = null;

    init {
        otPublishersHeadlessSDK = OTPublishersHeadlessSDK(context)
    }
    companion object {
        @Volatile
        private var instance: OneTrustManager? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: OneTrustManager(context).also { instance = it }
            }
    }

    fun getOtPublishersHeadlessSDK(): OTPublishersHeadlessSDK? {
        return otPublishersHeadlessSDK
    }

    fun initializeOpenTrustSDK() {
        otPublishersHeadlessSDK?.startSDK(
            BuildConfig.ONE_TRUST_DOMAIN_URL,
            BuildConfig.ONE_TRUST_DOMAIN_ID,
            "en",
            null,
            object : OTCallback {
                override fun onSuccess(otSuccessResponse: OTResponse) {
                    val otData = otSuccessResponse.responseData
                    Log.i("OneTrust", otData.toString())
                }

                override fun onFailure(otErrorResponse: OTResponse) {
                    // Use below method to get errorCode and errorMessage.
                    val errorCode = otErrorResponse.responseCode
                    val errorDetails = otErrorResponse.responseMessage
                    Log.i("OneTrust", otErrorResponse.toString())
                }
            })
    }
}