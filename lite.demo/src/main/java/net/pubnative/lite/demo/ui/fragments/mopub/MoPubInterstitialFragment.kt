package net.pubnative.lite.demo.ui.fragments.mopub

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.mopub.mobileads.MoPubErrorCode
import com.mopub.mobileads.MoPubInterstitial
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.sdk.api.InterstitialRequestManager
import net.pubnative.lite.sdk.api.RequestManager
import net.pubnative.lite.sdk.models.Ad
import net.pubnative.lite.sdk.utils.PrebidUtils

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class MoPubInterstitialFragment : Fragment(), RequestManager.RequestListener, MoPubInterstitial.InterstitialAdListener {
    val TAG = MoPubInterstitialFragment::class.java.simpleName

    private lateinit var requestManager: RequestManager
    private lateinit var mopubInterstitial: MoPubInterstitial
    private var zoneId: String? = null
    private var adUnitId: String? = null

    private lateinit var loadButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_mopub_interstitial, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadButton = view.findViewById(R.id.button_load)

        adUnitId = SettingsManager.getInstance(activity!!).getSettings().mopubInterstitialAdUnitId

        requestManager = InterstitialRequestManager()
        mopubInterstitial = MoPubInterstitial(activity!!, adUnitId!!)
        mopubInterstitial.interstitialAdListener = this

        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)

        loadButton.setOnClickListener {
            loadPNAd()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mopubInterstitial.destroy()
    }

    fun loadPNAd() {
        requestManager.setZoneId(zoneId)
        requestManager.setRequestListener(this)
        requestManager.requestAd()
    }

    // --------------- PNLite Request Listener --------------------
    override fun onRequestSuccess(ad: Ad?) {
        mopubInterstitial.keywords = PrebidUtils.getPrebidKeywords(ad, zoneId)
        mopubInterstitial.load()
        Log.d(TAG, "onRequestSuccess")
    }

    override fun onRequestFail(throwable: Throwable?) {
        Log.d(TAG, "onRequestFail: ", throwable)
        Toast.makeText(activity, throwable?.message, Toast.LENGTH_SHORT).show()
    }

    // ------------- MoPub Interstitial Listener ------------------
    override fun onInterstitialLoaded(interstitial: MoPubInterstitial?) {
        mopubInterstitial.show()
        Log.d(TAG, "onInterstitialLoaded")
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