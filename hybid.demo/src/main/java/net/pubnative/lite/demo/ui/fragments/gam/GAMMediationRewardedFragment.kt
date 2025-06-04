// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.fragments.gam

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.*
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils

class GAMMediationRewardedFragment : Fragment(R.layout.fragment_dfp_rewarded) {
    val TAG = GAMMediationRewardedFragment::class.java.simpleName

    private var gamRewarded: RewardedAd? = null
    private lateinit var loadButton: Button
    private lateinit var showButton: Button
    private lateinit var errorView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        showButton = view.findViewById(R.id.button_show)

        val adUnitId = SettingsManager.getInstance(requireActivity())
            .getSettings().dfpSettings?.mediationRewardedAdUnitId
        showButton.isEnabled = false

        loadButton.setOnClickListener {
            loadButton.isEnabled = false
            errorView.text = ""
            val adRequest = AdManagerAdRequest.Builder().build()
            if (adUnitId != null) {
                RewardedAd.load(requireActivity(), adUnitId, adRequest, adLoadCallback)
            }
        }

        showButton.setOnClickListener {
            if (gamRewarded != null) {
                gamRewarded?.show(requireActivity()) {
                    Log.d(TAG, "onUserEarnedReward")
                }
            }
            enableLoadBtn()
            showButton.isEnabled = false
        }

        errorView.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                requireActivity(),
                errorView.text.toString()
            )
        }
    }

    // ---------------- AdManagerInterstitialAdLoadCallback ---------------------
    private val adLoadCallback = object : RewardedAdLoadCallback() {
        override fun onAdLoaded(ad: RewardedAd) {
            super.onAdLoaded(ad)
            gamRewarded = ad
            Log.d(TAG, "onRewardedAdLoaded")
            displayLogs()
            showButton.isEnabled = true
        }

        override fun onAdFailedToLoad(error: LoadAdError) {
            super.onAdFailedToLoad(error)
            gamRewarded = null
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