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
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.*
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd
import java.util.*

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class HyBidInterstitialFragment : Fragment(R.layout.fragment_hybid_interstitial),
    HyBidInterstitialAd.Listener, VideoListener, CacheListener {

    private var isLoadingAd: Boolean = false
    val TAG = HyBidInterstitialFragment::class.java.simpleName

    private var zoneId: String? = null

    private lateinit var loadButton: Button
    private lateinit var prepareButton: Button
    private lateinit var showButton: Button
    private lateinit var cachingCheckbox: CheckBox
    private lateinit var errorCodeView: TextView
    private lateinit var errorView: TextView
    private lateinit var creativeIdView: TextView
    private var interstitial: HyBidInterstitialAd? = null
    private var cachingEnabled: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        errorCodeView = view.findViewById(R.id.view_error_code)
        creativeIdView = view.findViewById(R.id.view_creative_id)
        loadButton = view.findViewById(R.id.button_load)
        prepareButton = view.findViewById(R.id.button_prepare)
        cachingCheckbox = view.findViewById(R.id.check_caching)
        showButton = view.findViewById(R.id.button_show)
        prepareButton.isEnabled = false
        showButton.isEnabled = false

        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)

        loadButton.setOnClickListener {
            cleanLogs()
            prepareButton.isEnabled = false
            showButton.isEnabled = false
            if (!isLoadingAd)
                fireLoadClicked()
        }

        prepareButton.setOnClickListener {
            interstitial?.prepare(this)
        }

        showButton.setOnClickListener {
            val activity = activity as TabActivity
            activity.cacheEventList()
            interstitial?.show()
        }

        cachingCheckbox.setOnCheckedChangeListener { _, isChecked ->
            cachingEnabled = isChecked
            val activity = activity as TabActivity
            activity.cacheEventList()
            prepareButton.visibility = if (isChecked) View.GONE else View.VISIBLE
        }

        errorView.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                requireActivity(),
                errorView.text.toString()
            )
        }

        creativeIdView.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                requireActivity(),
                creativeIdView.text.toString()
            )
        }
    }

    private fun fireLoadClicked() {
        cleanLogs()
        errorView.text = ""
        val activity = activity as TabActivity
        activity.notifyAdCleaned()
        loadInterstitialAd()
    }

    override fun onDestroy() {
        interstitial?.destroy()
        super.onDestroy()
    }

    private fun loadInterstitialAd() {
        interstitial = HyBidInterstitialAd(activity, zoneId, this)
        interstitial?.isAutoCacheOnLoad = cachingEnabled
        //Optional to track video events
        interstitial?.setVideoListener(this)
        interstitial?.load()
        isLoadingAd = true
    }

    override fun onInterstitialLoaded() {
        Log.d(TAG, "onInterstitialLoaded")
        prepareButton.isEnabled = !cachingEnabled
        showButton.isEnabled = cachingEnabled
        displayLogs()
        if (interstitial?.creativeId?.isNotEmpty() == true) {
            creativeIdView.text = interstitial?.creativeId
        }
        isLoadingAd = false
    }

    override fun onInterstitialLoadFailed(error: Throwable?) {
        prepareButton.isEnabled = false
        showButton.isEnabled = false
        if (error != null && error is HyBidError) {
            Log.e(TAG, error.message ?: " - ")
            errorCodeView.text = error.errorCode.code.toString()
            errorView.text = error.message ?: " - "
        } else {
            errorCodeView.text = " - "
            errorView.text = " - "
        }
        displayLogs()
        creativeIdView.text = ""
        isLoadingAd = false
    }

    override fun onInterstitialImpression() {
        Log.d(TAG, "onInterstitialImpression")
        if (HyBid.getDiagnosticsManager() != null) {
            HyBid.getDiagnosticsManager().printPlacementDiagnosticsLog(
                requireContext(),
                interstitial?.placementParams
            )
        }
    }

    override fun onInterstitialDismissed() {
        Log.d(TAG, "onInterstitialDismissed")
        interstitial = null
        prepareButton.isEnabled = false
        showButton.isEnabled = false
    }

    override fun onInterstitialClick() {
        Log.d(TAG, "onInterstitialClick")
    }

    override fun onVideoError(progressPercentage: Int) {
        Log.d(TAG, String.format(Locale.ENGLISH, "onVideoError progress: %d", progressPercentage))
    }

    override fun onVideoStarted() {
        Log.d(TAG, "onVideoStarted")
    }

    override fun onVideoDismissed(progressPercentage: Int) {
        Log.d(
            TAG,
            String.format(Locale.ENGLISH, "onVideoDismissed progress: %d", progressPercentage)
        )
    }

    override fun onVideoFinished() {
        Log.d(TAG, "onVideoFinished")
    }

    override fun onVideoSkipped() {
        Log.d(
            TAG,
            String.format(Locale.ENGLISH, "onVideoSkipped", "")
        )
    }

    override fun onCacheSuccess() {
        Log.d(TAG, "onCacheSuccess")
        prepareButton.isEnabled = false
        showButton.isEnabled = true
    }

    override fun onCacheFailed(error: Throwable?) {
        prepareButton.isEnabled = false
        showButton.isEnabled = true
        if (error != null && error is HyBidError) {
            Log.e(TAG, error.message ?: " - ")
            errorCodeView.text = error.errorCode.code.toString()
            errorView.text = error.message ?: " - "
        } else {
            errorCodeView.text = " - "
            errorView.text = " - "
        }
    }

    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }

    private fun cleanLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdCleaned()
        }
    }
}