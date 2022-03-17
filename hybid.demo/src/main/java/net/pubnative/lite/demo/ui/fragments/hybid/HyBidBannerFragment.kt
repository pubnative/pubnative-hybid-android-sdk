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
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.demo.util.convertDpToPx
import net.pubnative.lite.sdk.CacheListener
import net.pubnative.lite.sdk.DiagnosticsManager
import net.pubnative.lite.sdk.HyBidError
import net.pubnative.lite.sdk.VideoListener
import net.pubnative.lite.sdk.models.AdSize
import net.pubnative.lite.sdk.views.HyBidAdView
import net.pubnative.lite.sdk.views.PNAdView
import java.util.*

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class HyBidBannerFragment : Fragment(R.layout.fragment_hybid_banner), PNAdView.Listener,
    VideoListener, CacheListener {
    val TAG = HyBidBannerFragment::class.java.simpleName

    private val AUTO_REFRESH_MILLIS: Long = 30 * 1000

    private var zoneId: String? = null
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var hybidBanner: HyBidAdView
    private lateinit var autoRefreshSwitch: Switch
    private lateinit var loadButton: Button
    private lateinit var prepareButton: Button
    private lateinit var showButton: Button
    private lateinit var cachingCheckbox: CheckBox
    private lateinit var adSizeSpinner: Spinner
    private lateinit var spinnerAdapter: ArrayAdapter<AdSize>
    private lateinit var errorCodeView: TextView
    private lateinit var errorView: TextView
    private lateinit var creativeIdView: TextView
    private var cachingEnabled: Boolean = true

    private val adSizes = arrayOf(
        AdSize.SIZE_320x50,
        AdSize.SIZE_160x600,
        AdSize.SIZE_250x250,
        AdSize.SIZE_300x50,
        AdSize.SIZE_300x250,
        AdSize.SIZE_300x600,
        AdSize.SIZE_320x100,
        AdSize.SIZE_320x480,
        AdSize.SIZE_480x320,
        AdSize.SIZE_728x90,
        AdSize.SIZE_768x1024,
        AdSize.SIZE_1024x768
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        errorCodeView = view.findViewById(R.id.view_error_code)
        creativeIdView = view.findViewById(R.id.view_creative_id)
        loadButton = view.findViewById(R.id.button_load)
        prepareButton = view.findViewById(R.id.button_prepare)
        showButton = view.findViewById(R.id.button_show)
        cachingCheckbox = view.findViewById(R.id.check_caching)
        hybidBanner = view.findViewById(R.id.hybid_banner)
        adSizeSpinner = view.findViewById(R.id.spinner_ad_size)
        autoRefreshSwitch = view.findViewById(R.id.check_auto_refresh)
        prepareButton.isEnabled = false
        showButton.isEnabled = false

        autoRefreshSwitch.isChecked = false

        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)

        //Optional to track progress of video ads
        hybidBanner.setVideoListener(this)
        hybidBanner.setAdSize(AdSize.SIZE_320x50)

        loadButton.setOnClickListener {
            val activity = activity as TabActivity
            handler.removeCallbacksAndMessages(null)
            activity.notifyAdCleaned()
            loadPNAd()
            autoRefresh()
        }

        prepareButton.setOnClickListener {
            hybidBanner.prepare(this)
        }

        showButton.setOnClickListener {
            hybidBanner.show()
        }

        cachingCheckbox.setOnCheckedChangeListener { _, isChecked ->
            cachingEnabled = isChecked
            prepareButton.visibility = if (isChecked) View.GONE else View.VISIBLE
            showButton.visibility = if (isChecked) View.GONE else View.VISIBLE
            hybidBanner.isAutoShowOnLoad = isChecked
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

        spinnerAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, adSizes)
        adSizeSpinner.adapter = spinnerAdapter
    }

    fun loadPNAd() {

        errorView.text = ""

        val adSize = adSizes[adSizeSpinner.selectedItemPosition]

        hybidBanner.setAdSize(adSize)

        val layoutParams = LinearLayout.LayoutParams(
            convertDpToPx(requireContext(), adSize.width.toFloat()),
            convertDpToPx(requireContext(), adSize.height.toFloat())
        )
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL

        hybidBanner.layoutParams = layoutParams

        hybidBanner.isAutoCacheOnLoad = cachingEnabled

        hybidBanner.load(zoneId, this)
    }

    fun autoRefresh() {
        if (autoRefreshSwitch.isChecked) {
            handler.postDelayed({
                loadPNAd()
                autoRefresh()
            }, AUTO_REFRESH_MILLIS)
        } else {
            handler.removeCallbacksAndMessages(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null);
    }

    // --------------- PNAdView Listener --------------------
    override fun onAdLoaded() {
        Log.d(TAG, "onAdLoaded")
        prepareButton.isEnabled = !cachingEnabled
        showButton.isEnabled = cachingEnabled
        displayLogs()
        if (!TextUtils.isEmpty(hybidBanner.creativeId)) {
            creativeIdView.text = hybidBanner.creativeId
        }
    }

    override fun onAdLoadFailed(error: Throwable?) {
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
    }

    override fun onAdImpression() {
        Log.d(TAG, "onAdImpression")
        DiagnosticsManager.printPlacementDiagnosticsLog(
            requireContext(),
            hybidBanner.placementParams
        )
    }

    override fun onAdClick() {
        Log.d(TAG, "onAdClick")
    }

    // --------------- HyBid Video Listener --------------------
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

    override fun onDestroy() {
        hybidBanner.destroy()
        super.onDestroy()
    }

}