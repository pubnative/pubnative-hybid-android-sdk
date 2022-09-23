package net.pubnative.lite.demo.ui.fragments.admob

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils

class AdmobMediationRewardedFragment : Fragment(R.layout.fragment_admob_rewarded) {
    val TAG = AdmobMediationRewardedFragment::class.java.simpleName

    private var admobRewarded: RewardedAd? = null
    private lateinit var loadButton: Button
    private lateinit var showButton: Button
    private lateinit var errorView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        showButton = view.findViewById(R.id.button_show)

        val adUnitId =
            SettingsManager.getInstance(requireActivity()).getSettings().admobSettings?.rewardedAdUnitId
        showButton.isEnabled = false

        loadButton.setOnClickListener {
            loadButton.isEnabled = false
            errorView.text = ""
            if (adUnitId != null) {
                loadRewardedAd(adUnitId, adLoadCallback)
            }
        }

        showButton.setOnClickListener {
            showRewardedAd()
        }

        errorView.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                requireActivity(),
                errorView.text.toString()
            )
        }
    }

    private fun loadRewardedAd(
        adUnitId: String,
        adLoadCallback: RewardedAdLoadCallback
    ) {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(requireActivity(), adUnitId, adRequest, adLoadCallback)
    }

    private fun showRewardedAd() {
        admobRewarded?.show(requireActivity(), object : OnUserEarnedRewardListener {
            override fun onUserEarnedReward(reward: RewardItem) {
                Log.d(TAG, "onUserEarnedReward")
            }
        })
    }

    private val adLoadCallback = object : RewardedAdLoadCallback() {
        override fun onAdLoaded(ad: RewardedAd) {
            super.onAdLoaded(ad)
            admobRewarded = ad
            admobRewarded?.fullScreenContentCallback = fullscreenContentCallback
            Log.d(TAG, "onAdLoaded")
            displayLogs()
            showButton.isEnabled = true
        }

        override fun onAdFailedToLoad(error: LoadAdError) {
            super.onAdFailedToLoad(error)
            admobRewarded?.fullScreenContentCallback = null
            admobRewarded = null
            Log.d(TAG, "onRewardedAdFailedToLoad")
            displayLogs()
            errorView.text = error.message
            showButton.isEnabled = false
            enableLoadBtn()
        }
    }

    private val fullscreenContentCallback = object : FullScreenContentCallback() {
        override fun onAdShowedFullScreenContent() {
            super.onAdShowedFullScreenContent()
            Log.d(TAG, "onAdShowedFullScreenContent")
        }

        override fun onAdFailedToShowFullScreenContent(error: AdError) {
            super.onAdFailedToShowFullScreenContent(error)
            Log.d(TAG, "onAdFailedToShowFullScreenContent")
            errorView.text = error.message
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
            showButton.isEnabled = false
            admobRewarded = null
            enableLoadBtn()
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