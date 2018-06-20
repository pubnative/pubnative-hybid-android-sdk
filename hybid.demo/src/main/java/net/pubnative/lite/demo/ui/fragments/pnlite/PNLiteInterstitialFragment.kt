package net.pubnative.lite.demo.ui.fragments.pnlite

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.sdk.interstitial.PNInterstitialAd

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class PNLiteInterstitialFragment : Fragment(), PNInterstitialAd.Listener {
    val TAG = PNLiteInterstitialFragment::class.java.simpleName

    private var zoneId: String? = null

    private lateinit var loadButton: Button
    private var interstitial: PNInterstitialAd? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_pnlite_interstitial, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadButton = view.findViewById(R.id.button_load)


        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)

        loadButton.setOnClickListener {
            loadPNAd()
        }
    }

    override fun onDestroy() {
        interstitial?.destroy()
        super.onDestroy()
    }

    fun loadPNAd() {
        interstitial = PNInterstitialAd(activity, zoneId, this)
        interstitial?.load()
    }

    override fun onInterstitialLoaded() {
        Log.d(TAG, "onInterstitialLoaded")
        interstitial?.show()
    }

    override fun onInterstitialLoadFailed(error: Throwable?) {
        Log.e(TAG, "onInterstitialLoadFailed", error)
        Toast.makeText(activity, error?.message, Toast.LENGTH_SHORT).show()
    }

    override fun onInterstitialImpression() {
        Log.d(TAG, "onInterstitialImpression")
    }

    override fun onInterstitialDismissed() {
        Log.d(TAG, "onInterstitialDismissed")
    }

    override fun onInterstitialClick() {
        Log.d(TAG, "onInterstitialClick")
    }
}