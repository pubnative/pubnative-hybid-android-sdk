// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.fragments.maxads

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdRevenueListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager

class MaxAdsMediationNativeFragment : Fragment(R.layout.fragment_maxads_native),
    MaxAdRevenueListener {

    private var adUnitId: String? = ""
    private var nativeAdContainerView: ViewGroup? = null
    private var nativeAdLoader: MaxNativeAdLoader? = null
    private var loadedNativeAd: MaxAd? = null

    private lateinit var loadButton: Button
    private lateinit var showButton: Button
    private lateinit var errorView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadButton = view.findViewById(R.id.button_load)
        showButton = view.findViewById(R.id.button_show)
        showButton = view.findViewById(R.id.button_show)
        nativeAdContainerView = view.findViewById(R.id.ad_container)
        errorView = view.findViewById(R.id.view_error)

        adUnitId =
            SettingsManager.getInstance(requireActivity())
                .getSettings().maxAdsSettings?.nativeAdUnitId

        loadButton.setOnClickListener {
            loadButton.isEnabled = false
            errorView.text = ""
            loadNativeAd()
        }
    }

    private fun loadNativeAd() {
        if (loadedNativeAd != null) {
            nativeAdLoader?.destroy(loadedNativeAd)
        }
        nativeAdLoader = MaxNativeAdLoader(adUnitId, requireActivity())
        nativeAdLoader?.setRevenueListener(this)
        nativeAdLoader?.setNativeAdListener(NativeAdListener())
        nativeAdLoader?.loadAd(createNativeAdView())
    }

    private fun createNativeAdView(): MaxNativeAdView {
        val binder: MaxNativeAdViewBinder =
            MaxNativeAdViewBinder.Builder(R.layout.layout_max_native_ad)
                .setTitleTextViewId(R.id.title_text_view).setBodyTextViewId(R.id.body_text_view)
                .setStarRatingContentViewGroupId(R.id.star_rating_view)
                .setAdvertiserTextViewId(R.id.advertiser_textView)
                .setIconImageViewId(R.id.icon_image_view)
                .setMediaContentViewGroupId(R.id.media_view_container)
                .setCallToActionButtonId(R.id.cta_button).build()

        return MaxNativeAdView(binder, requireActivity())
    }

    private inner class NativeAdListener : MaxNativeAdListener() {
        override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, nativeAd: MaxAd) {
            if (loadedNativeAd != null) {
                nativeAdLoader?.destroy(loadedNativeAd)
            }
            loadedNativeAd = nativeAd
            showButton.isEnabled = true
            showButton.setOnClickListener {
                nativeAdContainerView?.removeAllViews()
                nativeAdContainerView?.addView(nativeAdView)
                showButton.isEnabled = false
                loadButton.isEnabled = true
            }
        }

        override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) {
            errorView.text = error.message?.let {
                error.message
            }
        }

        override fun onNativeAdClicked(nativeAd: MaxAd) {

        }
    }

    override fun onAdRevenuePaid(p0: MaxAd) {
    }

    override fun onDestroy() {
        nativeAdLoader?.destroy(loadedNativeAd)
        super.onDestroy()
    }
}