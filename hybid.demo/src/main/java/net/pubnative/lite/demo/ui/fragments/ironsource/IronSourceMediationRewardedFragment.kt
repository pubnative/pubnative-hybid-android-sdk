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

        errorView = view.findViewById(R.id.view_error)
        showButton = view.findViewById(R.id.button_show)

        val adUnitId =
            SettingsManager.getInstance(requireActivity())
                .getSettings().ironSourceSettings?.rewardedAdUnitId

        initializeIronSource()

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

    override fun onRewardedVideoAvailabilityChanged(available: Boolean) {
        activity?.let {
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
    }

    override fun onRewardedVideoAdOpened() {
        Log.d(TAG, "onRewardedVideoAdOpened")
    }

    override fun onRewardedVideoAdClosed() {
        activity?.let {
            Log.d(TAG, "onRewardedVideoAdClosed")
            showButton.isEnabled = false
        }
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
        activity?.let {
            errorView.text = error?.errorMessage
            showButton.isEnabled = false
        }
    }

    override fun onRewardedVideoAdClicked(placement: Placement?) {
        Log.d(TAG, "onRewardedVideoAdClicked")
    }

    private fun displayLogs() {
        activity?.let {
            if (it is TabActivity) {
                it.notifyAdUpdated()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        IronSource.setRewardedVideoListener(this)
    }

    override fun onStop() {
        IronSource.removeRewardedVideoListener()
        super.onStop()
    }

    private fun initializeIronSource() {
        val settings =
            SettingsManager.getInstance(requireContext()).getSettings().ironSourceSettings
        val appKey = settings?.appKey
        if (appKey != null && appKey.isNotEmpty()) {
            IronSource.init(
                requireActivity(), appKey, IronSource.AD_UNIT.BANNER,
                IronSource.AD_UNIT.INTERSTITIAL, IronSource.AD_UNIT.REWARDED_VIDEO
            )
        }
    }
}