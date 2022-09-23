package net.pubnative.lite.demo.ui.fragments.ironsource

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.logger.IronSourceError
import com.ironsource.mediationsdk.sdk.InterstitialListener
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils

class IronSourceMediationInterstitialFragment : Fragment(R.layout.fragment_ironsource_interstitial),
    InterstitialListener {
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

        IronSource.setInterstitialListener(this)
    }

    override fun onDestroy() {
        IronSource.removeInterstitialListener()
        super.onDestroy()
    }

    override fun onInterstitialAdReady() {
        Log.d(TAG, "onInterstitialAdReady")
        displayLogs()
        showButton.isEnabled = true
    }

    override fun onInterstitialAdLoadFailed(error: IronSourceError?) {
        Log.d(TAG, "onInterstitialAdLoadFailed")
        displayLogs()
        errorView.text = error?.errorMessage
        showButton.isEnabled = false
    }

    override fun onInterstitialAdOpened() {
        Log.d(TAG, "onInterstitialAdOpened")
    }

    override fun onInterstitialAdClosed() {
        Log.d(TAG, "onInterstitialAdClosed")
        showButton.isEnabled = false
    }

    override fun onInterstitialAdShowSucceeded() {
        Log.d(TAG, "onInterstitialAdShowSucceeded")
    }

    override fun onInterstitialAdShowFailed(error: IronSourceError?) {
        Log.d(TAG, "onInterstitialAdShowFailed")
    }

    override fun onInterstitialAdClicked() {
        Log.d(TAG, "onInterstitialAdClicked")
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
        if (appKey != null && appKey.isNotEmpty()) {
            IronSource.init(
                requireActivity(), appKey, IronSource.AD_UNIT.BANNER,
                IronSource.AD_UNIT.INTERSTITIAL, IronSource.AD_UNIT.REWARDED_VIDEO
            )
        }
    }
}