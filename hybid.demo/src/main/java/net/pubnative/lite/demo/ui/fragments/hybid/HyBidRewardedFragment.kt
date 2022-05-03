package net.pubnative.lite.demo.ui.fragments.hybid

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
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.CacheListener
import net.pubnative.lite.sdk.DiagnosticsManager
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.HyBidError
import net.pubnative.lite.sdk.rewarded.HyBidRewardedAd

class HyBidRewardedFragment : Fragment(R.layout.fragment_hybid_rewarded), HyBidRewardedAd.Listener, CacheListener {
    val TAG = HyBidRewardedFragment::class.java.simpleName

    private var zoneId: String? = null

    private lateinit var loadButton: Button
    private lateinit var prepareButton: Button
    private lateinit var showButton: Button
    private lateinit var cachingCheckbox: CheckBox
    private lateinit var errorCodeView: TextView
    private lateinit var errorView: TextView
    private lateinit var creativeIdView: TextView
    private var rewardedAd: HyBidRewardedAd? = null
    private var cachingEnabled: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        errorCodeView = view.findViewById(R.id.view_error_code)
        creativeIdView = view.findViewById(R.id.view_creative_id)
        loadButton = view.findViewById(R.id.button_load)
        prepareButton = view.findViewById(R.id.button_prepare)
        showButton = view.findViewById(R.id.button_show)
        cachingCheckbox = view.findViewById(R.id.check_caching)
        prepareButton.isEnabled = false
        showButton.isEnabled = false

        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)

        loadButton.setOnClickListener {
            errorView.text = ""
            prepareButton.isEnabled = false
            showButton.isEnabled = false
            val activity = activity as TabActivity
            activity.notifyAdCleaned()
            loadPNRewardedAd()
        }

        prepareButton.setOnClickListener {
            rewardedAd?.prepare(this)
        }

        showButton.setOnClickListener {
            rewardedAd?.show()
        }

        cachingCheckbox.setOnCheckedChangeListener { _, isChecked ->
            cachingEnabled = isChecked
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


    private fun loadPNRewardedAd() {
        rewardedAd = HyBidRewardedAd(activity, zoneId, this)
        rewardedAd?.isAutoCacheOnLoad = cachingEnabled
        rewardedAd?.load()
    }


    // -------------- Listeners ---------------

    override fun onRewardedLoaded() {
        Log.d(TAG, "onRewardedLoaded")
        prepareButton.isEnabled = !cachingEnabled
        showButton.isEnabled = cachingEnabled
        displayLogs()
        if (!TextUtils.isEmpty(rewardedAd?.creativeId)) {
            creativeIdView.text = rewardedAd?.creativeId
        }
    }

    override fun onRewardedLoadFailed(error: Throwable?) {
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

    override fun onRewardedOpened() {
        Log.d(TAG, "onRewardedOpened")
        if (HyBid.getDiagnosticsManager() != null) {
            HyBid.getDiagnosticsManager().printPlacementDiagnosticsLog(
                requireContext(),
                rewardedAd?.placementParams
            )
        }
    }

    override fun onRewardedClosed() {
        Log.d(TAG, "onRewardedClosed")
        rewardedAd = null
        prepareButton.isEnabled = false
        showButton.isEnabled = false
    }

    override fun onRewardedClick() {
        Log.d(TAG, "onRewardedClick")
    }

    override fun onReward() {
        Log.d(TAG, "onReward")
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
}