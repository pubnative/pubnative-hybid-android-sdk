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
import net.pubnative.lite.sdk.DiagnosticsManager
import net.pubnative.lite.sdk.HyBidError
import net.pubnative.lite.sdk.rewarded.HyBidRewardedAd

class HyBidRewardedFragment : Fragment(R.layout.fragment_hybid_rewarded), HyBidRewardedAd.Listener {
    val TAG = HyBidRewardedFragment::class.java.simpleName

    private var zoneId: String? = null

    private lateinit var loadButton: Button
    private lateinit var showButton: Button
    private lateinit var errorCodeView: TextView
    private lateinit var errorView: TextView
    private lateinit var creativeIdView: TextView
    private var rewardedAd: HyBidRewardedAd? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        errorCodeView = view.findViewById(R.id.view_error_code)
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
            showButton.isEnabled = false
        }

        errorView.setOnClickListener { ClipboardUtils.copyToClipboard(requireActivity(), errorView.text.toString()) }
        creativeIdView.setOnClickListener { ClipboardUtils.copyToClipboard(requireActivity(), creativeIdView.text.toString()) }
    }


    private fun loadPNRewardedAd() {
        rewardedAd = HyBidRewardedAd(activity, zoneId, this)
        rewardedAd?.load()
    }


    // -------------- Listeners ---------------

    override fun onReward() {
        Log.d(TAG, "onReward")
    }

    override fun onRewardedLoaded() {
        Log.d(TAG, "onRewardedLoaded")
        showButton.isEnabled = true
        displayLogs()
        if (!TextUtils.isEmpty(rewardedAd?.creativeId)) {
            creativeIdView.text = rewardedAd?.creativeId
        }
    }

    override fun onRewardedOpened() {
        Log.d(TAG, "onRewardedOpened")
        DiagnosticsManager.printPlacementDiagnosticsLog(requireContext(), rewardedAd?.placementParams)
    }

    override fun onRewardedClosed() {
        Log.d(TAG, "onRewardedClosed")
    }

    override fun onRewardedClick() {
        Log.d(TAG, "onRewardedClick")
    }

    override fun onRewardedLoadFailed(error: Throwable?) {
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


    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }
}