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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class HyBidInterstitialFragment : Fragment(), HyBidInterstitialAd.Listener {
    val TAG = HyBidInterstitialFragment::class.java.simpleName

    private var zoneId: String? = null

    private lateinit var loadButton: Button
    private lateinit var errorView: TextView
    private lateinit var creativeIdView: TextView
    private var interstitial: HyBidInterstitialAd? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_hybid_interstitial, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        creativeIdView = view.findViewById(R.id.view_creative_id)
        loadButton = view.findViewById(R.id.button_load)


        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)

        loadButton.setOnClickListener {
            errorView.text = ""
            val activity = activity as TabActivity
            activity.notifyAdCleaned()
            loadPNAd()
        }

        errorView.setOnClickListener { ClipboardUtils.copyToClipboard(activity!!, errorView.text.toString()) }
        creativeIdView.setOnClickListener { ClipboardUtils.copyToClipboard(activity!!, creativeIdView.text.toString()) }
    }

    override fun onDestroy() {
        interstitial?.destroy()
        super.onDestroy()
    }

    fun loadPNAd() {
        interstitial = HyBidInterstitialAd(activity, zoneId, this)
        interstitial?.setSkipOffset(10)
        interstitial?.load()
    }

    override fun onInterstitialLoaded() {
        Log.d(TAG, "onInterstitialLoaded")
        interstitial?.show()
        displayLogs()
        if (!TextUtils.isEmpty(interstitial?.creativeId)) {
            creativeIdView.text = interstitial?.creativeId
        }
    }

    override fun onInterstitialLoadFailed(error: Throwable?) {
        Log.e(TAG, "onInterstitialLoadFailed", error)
        errorView.text = error?.message
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