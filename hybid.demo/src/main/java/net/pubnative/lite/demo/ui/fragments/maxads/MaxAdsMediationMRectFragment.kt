package net.pubnative.lite.demo.ui.fragments.maxads

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.sdk.AppLovinSdkUtils
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity

class MaxAdsMediationMRectFragment : Fragment(R.layout.fragment_maxads_mrect), MaxAdViewAdListener {
    val TAG = MaxAdsMediationMRectFragment::class.java.simpleName

    private var maxMRect: MaxAdView? = null
    private lateinit var adContainer: FrameLayout
    private lateinit var loadButton: Button
    private lateinit var errorView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        adContainer = view.findViewById(R.id.ad_container)

        val adUnitId = SettingsManager.getInstance(requireActivity())
            .getSettings().maxAdsSettings?.mRectAdUnitId

        maxMRect = MaxAdView(adUnitId, requireActivity())
        maxMRect?.setListener(this)

        val width = AppLovinSdkUtils.dpToPx(requireContext(), 300)
        val height = AppLovinSdkUtils.dpToPx(requireContext(), 250)

        maxMRect?.layoutParams = FrameLayout.LayoutParams(width, height)
        maxMRect?.setBackgroundColor(Color.BLACK)
        adContainer.addView(maxMRect)

        view.findViewById<Button>(R.id.button_load).setOnClickListener {
            errorView.text = ""
            maxMRect?.loadAd()
        }
    }

    override fun onDestroy() {
        maxMRect?.destroy()
        super.onDestroy()
    }

    // ---------------- MaxAds Banner Listener ---------------------

    override fun onAdLoaded(ad: MaxAd) {
        Log.d(TAG, "onAdLoaded")
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
        Toast.makeText(requireContext(), error?.message, Toast.LENGTH_SHORT).show()
    }

    override fun onAdClicked(ad: MaxAd) {
        Log.d(TAG, "onAdClicked")
    }

    override fun onAdExpanded(ad: MaxAd) {
        Log.d(TAG, "onAdExpanded")
    }

    override fun onAdCollapsed(ad: MaxAd) {
        Log.d(TAG, "onAdCollapsed")
    }

    override fun onAdHidden(ad: MaxAd) {
        Log.d(TAG, "onAdHidden")
    }

    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }
}