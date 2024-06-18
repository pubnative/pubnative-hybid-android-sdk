package net.pubnative.lite.demo.ui.fragments.ironsource

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo
import com.ironsource.mediationsdk.logger.IronSourceError
import com.ironsource.mediationsdk.model.Placement
import com.ironsource.mediationsdk.sdk.LevelPlayRewardedVideoListener
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils

class IronSourceMediationRewardedFragment : Fragment(R.layout.fragment_ironsource_rewarded),
    LevelPlayRewardedVideoListener {
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

    override fun onAdShowFailed(error: IronSourceError?, info: AdInfo?) {
        Log.d(TAG, "onAdShowFailed")
        activity?.let {
            errorView.text = error?.errorMessage
            showButton.isEnabled = false
        }
    }

    override fun onAdOpened(info: AdInfo?) {
        Log.d(TAG, "onAdOpened")
    }

    override fun onAdClosed(info: AdInfo?) {
        activity?.let {
            Log.d(TAG, "onAdClosed")
            showButton.isEnabled = false
        }
    }

    override fun onAdClicked(placement: Placement?, info: AdInfo?) {
        Log.d(TAG, "onAdClicked")
    }

    override fun onAdRewarded(placement: Placement?, info: AdInfo?) {
        Log.d(TAG, "onAdRewarded")
    }

    override fun onAdAvailable(info: AdInfo?) {
        activity?.let {
            Log.d(TAG, "onAdAvailable")
            displayLogs()
            showButton.isEnabled = true
        }
    }

    override fun onAdUnavailable() {
        activity?.let {
            Log.d(TAG, "onAdUnavailable")
            displayLogs()
            showButton.isEnabled = false
        }
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
        IronSource.setLevelPlayRewardedVideoListener(this)
    }

    override fun onStop() {
        IronSource.removeRewardedVideoListener()
        super.onStop()
    }

    private fun initializeIronSource() {
        val settings =
            SettingsManager.getInstance(requireContext()).getSettings().ironSourceSettings
        val appKey = settings?.appKey
        if (!appKey.isNullOrEmpty()) {
            IronSource.setMetaData("is_test_suite", "enable")
            IronSource.init(
                requireActivity(), appKey, IronSource.AD_UNIT.BANNER,
                IronSource.AD_UNIT.INTERSTITIAL, IronSource.AD_UNIT.REWARDED_VIDEO
            )
        }
    }
}