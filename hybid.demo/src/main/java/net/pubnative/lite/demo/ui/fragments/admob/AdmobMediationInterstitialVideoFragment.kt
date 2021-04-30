package net.pubnative.lite.demo.ui.fragments.admob

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
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.AdmobErrorParser
import net.pubnative.lite.demo.util.ClipboardUtils

class AdmobMediationInterstitialVideoFragment : Fragment() {

    val TAG = AdmobMediationInterstitialVideoFragment::class.java.simpleName

    private lateinit var admobInterstitial: InterstitialAd
    private lateinit var loadButton: Button
    private lateinit var showButton: Button
    private lateinit var errorView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_admob_interstitial_video, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        showButton = view.findViewById(R.id.button_show)
        showButton.isEnabled = false

        val adUnitId = SettingsManager.getInstance(requireActivity()).getSettings().admobInterstitialVideoAdUnitId

        admobInterstitial = InterstitialAd(activity)
        admobInterstitial.adUnitId = adUnitId
        admobInterstitial.adListener = adListener

        loadButton.setOnClickListener {
            errorView.text = ""
            admobInterstitial.loadAd(AdRequest.Builder()
                    .addTestDevice("9CD3F3CADFC5127409B07C5F802273E7")
                    .build())
        }

        showButton.setOnClickListener {
            admobInterstitial.show()
        }

        errorView.setOnClickListener { ClipboardUtils.copyToClipboard(requireActivity(), errorView.text.toString()) }
    }

    // ---------------- Admob Ad Listener ---------------------
    private val adListener = object : AdListener() {
        override fun onAdLoaded() {
            super.onAdLoaded()
            Log.d(TAG, "onAdLoaded")
            displayLogs()
            showButton.isEnabled = true
        }

        override fun onAdFailedToLoad(errorCode: Int) {
            super.onAdFailedToLoad(errorCode)
            Log.d(TAG, "onAdFailedToLoad")
            displayLogs()
            errorView.text = AdmobErrorParser.getErrorMessage(errorCode)
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