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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.demo.util.convertDpToPx
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.models.AdSize
import net.pubnative.lite.sdk.reporting.ReportingEventBridge
import net.pubnative.lite.sdk.views.HyBidAdView
import net.pubnative.lite.sdk.views.PNAdView
import java.util.*
import kotlin.concurrent.schedule


/**
 * Created by erosgarciaponte on 30.01.18.
 */
class HyBidBannerFragment : Fragment(), PNAdView.Listener {
    val TAG = HyBidBannerFragment::class.java.simpleName

    private val AUTO_REFRESH_MILLIS : Long = 30 * 1000

    private var zoneId: String? = null
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var hybidBanner: HyBidAdView
    private lateinit var autoRefreshSwitch: Switch
    private lateinit var loadButton: Button
    private lateinit var adSizeSpinner: Spinner
    private lateinit var spinnerAdapter: ArrayAdapter<AdSize>
    private lateinit var errorView: TextView
    private lateinit var creativeIdView: TextView

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
            AdSize.SIZE_1024x768)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_hybid_banner, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        creativeIdView = view.findViewById(R.id.view_creative_id)
        loadButton = view.findViewById(R.id.button_load)
        hybidBanner = view.findViewById(R.id.hybid_banner)
        adSizeSpinner = view.findViewById(R.id.spinner_ad_size)
        autoRefreshSwitch = view.findViewById(R.id.check_auto_refresh)

        autoRefreshSwitch.isChecked = false

        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)

        hybidBanner.setAdSize(AdSize.SIZE_320x50)

        loadButton.setOnClickListener {
            val activity = activity as TabActivity
            handler.removeCallbacksAndMessages(null)
            activity.notifyAdCleaned()
            loadPNAd()
            autoRefresh()
        }

        errorView.setOnClickListener { ClipboardUtils.copyToClipboard(requireActivity(), errorView.text.toString()) }
        creativeIdView.setOnClickListener { ClipboardUtils.copyToClipboard(requireActivity(), creativeIdView.text.toString()) }

        spinnerAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, adSizes)
        adSizeSpinner.adapter = spinnerAdapter
    }

    fun loadPNAd() {

        errorView.text = ""

        val adSize = adSizes[adSizeSpinner.selectedItemPosition]

        hybidBanner.setAdSize(adSize)

        val layoutParams = LinearLayout.LayoutParams(
                convertDpToPx(requireContext(), adSize.width.toFloat()),
                convertDpToPx(requireContext(), adSize.height.toFloat()))
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL

        hybidBanner.layoutParams = layoutParams

        hybidBanner.load(zoneId, this)

        val event = ReportingEventBridge("Standalone Banner")
        event.setAdSize(adSize)

        if (HyBid.getReportingController() != null) {
            HyBid.getReportingController().reportEvent(event.reportingEvent)
        }
    }

    fun autoRefresh(){
        if (autoRefreshSwitch.isChecked){
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
        displayLogs()
        if (!TextUtils.isEmpty(hybidBanner.creativeId)) {
            creativeIdView.text = hybidBanner.creativeId
        }
    }

    override fun onAdLoadFailed(error: Throwable?) {
        Log.e(TAG, "onAdLoadFailed", error)
        errorView.text = error?.message
        displayLogs()
        creativeIdView.text = ""
    }

    override fun onAdImpression() {
        Log.d(TAG, "onAdImpression")
    }

    override fun onAdClick() {
        Log.d(TAG, "onAdClick")
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