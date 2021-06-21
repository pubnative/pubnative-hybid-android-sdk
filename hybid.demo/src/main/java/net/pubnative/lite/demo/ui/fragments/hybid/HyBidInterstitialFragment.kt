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
package net.pubnative.lite.demo.ui.fragments.hybid

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd
import net.pubnative.lite.sdk.models.AdSize
import net.pubnative.lite.sdk.reporting.ReportingEventBridge

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class HyBidInterstitialFragment : Fragment(R.layout.fragment_hybid_interstitial), HyBidInterstitialAd.Listener {

    val TAG = HyBidInterstitialFragment::class.java.simpleName

    private var zoneId: String? = null

    private lateinit var loadButton: Button
    private lateinit var showButton: Button
    private lateinit var errorView: TextView
    private lateinit var creativeIdView: TextView
    private var interstitial: HyBidInterstitialAd? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        creativeIdView = view.findViewById(R.id.view_creative_id)
        loadButton = view.findViewById(R.id.button_load)
        showButton = view.findViewById(R.id.button_show)
        showButton.isEnabled = false

        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)

        loadButton.setOnClickListener {
            errorView.text = ""
            val activity = activity as TabActivity
            activity.notifyAdCleaned()
            loadInterstitialAd()
        }

        showButton.setOnClickListener {
            interstitial?.show()
        }

        errorView.setOnClickListener { ClipboardUtils.copyToClipboard(requireActivity(), errorView.text.toString()) }
        creativeIdView.setOnClickListener { ClipboardUtils.copyToClipboard(requireActivity(), creativeIdView.text.toString()) }
    }

    override fun onDestroy() {
        interstitial?.destroy()
        super.onDestroy()
    }

    private fun loadInterstitialAd() {
        interstitial = HyBidInterstitialAd(activity, zoneId, this)
        interstitial?.setSkipOffset(10)
        interstitial?.load()

        val event = ReportingEventBridge("Standalone Interstitial")
        event.setAdSize(AdSize.SIZE_INTERSTITIAL)

        if (HyBid.getReportingController() != null) {
            HyBid.getReportingController().reportEvent(event.reportingEvent)
        }
    }

    override fun onInterstitialLoaded() {
        Log.d(TAG, "onInterstitialLoaded")
        showButton.isEnabled = true
        displayLogs()
        if (!TextUtils.isEmpty(interstitial?.creativeId)) {
            creativeIdView.text = interstitial?.creativeId
        }
    }

    override fun onInterstitialLoadFailed(error: Throwable?) {
        error?.message?.let {
            Log.e(TAG, it)
            errorView.text = it
        }
        creativeIdView.text = ""
        displayLogs()
    }

    override fun onInterstitialImpression() {
        Log.d(TAG, "onInterstitialImpression")
    }

    override fun onInterstitialDismissed() {
        Log.d(TAG, "onInterstitialDismissed")
    }

    override fun onInterstitialClick() {
        Log.d(TAG, "onInterstitialClick")
    }

    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }
}