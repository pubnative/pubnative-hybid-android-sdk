package net.pubnative.lite.demo.ui.fragments.chartboost

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.chartboost.chartboostmediationsdk.ad.ChartboostMediationBannerAdLoadListener
import com.chartboost.chartboostmediationsdk.ad.ChartboostMediationBannerAdLoadRequest
import com.chartboost.chartboostmediationsdk.ad.ChartboostMediationBannerAdLoadResult
import com.chartboost.chartboostmediationsdk.ad.ChartboostMediationBannerAdView
import com.chartboost.chartboostmediationsdk.ad.ChartboostMediationBannerAdViewListener
import com.chartboost.chartboostmediationsdk.domain.Keywords
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils

class ChartboostMediationMrectVideoFragment : Fragment(R.layout.fragment_chartboost_mrect_video),
    ChartboostMediationBannerAdLoadListener, ChartboostMediationBannerAdViewListener {

    companion object {
        private val TAG = ChartboostMediationMrectVideoFragment::class.java.simpleName
    }

    private lateinit var loadButton: Button
    private lateinit var chartboostBannerContainer: FrameLayout
    private lateinit var errorView: TextView
    private var adView: ChartboostMediationBannerAdView? = null
    private var chartboostPlacementName: String? = null
    private val bannerSize = ChartboostMediationBannerAdView.ChartboostMediationBannerSize.MEDIUM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadButton = view.findViewById(R.id.button_load)
        chartboostBannerContainer = view.findViewById(R.id.ad_container)
        errorView = view.findViewById(R.id.view_error)

        /*heliumPlacementName = SettingsManager.getInstance(requireActivity())
            .getSettings().chartboostSettings?.mediationMrectVideoAdUnitId*/

        if (chartboostPlacementName != null) {
            adView = ChartboostMediationBannerAdView(
                requireContext(),
                chartboostPlacementName!!,
                bannerSize,
                this
            )
            if (adView != null) {
                chartboostBannerContainer.addView(adView)
            }
        }

        loadButton.setOnClickListener {
            adView?.loadFromJava(
                ChartboostMediationBannerAdLoadRequest(
                    chartboostPlacementName!!,
                    Keywords(),
                    bannerSize
                ), this
            )
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

    // Chartboost Listener
    override fun onAdLoaded(result: ChartboostMediationBannerAdLoadResult) {
        Log.d(TAG, "onAdLoaded")
        displayLogs()
    }

    override fun onAdViewAdded(placement: String, child: View?) {
        Log.d(TAG, "onAdViewAdded")
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