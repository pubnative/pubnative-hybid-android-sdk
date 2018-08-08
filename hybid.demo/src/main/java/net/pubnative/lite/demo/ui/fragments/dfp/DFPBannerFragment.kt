// The MIT License (MIT)
//
// Copyright (c) 2018 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package net.pubnative.lite.demo.ui.fragments.dfp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.doubleclick.PublisherAdView
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.sdk.api.BannerRequestManager
import net.pubnative.lite.sdk.api.RequestManager
import net.pubnative.lite.sdk.models.Ad
import net.pubnative.lite.sdk.utils.PrebidUtils

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class DFPBannerFragment : Fragment(), RequestManager.RequestListener {
    val TAG = DFPBannerFragment::class.java.simpleName

    private lateinit var requestManager: RequestManager
    private var zoneId: String? = null
    private var adUnitId: String? = null

    private lateinit var dfpBanner: PublisherAdView
    private lateinit var dfpBannerContainer: FrameLayout
    private lateinit var loadButton: Button
    private lateinit var impressionIdView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_dfp_banner, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadButton = view.findViewById(R.id.button_load)
        impressionIdView = view.findViewById(R.id.view_impression_id)
        dfpBannerContainer = view.findViewById(R.id.dfp_banner_container)

        requestManager = BannerRequestManager()

        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)
        adUnitId = SettingsManager.getInstance(activity!!).getSettings().dfpBannerAdUnitId

        dfpBanner = PublisherAdView(activity)
        dfpBanner.adUnitId = adUnitId
        dfpBanner.setAdSizes(AdSize.BANNER)
        dfpBanner.adListener = adListener

        dfpBannerContainer.addView(dfpBanner)

        loadButton.setOnClickListener {
            loadPNAd()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dfpBanner.destroy()
    }

    fun loadPNAd() {
        requestManager.setZoneId(zoneId)
        requestManager.setRequestListener(this)
        requestManager.requestAd()
    }

    // --------------- HyBid Request Listener --------------------
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

        dfpBanner.loadAd(adRequest)

        if (!TextUtils.isEmpty(ad?.impressionId)) {
            impressionIdView.text = ad?.impressionId
        }
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