package net.pubnative.lite.demo.ui.fragments.fairbid

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.fyber.fairbid.ads.ImpressionData
import com.fyber.fairbid.ads.Rewarded
import com.fyber.fairbid.ads.rewarded.RewardedListener
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.vpaid.enums.AudioState

val FairbidMediationRewardedFragment_TAG: String =
    FairbidMediationRewardedFragment::class.java.simpleName

class FairbidMediationRewardedFragment : Fragment(R.layout.fragment_fairbid_mediation_rewarded),
    RewardedListener {

    private var adUnitId: String? = null
    private lateinit var loadButton: Button
    private lateinit var showButton: Button
    private lateinit var errorView: TextView

    private lateinit var videoAudioStatus: AudioState

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        showButton = view.findViewById(R.id.button_show)
        showButton.isEnabled = false

        videoAudioStatus = HyBid.getVideoAudioStatus()

        adUnitId =
            SettingsManager.getInstance(requireActivity())
                .getSettings().fairbidSettings?.mediationRewardedAdUnitId

        Rewarded.setRewardedListener(this)
        adUnitId?.let { Rewarded.disableAutoRequesting(it) }

        loadButton.setOnClickListener {
            showButton.isEnabled = false
            adUnitId?.let { it1 -> Rewarded.request(it1) }
        }

        showButton.setOnClickListener {
            if (adUnitId != null && Rewarded.isAvailable(adUnitId!!)) {
                Rewarded.show(adUnitId!!, requireActivity())
            }
            showButton.isEnabled = false
        }

        errorView.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                requireActivity(),
                errorView.text.toString()
            )
        }
    }

    // Fairbid Rewarded Listeners
    override fun onShow(placementId: String, impressionData: ImpressionData) {
        Log.d(FairbidMediationRewardedFragment_TAG, "onShow")
        showButton.isEnabled = false
    }

    override fun onClick(placementId: String) {
        Log.d(FairbidMediationRewardedFragment_TAG, "onClick")
    }

    override fun onHide(placementId: String) {
        Log.d(FairbidMediationRewardedFragment_TAG, "onHide")
    }

    override fun onShowFailure(placementId: String, impressionData: ImpressionData) {
        Log.d(FairbidMediationRewardedFragment_TAG, "onShowFailure")
        showButton.isEnabled = false
    }

    override fun onAvailable(placementId: String) {
        Log.d(FairbidMediationRewardedFragment_TAG, "onAvailable")
        showButton.isEnabled = true
        displayLogs()
    }

    override fun onUnavailable(placementId: String) {
        Log.d(FairbidMediationRewardedFragment_TAG, "onUnavailable")
        showButton.isEnabled = false
        displayLogs()
    }

    override fun onCompletion(placementId: String, userRewarded: Boolean) {
        Log.d(FairbidMediationRewardedFragment_TAG, "onCompletion")
    }

    override fun onRequestStart(placementId: String, requestId: String) {
        Log.d(FairbidMediationRewardedFragment_TAG, "onRequestStart")
    }

    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        HyBid.setVideoAudioStatus(videoAudioStatus)
    }
}