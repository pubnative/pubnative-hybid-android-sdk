package net.pubnative.lite.demo.ui.fragments.dfp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.doubleclick.PublisherAdView
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.sdk.api.MRectRequestManager
import net.pubnative.lite.sdk.api.RequestManager
import net.pubnative.lite.sdk.models.Ad
import net.pubnative.lite.sdk.utils.PrebidUtils

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class DFPMRectFragment : Fragment(), RequestManager.RequestListener {
    val TAG = DFPMRectFragment::class.java.simpleName

    private lateinit var requestManager: RequestManager
    private var zoneId: String? = null
    private var adUnitId: String? = null

    private lateinit var dfpMRect: PublisherAdView
    private lateinit var dfpMRectContainer: FrameLayout
    private lateinit var loadButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_dfp_mrect, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadButton = view.findViewById(R.id.button_load)
        dfpMRectContainer = view.findViewById(R.id.dfp_mrect_container)

        requestManager = MRectRequestManager()

        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)
        adUnitId = SettingsManager.getInstance(activity!!).getSettings().dfpMediumAdUnitId

        dfpMRect = PublisherAdView(activity)
        dfpMRect.adUnitId = adUnitId
        dfpMRect.setAdSizes(AdSize.MEDIUM_RECTANGLE)

        dfpMRectContainer.addView(dfpMRect)

        loadButton.setOnClickListener {
            loadPNAd()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dfpMRect.destroy()
    }

    fun loadPNAd() {
        requestManager.setZoneId(zoneId)
        requestManager.setRequestListener(this)
        requestManager.requestAd()
    }

    // --------------- PNLite Request Listener --------------------
    override fun onRequestSuccess(ad: Ad?) {
        val builder = PublisherAdRequest.Builder()

        val keywordSet = PrebidUtils.getPrebidKeywordsSet(ad, zoneId)
        for (key in keywordSet) {
            builder.addKeyword(key)
        }

        val keywordBundle = PrebidUtils.getPrebidKeywordsBundle(ad, zoneId)
        for (key in keywordBundle.keySet()) {
            builder.addCustomTargeting(key, keywordBundle.getString(key))
        }

        val adRequest = builder.build()
        dfpMRect.loadAd(adRequest)
        Log.d(TAG, "onRequestSuccess")
    }

    override fun onRequestFail(throwable: Throwable?) {
        Log.d(TAG, "onRequestFail: ", throwable)
        Toast.makeText(activity, throwable?.message, Toast.LENGTH_SHORT).show()
    }

    // ---------------- DFP Ad Listener ---------------------
    private val adListener = object : AdListener() {
        override fun onAdLoaded() {
            super.onAdLoaded()
            Log.d(TAG, "onAdLoaded")
        }

        override fun onAdFailedToLoad(errorCode: Int) {
            super.onAdFailedToLoad(errorCode)
            Log.d(TAG, "onAdFailedToLoad")
        }

        override fun onAdImpression() {
            super.onAdImpression()
            Log.d(TAG, "onAdImpression")
        }

        override fun onAdClicked() {
            super.onAdClicked()
            Log.d(TAG, "onAdClicked")
        }

        override fun onAdOpened() {
            super.onAdOpened()
            Log.d(TAG, "onAdOpened")
        }

        override fun onAdLeftApplication() {
            super.onAdLeftApplication()
            Log.d(TAG, "onAdLeftApplication")
        }

        override fun onAdClosed() {
            super.onAdClosed()
            Log.d(TAG, "onAdClosed")
        }
    }
}