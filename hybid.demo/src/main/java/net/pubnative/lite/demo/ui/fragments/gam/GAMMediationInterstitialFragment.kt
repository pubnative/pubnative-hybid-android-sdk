package net.pubnative.lite.demo.ui.fragments.gam

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.*
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils

class GAMMediationInterstitialFragment : Fragment(R.layout.fragment_dfp_interstitial) {
    val TAG = GAMMediationInterstitialFragment::class.java.simpleName

    private var gamInterstitial: AdManagerInterstitialAd? = null
    private lateinit var loadButton: Button
    private lateinit var showButton: Button
    private lateinit var errorView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        showButton = view.findViewById(R.id.button_show)
        showButton.isEnabled = false

        val adUnitId = SettingsManager.getInstance(requireActivity())
            .getSettings().dfpSettings?.mediationInterstitialAdUnitId

        loadButton.setOnClickListener {
            loadButton.isEnabled = false
            errorView.text = ""
            val adRequest = AdManagerAdRequest.Builder().build()
            if (adUnitId != null) {
                AdManagerInterstitialAd.load(requireActivity(), adUnitId, adRequest, adLoadCallback)
            }
        }

        showButton.setOnClickListener {
            gamInterstitial?.show(requireActivity())
        }

        errorView.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                requireActivity(),
                errorView.text.toString()
            )
        }
    }

    // ---------------- AdManagerInterstitialAdLoadCallback ---------------------
    private val adLoadCallback = object : AdManagerInterstitialAdLoadCallback() {
        override fun onAdLoaded(ad: AdManagerInterstitialAd) {
            super.onAdLoaded(ad)
            gamInterstitial = ad
            Log.d(TAG, "onAdLoaded")
            displayLogs()
            showButton.isEnabled = true
        }

        override fun onAdFailedToLoad(error: LoadAdError) {
            super.onAdFailedToLoad(error)
            gamInterstitial = null
            displayLogs()
            errorView.text = error.message
            Log.d(TAG, "onAdFailedToLoad")
            enableLoadBtn()
        }
    }

    // ---------------- FullScreenContentCallback ---------------------
    private val fullscreenContentCallback = object : FullScreenContentCallback() {
        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
            Log.d(TAG, "onAdShowedFullScreenContent")
        }

        override fun onAdFailedToShowFullScreenContent(error: AdError) {
            super.onAdFailedToShowFullScreenContent(error)
            Log.d(TAG, "onAdFailedToShowFullScreenContent")
        }

        override fun onAdImpression() {
            super.onAdImpression()
            Log.d(TAG, "onAdImpression")
        }

        override fun onAdClicked() {
            super.onAdClicked()
            Log.d(TAG, "onAdClicked")
        }

        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            Log.d(TAG, "onAdDismissedFullScreenContent")
        }
    }

    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }

    private fun enableLoadBtn() {
        loadButton.isEnabled = true
    }
}