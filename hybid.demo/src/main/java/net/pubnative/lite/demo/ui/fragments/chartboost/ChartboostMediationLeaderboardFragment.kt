package net.pubnative.lite.demo.ui.fragments.chartboost

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.chartboost.heliumsdk.ad.HeliumBannerAd
import com.chartboost.heliumsdk.ad.HeliumBannerAdListener
import com.chartboost.heliumsdk.domain.ChartboostMediationAdException
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils

class ChartboostMediationLeaderboardFragment : Fragment(R.layout.fragment_chartboost_leaderboard),
    HeliumBannerAdListener {

    companion object {
        private val TAG = ChartboostMediationLeaderboardFragment::class.java.simpleName
    }

    private lateinit var loadButton: Button
    private lateinit var chartboostBannerContainer: FrameLayout
    private lateinit var errorView: TextView
    private var adView: HeliumBannerAd? = null
    private var heliumPlacementName : String? = null
    private val bannerSize = HeliumBannerAd.HeliumBannerSize.LEADERBOARD

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadButton = view.findViewById(R.id.button_load)
        chartboostBannerContainer = view.findViewById(R.id.ad_container)
        errorView = view.findViewById(R.id.view_error)

        /*heliumPlacementName = SettingsManager.getInstance(requireActivity())
            .getSettings().chartboostSettings?.mediationMrectVideoAdUnitId*/

        if (heliumPlacementName != null) {
            adView = HeliumBannerAd(requireContext(), heliumPlacementName!!, bannerSize, this)
            if (adView != null) {
                chartboostBannerContainer.addView(adView)
            }
        }

        loadButton.setOnClickListener {
            adView?.load()
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
        adView?.destroy()
    }

    // Helium Listener
    override fun onAdCached(
        placementName: String,
        loadId: String,
        winningBidInfo: Map<String, String>,
        error: ChartboostMediationAdException?
    ) {
        Log.d(TAG, "onAdCached")
        displayLogs()
    }

    override fun onAdClicked(placementName: String) {
        Log.d(TAG, "onAdClicked")
    }

    override fun onAdImpressionRecorded(placementName: String) {
        Log.d(TAG, "onAdImpressionRecorded")
    }

    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }
}