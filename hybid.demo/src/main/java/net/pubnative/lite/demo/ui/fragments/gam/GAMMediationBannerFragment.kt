package net.pubnative.lite.demo.ui.fragments.gam

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.*
import com.google.android.gms.ads.admanager.AdManagerAdView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils

class GAMMediationBannerFragment : Fragment(R.layout.fragment_dfp_banner) {
    val TAG = GAMMediationBannerFragment::class.java.simpleName

    private lateinit var gamBanner: AdManagerAdView
    private lateinit var gamBannerContainer: FrameLayout
    private lateinit var loadButton: Button
    private lateinit var errorView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        gamBannerContainer = view.findViewById(R.id.dfp_banner_container)

        val adUnitId =
            SettingsManager.getInstance(requireActivity()).getSettings().dfpMediationBannerAdUnitId

        gamBanner = AdManagerAdView(requireActivity())
        gamBanner.setAdSize(AdSize.BANNER)
        gamBanner.adUnitId = adUnitId
        gamBanner.adListener = adListener

        gamBannerContainer.addView(gamBanner)

        loadButton.setOnClickListener {
            loadButton.isEnabled = false
            errorView.text = ""
            gamBanner.loadAd(
                AdRequest.Builder()
                    .build()
            )
        }

        errorView.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                requireActivity(),
                errorView.text.toString()
            )
        }
    }

    // ------------------ Admob Ad Listener ---------------------
    private val adListener = object : AdListener() {
        override fun onAdLoaded() {
            super.onAdLoaded()
            displayLogs()
            Log.d(TAG, "onAdLoaded")
            enableLoadBtn()
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            super.onAdFailedToLoad(loadAdError)
            displayLogs()
            errorView.text = loadAdError.message
            Log.d(TAG, "onAdFailedToLoad")
            enableLoadBtn()
        }

        override fun onAdClicked() {
            super.onAdClicked()
            Log.d(TAG, "onAdClicked")
        }

        override fun onAdOpened() {
            super.onAdOpened()
            Log.d(TAG, "onAdOpened")
        }

        override fun onAdImpression() {
            super.onAdImpression()
            Log.d(TAG, "onAdImpression")
        }

        override fun onAdClosed() {
            super.onAdClosed()
            Log.d(TAG, "onAdClosed")
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