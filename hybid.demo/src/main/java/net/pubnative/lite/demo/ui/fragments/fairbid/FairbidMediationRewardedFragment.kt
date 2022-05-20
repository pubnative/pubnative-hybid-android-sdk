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

class FairbidMediationRewardedFragment : Fragment(R.layout.fragment_fairbid_rewarded),
    RewardedListener {
    val TAG = FairbidMediationRewardedFragment::class.java.simpleName

    private lateinit var loadButton: Button
    private lateinit var showButton: Button
    private lateinit var errorView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        showButton = view.findViewById(R.id.button_show)
        showButton.isEnabled = false

        val adUnitId =
            SettingsManager.getInstance(requireActivity())
                .getSettings().fairbidMediationRewardedAdUnitId

        Rewarded.setRewardedListener(this)

        loadButton.setOnClickListener {
            showButton.isEnabled = false
            Rewarded.request(adUnitId)
        }

        showButton.setOnClickListener {
            if (Rewarded.isAvailable(adUnitId)) {
                Rewarded.show(adUnitId, requireActivity())
            }
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
        Log.d(TAG, "onShow")
        showButton.isEnabled = false
    }

    override fun onClick(placementId: String) {
        Log.d(TAG, "onClick")
    }

    override fun onHide(placementId: String) {
        Log.d(TAG, "onHide")
    }

    override fun onShowFailure(placementId: String, impressionData: ImpressionData) {
        Log.d(TAG, "onShowFailure")
        showButton.isEnabled = false
    }

    override fun onAvailable(placementId: String) {
        Log.d(TAG, "onAvailable")
        showButton.isEnabled = true
        displayLogs()
    }

    override fun onUnavailable(placementId: String) {
        Log.d(TAG, "onUnavailable")
        showButton.isEnabled = false
        displayLogs()
    }

    override fun onCompletion(placementId: String, userRewarded: Boolean) {
        Log.d(TAG, "onCompletion")
    }

    override fun onRequestStart(placementId: String) {
        Log.d(TAG, "onRequestStart")
    }

    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }
}