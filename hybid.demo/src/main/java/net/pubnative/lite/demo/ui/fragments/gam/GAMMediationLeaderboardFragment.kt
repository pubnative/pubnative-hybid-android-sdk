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

class GAMMediationLeaderboardFragment : Fragment(R.layout.fragment_dfp_leaderboard) {
    val TAG = GAMMediationLeaderboardFragment::class.java.simpleName

    private lateinit var gamLeaderboard: AdManagerAdView
    private lateinit var gamLeaderboardContainer: FrameLayout
    private lateinit var loadButton: Button
    private lateinit var errorView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        gamLeaderboardContainer = view.findViewById(R.id.dfp_leaderboard_container)

        //It is done as leaderboardAdUnitId but it should be mediationLeaderboardAdUnitId
//        val adUnitId =
//            SettingsManager.getInstance(requireActivity()).getSettings().dfpSettings?.leaderboardAdUnitId

        val adUnitId =
            SettingsManager.getInstance(requireActivity()).getSettings().dfpSettings?.mediationLeaderboardAdUnitId

        gamLeaderboard = AdManagerAdView(requireActivity())
        gamLeaderboard.setAdSize(AdSize.LEADERBOARD)
        if (adUnitId != null) {
            gamLeaderboard.adUnitId = adUnitId
        }
        gamLeaderboard.adListener = adListener

        gamLeaderboardContainer.addView(gamLeaderboard)

        loadButton.setOnClickListener {
            loadButton.isEnabled = false
            errorView.text = ""
            gamLeaderboard.loadAd(
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