package net.pubnative.lite.demo.ui.fragments.ironsource

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ironsource.mediationsdk.ISBannerSize
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.IronSourceBannerLayout
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo
import com.ironsource.mediationsdk.logger.IronSourceError
import com.ironsource.mediationsdk.sdk.LevelPlayBannerListener
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils

class IronSourceMediationBannerFragment : Fragment(R.layout.fragment_ironsource_banner),
    LevelPlayBannerListener {
    val TAG = IronSourceMediationBannerFragment::class.java.simpleName

    private lateinit var ironSourceBanner: IronSourceBannerLayout
    private lateinit var ironSourceBannerContainer: FrameLayout
    private lateinit var loadButton: Button
    private lateinit var errorView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        ironSourceBannerContainer = view.findViewById(R.id.ironsource_banner_container)

        val adUnitId =
            SettingsManager.getInstance(requireActivity())
                .getSettings().ironSourceSettings?.bannerAdUnitId

        ironSourceBanner = IronSource.createBanner(requireActivity(), ISBannerSize.BANNER)
        ironSourceBanner.levelPlayBannerListener = this
        ironSourceBannerContainer.addView(ironSourceBanner)

        initializeIronSource()

        loadButton.setOnClickListener {
            errorView.text = ""
            IronSource.loadBanner(ironSourceBanner, adUnitId)
        }

        errorView.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                requireActivity(),
                errorView.text.toString()
            )
        }
    }

    override fun onDestroy() {
        ironSourceBanner.removeBannerListener()
        IronSource.destroyBanner(ironSourceBanner)
        super.onDestroy()
    }

    override fun onAdLoaded(info: AdInfo?) {
        displayLogs()
        Log.d(TAG, "onAdLoaded")
    }

    override fun onAdLoadFailed(error: IronSourceError?) {
        displayLogs()
        errorView.text = error?.errorMessage
        Log.d(TAG, "onAdLoadFailed")
    }

    override fun onAdClicked(info: AdInfo?) {
        Log.d(TAG, "onAdClicked")
    }

    override fun onAdLeftApplication(info: AdInfo?) {
        Log.d(TAG, "onAdLeftApplication")
    }

    override fun onAdScreenPresented(info: AdInfo?) {
        Log.d(TAG, "onAdScreenPresented")
    }

    override fun onAdScreenDismissed(info: AdInfo?) {
        Log.d(TAG, "onAdScreenDismissed")
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
            IronSource.setMetaData("is_test_suite", "enable")
            IronSource.init(
                requireActivity(), appKey, IronSource.AD_UNIT.BANNER,
                IronSource.AD_UNIT.INTERSTITIAL, IronSource.AD_UNIT.REWARDED_VIDEO
            )
        }
    }
}