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
package net.pubnative.lite.demo.ui.fragments.mopub

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mopub.mobileads.MoPubErrorCode
import com.mopub.mobileads.MoPubInterstitial
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.api.InterstitialRequestManager
import net.pubnative.lite.sdk.api.RequestManager
import net.pubnative.lite.sdk.models.Ad
import net.pubnative.lite.sdk.utils.HeaderBiddingUtils

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class MoPubInterstitialFragment : Fragment(), RequestManager.RequestListener, MoPubInterstitial.InterstitialAdListener {
    val TAG = MoPubInterstitialFragment::class.java.simpleName

    private lateinit var requestManager: RequestManager
    private lateinit var mopubInterstitial: MoPubInterstitial
    private var zoneId: String? = null
    private var adUnitId: String? = null
    private var ad: Ad? = null

    private lateinit var loadButton: Button
    private lateinit var prepareButton: Button
    private lateinit var showButton: Button
    private lateinit var cachingCheckbox: CheckBox
    private lateinit var errorView: TextView
    private lateinit var creativeIdView: TextView

    private var cachingEnabled: Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_mopub_interstitial, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        view.findViewById<TextView>(R.id.label_creative_id).visibility = View.VISIBLE
        creativeIdView = view.findViewById(R.id.view_creative_id)
        creativeIdView.visibility = View.VISIBLE
        loadButton = view.findViewById(R.id.button_load)
        prepareButton = view.findViewById(R.id.button_prepare)
        showButton = view.findViewById(R.id.button_show)
        cachingCheckbox = view.findViewById(R.id.check_caching)
        cachingCheckbox.visibility = View.VISIBLE
        prepareButton.isEnabled = false
        showButton.isEnabled = false

        adUnitId = SettingsManager.getInstance(requireActivity()).getSettings().mopubInterstitialAdUnitId

        requestManager = InterstitialRequestManager()
        mopubInterstitial = MoPubInterstitial(requireActivity(), adUnitId!!)
        mopubInterstitial.interstitialAdListener = this

        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)

        loadButton.setOnClickListener {
            errorView.text = ""
            val activity = activity as TabActivity
            activity.notifyAdCleaned()
            loadPNAd()
        }

        prepareButton.setOnClickListener {
            ad?.let { ad ->
                requestManager.cacheAd(ad)
            }
        }

        showButton.setOnClickListener{
            mopubInterstitial.show()
        }

        cachingCheckbox.setOnCheckedChangeListener { _, isChecked ->
            cachingEnabled = isChecked
            prepareButton.visibility = if (isChecked) View.GONE else View.VISIBLE
        }

        errorView.setOnClickListener { ClipboardUtils.copyToClipboard(requireActivity(), errorView.text.toString()) }
        creativeIdView.setOnClickListener { ClipboardUtils.copyToClipboard(requireActivity(), creativeIdView.text.toString()) }
    }

    override fun onDestroy() {
        super.onDestroy()
        mopubInterstitial.destroy()
    }

    fun loadPNAd() {
        requestManager.setZoneId(zoneId)
        requestManager.setRequestListener(this)
        requestManager.isAutoCacheOnLoad = cachingEnabled
        requestManager.requestAd()
    }

    // --------------- HyBid Request Listener --------------------
    override fun onRequestSuccess(ad: Ad?) {
        this.ad = ad
        mopubInterstitial.setKeywords(HeaderBiddingUtils.getHeaderBiddingKeywords(ad))
        mopubInterstitial.load()

        Log.d(TAG, "onRequestSuccess")
        displayLogs()
        if (!TextUtils.isEmpty(ad?.creativeId)) {
            creativeIdView.text = ad?.creativeId
        }
    }

    override fun onRequestFail(throwable: Throwable?) {
        Log.d(TAG, "onRequestFail: ", throwable)
        ad = null
        errorView.text = throwable?.message
        creativeIdView.text = ""
        displayLogs()
    }

    // ------------- MoPub Interstitial Listener ------------------
    override fun onInterstitialLoaded(interstitial: MoPubInterstitial?) {
        showButton.isEnabled = true
        prepareButton.isEnabled = !cachingEnabled
        Log.d(TAG, "onInterstitialLoaded")
    }

    override fun onInterstitialFailed(interstitial: MoPubInterstitial?, errorCode: MoPubErrorCode?) {
        prepareButton.isEnabled = false
        showButton.isEnabled = false
        Log.d(TAG, "onInterstitialFailed")
    }

    override fun onInterstitialShown(interstitial: MoPubInterstitial?) {
        Log.d(TAG, "onInterstitialShown")
    }

    override fun onInterstitialDismissed(interstitial: MoPubInterstitial?) {
        Log.d(TAG, "onInterstitialDismissed")
        prepareButton.isEnabled = false
        showButton.isEnabled = false
    }

    override fun onInterstitialClicked(interstitial: MoPubInterstitial?) {
        Log.d(TAG, "onInterstitialClicked")
    }

    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }
}