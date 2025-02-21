package net.pubnative.lite.demo.ui.fragments.ironsource

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.unity3d.mediation.LevelPlay
import com.unity3d.mediation.LevelPlayAdError
import com.unity3d.mediation.LevelPlayAdInfo
import com.unity3d.mediation.LevelPlayAdSize
import com.unity3d.mediation.LevelPlayConfiguration
import com.unity3d.mediation.LevelPlayInitError
import com.unity3d.mediation.LevelPlayInitListener
import com.unity3d.mediation.LevelPlayInitRequest
import com.unity3d.mediation.banner.LevelPlayBannerAdView
import com.unity3d.mediation.banner.LevelPlayBannerAdViewListener
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils

class IronSourceMediationBannerFragment : Fragment(R.layout.fragment_ironsource_banner),
    LevelPlayBannerAdViewListener {
    val TAG = IronSourceMediationBannerFragment::class.java.simpleName

    private lateinit var levelPlayBanner: LevelPlayBannerAdView
    private lateinit var levelPlayBannerContainer: FrameLayout
    private lateinit var loadButton: Button
    private lateinit var errorView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        levelPlayBannerContainer = view.findViewById(R.id.ironsource_banner_container)

        val adUnitId =
            SettingsManager.getInstance(requireActivity())
                .getSettings().ironSourceSettings?.bannerAdUnitId

        initializeIronSource()

        levelPlayBanner = LevelPlayBannerAdView(requireActivity(), adUnitId!!)
        levelPlayBanner.setAdSize(LevelPlayAdSize.BANNER)
        levelPlayBanner.setBannerListener(this)
        levelPlayBannerContainer.addView(levelPlayBanner)

        loadButton.setOnClickListener {
            errorView.text = ""
            levelPlayBanner.loadAd()
        }

        errorView.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                requireActivity(),
                errorView.text.toString()
            )
        }
    }

    override fun onDestroy() {
        levelPlayBanner.destroy()
        super.onDestroy()
    }

    override fun onAdLoaded(adInfo: LevelPlayAdInfo) {
        displayLogs()
        Log.d(TAG, "onAdLoaded")
    }

    override fun onAdLoadFailed(error: LevelPlayAdError) {
        displayLogs()
        errorView.text = error.getErrorMessage()
        Log.d(TAG, "onAdLoadFailed")
    }

    override fun onAdDisplayed(adInfo: LevelPlayAdInfo) {
        Log.d(TAG, "onAdDisplayed")
        super.onAdDisplayed(adInfo)
    }

    override fun onAdDisplayFailed(adInfo: LevelPlayAdInfo, error: LevelPlayAdError) {
        Log.e(TAG, "onAdDisplayFailed: " + error.getErrorMessage())
        super.onAdDisplayFailed(adInfo, error)
    }

    override fun onAdClicked(adInfo: LevelPlayAdInfo) {
        Log.d(TAG, "onAdClicked")
        super.onAdClicked(adInfo)
    }

    override fun onAdExpanded(adInfo: LevelPlayAdInfo) {
        Log.d(TAG, "onAdExpanded")
        super.onAdExpanded(adInfo)
    }

    override fun onAdCollapsed(adInfo: LevelPlayAdInfo) {
        Log.d(TAG, "onAdCollapsed")
        super.onAdCollapsed(adInfo)
    }

    override fun onAdLeftApplication(adInfo: LevelPlayAdInfo) {
        Log.d(TAG, "onAdLeftApplication")
        super.onAdLeftApplication(adInfo)
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