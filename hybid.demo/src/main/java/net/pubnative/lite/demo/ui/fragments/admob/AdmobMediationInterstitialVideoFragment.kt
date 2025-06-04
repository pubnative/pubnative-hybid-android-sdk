// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.fragments.admob

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils

class AdmobMediationInterstitialVideoFragment :
    Fragment(R.layout.fragment_admob_interstitial_video) {

    val TAG = AdmobMediationInterstitialVideoFragment::class.java.simpleName

    private var admobInterstitial: InterstitialAd? = null
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
            .getSettings().admobSettings?.interstitialVideoAdUnitId

        loadButton.setOnClickListener {
            loadButton.isEnabled = false
            errorView.text = ""
            val adRequest = AdRequest.Builder().build()
            if (adUnitId != null) {
                InterstitialAd.load(requireActivity(), adUnitId, adRequest, adLoadCallback)
            }
        }

        showButton.setOnClickListener {
            admobInterstitial?.show(requireActivity())
        }

        errorView.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                requireActivity(), errorView.text.toString()
            )
        }
    }

    // ---------------- Admob Interstitial Ad Load Callback ---------------------
    private val adLoadCallback = object : InterstitialAdLoadCallback() {
        override fun onAdLoaded(ad: InterstitialAd) {
            super.onAdLoaded(ad)
            admobInterstitial = ad
            admobInterstitial?.fullScreenContentCallback = fullScreenContentCallback
            Log.d(TAG, "onAdLoaded")
            displayLogs()
            showButton.isEnabled = true
        }

        override fun onAdFailedToLoad(error: LoadAdError) {
            super.onAdFailedToLoad(error)
            Log.d(TAG, "onAdFailedToLoad")
            displayLogs()
            errorView.text = error.message
            showButton.isEnabled = false
            enableLoadBtn()
        }
    }

    // ---------------- Admob Full Screen Content Callback ---------------------
    private val fullScreenContentCallback = object : FullScreenContentCallback() {
        override fun onAdFailedToShowFullScreenContent(error: AdError) {
            super.onAdFailedToShowFullScreenContent(error)
            Log.d(TAG, "onAdFailedToShowFullScreenContent")
        }

        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
            Log.d(TAG, "onAdShowedFullScreenContent")
        }

        override fun onAdDismissedFullScreenContent() {
            super.onAdDismissedFullScreenContent()
            Log.d(TAG, "onAdDismissedFullScreenContent")
            showButton.isEnabled = false
            enableLoadBtn()
        }

        override fun onAdImpression() {
            super.onAdImpression()
            Log.d(TAG, "onAdImpression")
        }

        override fun onAdClicked() {
            super.onAdClicked()
            Log.d(TAG, "onAdClicked")
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