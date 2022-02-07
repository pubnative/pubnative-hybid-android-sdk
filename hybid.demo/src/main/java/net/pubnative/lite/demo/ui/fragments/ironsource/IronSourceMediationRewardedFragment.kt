package net.pubnative.lite.demo.ui.fragments.ironsource

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.logger.IronSourceError
import com.ironsource.mediationsdk.model.Placement
import com.ironsource.mediationsdk.sdk.RewardedVideoListener
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils

class IronSourceMediationRewardedFragment : Fragment(R.layout.fragment_ironsource_rewarded),
    RewardedVideoListener {
    val TAG = IronSourceMediationRewardedFragment::class.java.simpleName

    private lateinit var showButton: Button
    private lateinit var errorView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        IronSource.setRewardedVideoListener(this)

        errorView = view.findViewById(R.id.view_error)
        showButton = view.findViewById(R.id.button_show)

        val adUnitId =
            SettingsManager.getInstance(requireActivity()).getSettings().ironSourceRewardedAdUnitId

        showButton.isEnabled = IronSource.isRewardedVideoAvailable()

        showButton.setOnClickListener {
            if (IronSource.isRewardedVideoAvailable()) {
                IronSource.showRewardedVideo(adUnitId)
            }
        }

        errorView.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                requireActivity(),
                errorView.text.toString()
            )
        }
    }

    override fun onDestroy() {
        IronSource.removeRewardedVideoListener()
        super.onDestroy()
    }

    override fun onRewardedVideoAvailabilityChanged(available: Boolean) {
        if (available) {
            Log.d(TAG, "onRewardedVideoAvailabilityChanged: available")
            displayLogs()
            showButton.isEnabled = true
        } else {
            Log.d(TAG, "onRewardedVideoAvailabilityChanged: unavailable")
            displayLogs()
            showButton.isEnabled = false
        }
    }

    override fun onRewardedVideoAdOpened() {
        Log.d(TAG, "onRewardedVideoAdOpened")
    }

    override fun onRewardedVideoAdClosed() {
        Log.d(TAG, "onRewardedVideoAdClosed")
        showButton.isEnabled = false
    }

    override fun onRewardedVideoAdStarted() {
        Log.d(TAG, "onRewardedVideoAdStarted")
    }

    override fun onRewardedVideoAdEnded() {
        Log.d(TAG, "onRewardedVideoAdEnded")
    }

    override fun onRewardedVideoAdRewarded(placement: Placement?) {
        Log.d(TAG, "onRewardedVideoAdRewarded")
    }

    override fun onRewardedVideoAdShowFailed(error: IronSourceError?) {
        Log.d(TAG, "onRewardedVideoAdShowFailed")
        errorView.text = error?.errorMessage
        showButton.isEnabled = false
    }

    override fun onRewardedVideoAdClicked(placement: Placement?) {
        Log.d(TAG, "onRewardedVideoAdClicked")
    }

    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }
}