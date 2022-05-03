package net.pubnative.lite.demo.ui.fragments.fairbid

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.fyber.fairbid.ads.Banner
import com.fyber.fairbid.ads.ImpressionData
import com.fyber.fairbid.ads.banner.BannerError
import com.fyber.fairbid.ads.banner.BannerListener
import com.fyber.fairbid.ads.banner.BannerOptions
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils


class FairbidMediationBannerFragment : Fragment(R.layout.fragment_fairbid_banner),
BannerListener {
    val TAG = FairbidMediationBannerFragment::class.java.simpleName

    private lateinit var fairbidBannerContainer: FrameLayout
    private lateinit var showButton: Button
    private lateinit var errorView: TextView
    private lateinit var adUnitId: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fairbidBannerContainer = view.findViewById(R.id.ad_container)
        errorView = view.findViewById(R.id.view_error)
        showButton = view.findViewById(R.id.button_show)
        showButton.isEnabled = true

        val bannerOptions = BannerOptions().placeInContainer(fairbidBannerContainer)

        adUnitId =
            SettingsManager.getInstance(requireActivity()).getSettings().fairbidMediationBannerAdUnitId

        Banner.setBannerListener(this)

        showButton.setOnClickListener {
            showButton.isEnabled = true
            if (!TextUtils.isEmpty(adUnitId)) {
                Banner.show(adUnitId, bannerOptions, requireActivity())
            }
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
        if (!TextUtils.isEmpty(adUnitId)) {
            Banner.destroy(adUnitId)
        }
    }

    // Fairbid Banner Listeners
    override fun onError(p0: String, error: BannerError?) {
        Log.d(TAG, "onError")
        displayLogs()
        errorView.text = error?.errorMessage
    }

    override fun onLoad(placementId: String) {
        Log.d(TAG, "onLoad")
        displayLogs()
        //showButton.isEnabled = true
    }

    override fun onShow(placementId: String, impressionData: ImpressionData) {
        Log.d(TAG, "onShow")
    }

    override fun onClick(placementId: String) {
        Log.d(TAG, "onClick")
    }

    override fun onRequestStart(placementId: String) {
        Log.d(TAG, "onAvailable")
    }


    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }
}