package net.pubnative.lite.demo.ui.fragments.ironsource

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.unity3d.mediation.LevelPlay
import com.unity3d.mediation.LevelPlayAdError
import com.unity3d.mediation.LevelPlayAdInfo
import com.unity3d.mediation.LevelPlayConfiguration
import com.unity3d.mediation.LevelPlayInitError
import com.unity3d.mediation.LevelPlayInitListener
import com.unity3d.mediation.LevelPlayInitRequest
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAd
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAdListener
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils

class IronSourceMediationInterstitialFragment : Fragment(R.layout.fragment_ironsource_interstitial),
    LevelPlayInterstitialAdListener {
    val TAG = IronSourceMediationInterstitialFragment::class.java.simpleName

    private lateinit var levelPlayInterstitial: LevelPlayInterstitialAd
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

        levelPlayInterstitial = LevelPlayInterstitialAd(adUnitId!!)
        levelPlayInterstitial.setListener(this)


        loadButton.setOnClickListener {
            errorView.text = ""
            levelPlayInterstitial.loadAd()
        }

        showButton.setOnClickListener {
            if (levelPlayInterstitial.isAdReady()) {
                levelPlayInterstitial.showAd(requireActivity())
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
        super.onDestroy()
    }

    override fun onAdLoaded(adInfo: LevelPlayAdInfo) {
        Log.d(TAG, "onAdLoaded")
        displayLogs()
        showButton.isEnabled = true
    }

    override fun onAdLoadFailed(error: LevelPlayAdError) {
        Log.d(TAG, "onAdLoadFailed")
        displayLogs()
        errorView.text = error.getErrorMessage()
        showButton.isEnabled = false
    }

    override fun onAdDisplayed(adInfo: LevelPlayAdInfo) {
        Log.d(TAG, "onAdDisplayed")
    }

    override fun onAdDisplayFailed(error: LevelPlayAdError, adInfo: LevelPlayAdInfo) {
        Log.e(TAG, "onAdDisplayFailed: " + error.getErrorMessage())
        super.onAdDisplayFailed(error, adInfo)
    }

    override fun onAdClicked(adInfo: LevelPlayAdInfo) {
        Log.d(TAG, "onAdClicked")
        super.onAdClicked(adInfo)
    }

    override fun onAdClosed(adInfo: LevelPlayAdInfo) {
        Log.d(TAG, "onAdClosed")
        showButton.isEnabled = false
        super.onAdClosed(adInfo)
    }

    override fun onAdInfoChanged(adInfo: LevelPlayAdInfo) {
        Log.d(TAG, "onAdInfoChanged")
        super.onAdInfoChanged(adInfo)
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
            val initRequest = LevelPlayInitRequest.Builder(appKey).build()
            LevelPlay.init(requireActivity(), initRequest, object : LevelPlayInitListener {
                override fun onInitSuccess(configuration: LevelPlayConfiguration) {

                }

                override fun onInitFailed(error: LevelPlayInitError) {

                }
            })
        }
    }
}