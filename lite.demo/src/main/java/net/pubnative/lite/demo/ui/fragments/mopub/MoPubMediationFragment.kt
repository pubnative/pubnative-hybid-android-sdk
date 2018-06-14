package net.pubnative.lite.demo.ui.fragments.mopub

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.mopub.mobileads.MoPubErrorCode
import com.mopub.mobileads.MoPubInterstitial
import com.mopub.mobileads.MoPubView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager

class MoPubMediationFragment : Fragment() {
    val TAG = MoPubMediationFragment::class.java.simpleName

    private var bannerAdUnitId: String? = null
    private var mediumAdUnitId: String? = null
    private var fullscreenAdUnitId: String? = null

    private lateinit var mopubBanner: MoPubView
    private lateinit var mopubMedium: MoPubView
    private lateinit var mopubInterstitial: MoPubInterstitial

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_mopub_mediation, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mopubBanner = view.findViewById(R.id.mopub_banner)
        mopubMedium = view.findViewById(R.id.mopub_medium)

        val settings = SettingsManager.getInstance(activity!!).getSettings()
        bannerAdUnitId = settings.mopubMediationBannerAdUnitId
        mediumAdUnitId = settings.mopubMediationMediumAdUnitId
        fullscreenAdUnitId = settings.mopubMediationInterstitialAdUnitId

        mopubBanner.bannerAdListener = bannerListener
        mopubBanner.adUnitId = bannerAdUnitId
        mopubBanner.autorefreshEnabled = false

        mopubMedium.bannerAdListener = mediumListener
        mopubMedium.adUnitId = mediumAdUnitId
        mopubMedium.autorefreshEnabled = false

        mopubInterstitial = MoPubInterstitial(activity!!, fullscreenAdUnitId!!)
        mopubInterstitial.interstitialAdListener = interstitialListener

        view.findViewById<Button>(R.id.button_load_banner).setOnClickListener {
            mopubBanner.loadAd()
        }

        view.findViewById<Button>(R.id.button_load_medium).setOnClickListener {
            mopubMedium.loadAd()
        }

        view.findViewById<Button>(R.id.button_load_fullscreen).setOnClickListener {
            mopubInterstitial.load()
        }
    }

    private val bannerListener = object : MoPubView.BannerAdListener {
        override fun onBannerLoaded(banner: MoPubView?) {
            Log.d(TAG, "onBannerLoaded")
        }

        override fun onBannerFailed(banner: MoPubView?, errorCode: MoPubErrorCode?) {
            Log.d(TAG, "onBannerFailed")
        }

        override fun onBannerClicked(banner: MoPubView?) {
            Log.d(TAG, "onBannerClicked")
        }

        override fun onBannerExpanded(banner: MoPubView?) {
            Log.d(TAG, "onBannerExpanded")
        }

        override fun onBannerCollapsed(banner: MoPubView?) {
            Log.d(TAG, "onBannerCollapsed")
        }
    }

    private val mediumListener = object : MoPubView.BannerAdListener {
        override fun onBannerLoaded(banner: MoPubView?) {
            Log.d(TAG, "onMediumLoaded")
        }

        override fun onBannerFailed(banner: MoPubView?, errorCode: MoPubErrorCode?) {
            Log.d(TAG, "onMediumFailed")
        }

        override fun onBannerClicked(banner: MoPubView?) {
            Log.d(TAG, "onMediumClicked")
        }

        override fun onBannerExpanded(banner: MoPubView?) {
            Log.d(TAG, "onMediumExpanded")
        }

        override fun onBannerCollapsed(banner: MoPubView?) {
            Log.d(TAG, "onMediumCollapsed")
        }
    }

    private val interstitialListener = object : MoPubInterstitial.InterstitialAdListener {
        override fun onInterstitialLoaded(interstitial: MoPubInterstitial?) {
            Log.d(TAG, "onInterstitialLoaded")
            interstitial?.show()
        }

        override fun onInterstitialFailed(interstitial: MoPubInterstitial?, errorCode: MoPubErrorCode?) {
            Log.d(TAG, "onInterstitialFailed")
        }

        override fun onInterstitialShown(interstitial: MoPubInterstitial?) {
            Log.d(TAG, "onInterstitialShown")
        }

        override fun onInterstitialDismissed(interstitial: MoPubInterstitial?) {
            Log.d(TAG, "onInterstitialDismissed")
        }

        override fun onInterstitialClicked(interstitial: MoPubInterstitial?) {
            Log.d(TAG, "onInterstitialClicked")
        }
    }
}