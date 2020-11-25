package net.pubnative.lite.demo.ui.fragments.dfp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.LoadAdError
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.AdmobErrorParser
import net.pubnative.lite.demo.util.ClipboardUtils

class DFPMediationInterstitialFragment : Fragment(){
    val TAG = DFPMediationInterstitialFragment::class.java.simpleName

    private lateinit var dfpInterstitial: InterstitialAd
    private lateinit var loadButton: Button
    private lateinit var errorView: TextView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_dfp_interstitial, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)

        val adUnitId = SettingsManager.getInstance(activity!!).getSettings().dfpMediationInterstitialAdUnitId

        dfpInterstitial = InterstitialAd(activity)
        dfpInterstitial.adUnitId = adUnitId
        dfpInterstitial.adListener = adListener

        loadButton.setOnClickListener {
            errorView.text = ""
            dfpInterstitial.loadAd(AdRequest.Builder()
                    .addTestDevice("9CD3F3CADFC5127409B07C5F802273E7")
                    .build())
        }

        errorView.setOnClickListener { ClipboardUtils.copyToClipboard(activity!!, errorView.text.toString()) }
    }

    // ---------------- Admob Ad Listener ---------------------
    private val adListener = object : AdListener() {
        override fun onAdLoaded() {
            super.onAdLoaded()
            Log.d(TAG, "onAdLoaded")
            displayLogs()
            dfpInterstitial.show()
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            super.onAdFailedToLoad(loadAdError)
            displayLogs()
            errorView.text = AdmobErrorParser.getErrorMessage(loadAdError.code)
            Log.d(TAG, "onAdFailedToLoad")
        }

        override fun onAdClicked() {
            super.onAdClicked()
            Log.d(TAG, "onAdClicked")
        }

        override fun onAdOpened() {
            super.onAdOpened()
            Log.d(TAG, "onAdOpened")
        }

        override fun onAdLeftApplication() {
            super.onAdLeftApplication()
            Log.d(TAG, "onAdLeftApplication")
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