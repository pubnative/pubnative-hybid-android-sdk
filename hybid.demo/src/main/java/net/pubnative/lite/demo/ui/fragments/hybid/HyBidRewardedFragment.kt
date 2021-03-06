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
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.models.AdSize
import net.pubnative.lite.sdk.reporting.ReportingEventBridge
import net.pubnative.lite.sdk.rewarded.HyBidRewardedAd

class HyBidRewardedFragment : Fragment(), HyBidRewardedAd.Listener {
    val TAG = HyBidRewardedFragment::class.java.simpleName

    private var zoneId: String? = null

    private lateinit var loadButton: Button
    private lateinit var showButton: Button
    private lateinit var errorView: TextView
    private lateinit var creativeIdView: TextView
    private var rewardedAd: HyBidRewardedAd? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_hybid_rewarded, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        creativeIdView = view.findViewById(R.id.view_creative_id)
        loadButton = view.findViewById(R.id.button_load)
        showButton = view.findViewById(R.id.button_show)


        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)

        loadButton.setOnClickListener {
            errorView.text = ""
            val activity = activity as TabActivity
            activity.notifyAdCleaned()
            loadPNRewardedAd()
        }

        showButton.setOnClickListener {
            rewardedAd?.show()
            displayLogs()
            showButton.isEnabled = false
        }

        errorView.setOnClickListener { ClipboardUtils.copyToClipboard(requireActivity(), errorView.text.toString()) }
        creativeIdView.setOnClickListener { ClipboardUtils.copyToClipboard(requireActivity(), creativeIdView.text.toString()) }
    }


    private fun loadPNRewardedAd() {
        rewardedAd = HyBidRewardedAd(activity, zoneId, this)
        rewardedAd?.load()

        val event = ReportingEventBridge("Standalone Rewarded")
        event.setAdSize(AdSize.SIZE_INTERSTITIAL)

        if (HyBid.getReportingController() != null) {
            HyBid.getReportingController().reportEvent(event.reportingEvent)
        }
    }


    // -------------- Listeners ---------------

    override fun onReward() {
        Log.d(TAG, "onReward")
    }

    override fun onRewardedLoaded() {
        Log.d(TAG, "onRewardedLoaded")
        showButton.isEnabled = true
        if (!TextUtils.isEmpty(rewardedAd?.creativeId)) {
            creativeIdView.text = rewardedAd?.creativeId
        }
    }

    override fun onRewardedOpened() {
        Log.d(TAG, "onRewardedOpened")
    }

    override fun onRewardedClosed() {
        Log.d(TAG, "onRewardedClosed")
    }

    override fun onRewardedClick() {
        Log.d(TAG, "onRewardedClick")
    }

    override fun onRewardedLoadFailed(error: Throwable?) {
        showButton.isEnabled = false
        error?.message?.let {
            Log.e(TAG, it)
            errorView.text = it
        }
        creativeIdView.text = ""
        displayLogs()
    }


    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }
}