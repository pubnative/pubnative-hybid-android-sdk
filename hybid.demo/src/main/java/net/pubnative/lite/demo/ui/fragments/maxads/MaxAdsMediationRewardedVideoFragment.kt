// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.fragments.maxads

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxRewardedAd
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity

class MaxAdsMediationRewardedVideoFragment : Fragment(R.layout.fragment_maxads_rewarded),
    MaxRewardedAdListener {
    val TAG = MaxAdsMediationRewardedVideoFragment::class.java.simpleName
    private var maxRewarded: MaxRewardedAd? = null
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
            .getSettings().maxAdsSettings?.rewardedVideoAdUnitId

        maxRewarded = MaxRewardedAd.getInstance(adUnitId, requireActivity())
        maxRewarded?.setListener(this)

        view.findViewById<Button>(R.id.button_load).setOnClickListener {
            errorView.text = ""

            maxRewarded?.loadAd()
        }

        view.findViewById<Button>(R.id.button_show).setOnClickListener {
            maxRewarded?.let {
                if (it.isReady) {
                    it.showAd()
                }
            }
        }
    }

    override fun onDestroy() {
        maxRewarded?.destroy()
        super.onDestroy()
    }

    // ------------- MaxAds Rewarded Listener ------------------

    override fun onAdLoaded(ad: MaxAd) {
        Log.d(TAG, "onAdLoaded")
        displayLogs()
        showButton.isEnabled = true
    }

    override fun onAdLoadFailed(adUnitId: String, error: MaxError) {
        Log.d(TAG, "onAdLoadFailed")
        displayLogs()
        errorView.text = error.message
    }

    override fun onAdDisplayed(ad: MaxAd) {
        Log.d(TAG, "onAdDisplayed")
    }

    override fun onAdDisplayFailed(ad: MaxAd, error: MaxError) {
        Log.d(TAG, "onAdDisplayFailed")
        Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
    }

    override fun onAdClicked(ad: MaxAd) {
        Log.d(TAG, "onAdClicked")
    }

    override fun onAdHidden(ad: MaxAd) {
        Log.d(TAG, "onAdHidden")
    }

    override fun onUserRewarded(ad: MaxAd, reward: MaxReward) {
        Log.d(TAG, "onUserRewarded")
    }

    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }
}