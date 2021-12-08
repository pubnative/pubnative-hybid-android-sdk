package net.pubnative.lite.demo.ui.fragments.admob

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity

class AdmobMediationNativeFragment : Fragment(R.layout.fragment_admob_native) {
    val TAG = AdmobMediationNativeFragment::class.java.simpleName

    private lateinit var admobNativeContainer: FrameLayout
    private lateinit var loadButton: Button
    private lateinit var errorView: TextView

    private var admobNative: NativeAd? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        admobNativeContainer = view.findViewById(R.id.ad_container)

        val adUnitId =
            SettingsManager.getInstance(requireActivity()).getSettings().admobNativeAdUnitId

        view.findViewById<Button>(R.id.button_load).setOnClickListener {
            errorView.text = ""
            admobNative?.destroy()

            val adLoader = AdLoader.Builder(requireContext(), adUnitId)
                .forNativeAd {
                    if (isDetached) {
                        it.destroy()
                    } else {
                        admobNative = it
                        renderAd()
                    }
                }
                .withAdListener(adListener)
                .withNativeAdOptions(nativeAdOptions).build()

            adLoader.loadAd(AdRequest.Builder().build())
        }
    }

    override fun onDestroy() {
        admobNative?.destroy()
        super.onDestroy()
    }

    private fun renderAd() {
        admobNative?.let {
            admobNativeContainer.removeAllViews()
            val adView = LayoutInflater.from(requireContext())
                .inflate(
                    R.layout.layout_admob_native_ad,
                    admobNativeContainer,
                    false
                ) as NativeAdView
            val adIcon = adView.findViewById<ImageView>(R.id.ad_icon)
            val adTitle = adView.findViewById<TextView>(R.id.ad_title)
            val adBanner = adView.findViewById<ImageView>(R.id.ad_banner)
            val adDescription = adView.findViewById<TextView>(R.id.ad_description)
            val adCallToAction = adView.findViewById<Button>(R.id.ad_call_to_action)
            val adChoices = adView.findViewById<ImageView>(R.id.ad_choices)

            adIcon.setImageDrawable(it.icon?.drawable)
            adView.iconView = adIcon

            it.images?.let { images ->
                if (images.isNotEmpty()) {
                    adBanner.setImageDrawable(it.images.first().drawable)
                    adView.imageView = adBanner
                }
            }

            adTitle.text = it.headline
            adView.headlineView = adTitle
            adDescription.text = it.body
            adView.bodyView = adDescription
            adCallToAction.text = it.callToAction
            adView.callToActionView = adCallToAction
            //adView.advertiserView = adChoices
            adView.setNativeAd(it)

            admobNativeContainer.addView(adView)
        }
    }

    private val nativeAdOptions = NativeAdOptions.Builder().build()

    // ------------------ Admob Ad Listener ---------------------
    private val adListener = object : AdListener() {
        override fun onAdLoaded() {
            super.onAdLoaded()
            displayLogs()
            Log.d(TAG, "onAdLoaded")
        }

        override fun onAdFailedToLoad(error: LoadAdError) {
            super.onAdFailedToLoad(error)
            displayLogs()
            errorView.text = error.message
            Log.d(TAG, "onAdFailedToLoad")
        }

        override fun onAdImpression() {
            super.onAdImpression()
            Log.d(TAG, "onAdImpression")
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
}