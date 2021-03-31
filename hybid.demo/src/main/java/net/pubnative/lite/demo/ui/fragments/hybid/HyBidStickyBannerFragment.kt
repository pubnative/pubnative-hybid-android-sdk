package net.pubnative.lite.demo.ui.fragments.hybid

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.models.AdSize
import net.pubnative.lite.sdk.reporting.ReportingEventBridge
import net.pubnative.lite.sdk.views.HyBidAdView
import net.pubnative.lite.sdk.views.PNAdView


/**
 * Created by erosgarciaponte on 30.01.18.
 */

class HyBidStickyBannerFragment : Fragment(), PNAdView.Listener, RadioGroup.OnCheckedChangeListener {

    private val TAG: String = HyBidStickyBannerFragment::class.java.simpleName

    private var zoneId: String? = null

    private var mPosition: HyBidAdView.Position = HyBidAdView.Position.TOP

    private lateinit var hybidBanner: HyBidAdView
    private lateinit var loadButton: Button
    private lateinit var adSizeSpinner: Spinner
    private lateinit var spinnerAdapter: ArrayAdapter<AdSize>
    private lateinit var errorView: TextView
    private lateinit var creativeIdView: TextView

    private lateinit var radioPosition: RadioGroup

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
            inflater.inflate(R.layout.fragment_sticky_top_bottom, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        creativeIdView = view.findViewById(R.id.view_creative_id)
        loadButton = view.findViewById(R.id.button_load)
        hybidBanner = HyBidAdView(activity)
        adSizeSpinner = view.findViewById(R.id.spinner_ad_size)
        radioPosition = view.findViewById(R.id.radioPosition)

        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)

        hybidBanner.setAdSize(AdSize.SIZE_320x50)

        loadButton.setOnClickListener {
            errorView.text = ""
            val activity = activity as TabActivity
            activity.notifyAdCleaned()
            loadPNAd()
        }

        errorView.setOnClickListener { ClipboardUtils.copyToClipboard(requireActivity(), errorView.text.toString()) }
        creativeIdView.setOnClickListener { ClipboardUtils.copyToClipboard(requireActivity(), creativeIdView.text.toString()) }

        spinnerAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, adSizes)
        adSizeSpinner.adapter = spinnerAdapter

        radioPosition.setOnCheckedChangeListener(this)
    }

    fun loadPNAd() {

        val adSize = adSizes[adSizeSpinner.selectedItemPosition]

        hybidBanner.setAdSize(adSize)

        hybidBanner.load(zoneId, mPosition, this)

        val event = ReportingEventBridge("Sticky Banner")
        event.setAdSize(adSize)

        if (HyBid.getReportingController() != null) {
            HyBid.getReportingController().reportEvent(event.reportingEvent)
        }
    }


    // --------------- PNAdView Listener --------------------
    override fun onAdLoaded() {
        Log.d(TAG, "onAdLoaded")
        displayLogs()
    }

    override fun onAdLoadFailed(error: Throwable?) {
        Log.e(TAG, "onAdLoadFailed", error)
        errorView.text = error?.message
        displayLogs()
        creativeIdView.text = ""
    }

    override fun onAdImpression() {
        Log.d(TAG, "onAdImpression")
        if (!TextUtils.isEmpty(hybidBanner.creativeId)) {
            creativeIdView.text = hybidBanner.creativeId
        }
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

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        if (checkedId == R.id.radioTop) {
            mPosition = HyBidAdView.Position.TOP
        } else if (checkedId == R.id.radioBottom) {
            mPosition = HyBidAdView.Position.BOTTOM
        }
    }
}