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
import com.ironsource.mediationsdk.sdk.LevelPlayInterstitialListener
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils

class IronSourceMediationInterstitialFragment : Fragment(R.layout.fragment_ironsource_interstitial),
    LevelPlayInterstitialListener {
    val TAG = IronSourceMediationInterstitialFragment::class.java.simpleName

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
                .getSettings().ironSourceSettings?.interstitialAdUnitId

        initializeIronSource()

        loadButton.setOnClickListener {
            errorView.text = ""
            IronSource.loadInterstitial()
        }

        showButton.setOnClickListener {
            if (IronSource.isInterstitialReady()) {
                IronSource.showInterstitial(adUnitId)
            }
        }

        errorView.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                requireActivity(),
                errorView.text.toString()
            )
        }

        IronSource.setLevelPlayInterstitialListener(this)
    }

    override fun onDestroy() {
        IronSource.removeInterstitialListener()
        super.onDestroy()
    }

    override fun onAdReady(info: AdInfo?) {
        Log.d(TAG, "onAdReady")
        displayLogs()
        showButton.isEnabled = true
    }

    override fun onAdLoadFailed(error: IronSourceError?) {
        Log.d(TAG, "onAdLoadFailed")
        displayLogs()
        errorView.text = error?.errorMessage
        showButton.isEnabled = false
    }

    override fun onAdOpened(info: AdInfo?) {
        Log.d(TAG, "onAdOpened")
    }

    override fun onAdShowSucceeded(info: AdInfo?) {
        Log.d(TAG, "onAdShowSucceeded")
    }

    override fun onAdShowFailed(error: IronSourceError?, info: AdInfo?) {
        Log.d(TAG, "onAdShowFailed")
    }

    override fun onAdClicked(info: AdInfo?) {
        Log.d(TAG, "onAdClicked")
    }

    override fun onAdClosed(info: AdInfo?) {
        Log.d(TAG, "onAdClosed")
        showButton.isEnabled = false
    }

    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
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