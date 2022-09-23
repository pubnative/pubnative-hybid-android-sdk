package net.pubnative.lite.demo.ui.fragments.fairbid

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.fyber.fairbid.ads.ImpressionData
import com.fyber.fairbid.ads.Interstitial
import com.fyber.fairbid.ads.interstitial.InterstitialListener
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils

val FairbidInterstitialFragment_TAG: String = FairbidInterstitialFragment::class.java.simpleName

class FairbidInterstitialFragment : Fragment(R.layout.fragment_fairbid_interstitial),
    InterstitialListener {

    private var adUnitId: String? = null
    private lateinit var loadButton: Button
    private lateinit var showButton: Button
    private lateinit var errorView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        showButton = view.findViewById(R.id.button_show)
        showButton.isEnabled = false

        if (SettingsManager.getInstance(requireActivity())
                .getSettings().fairbidSettings?.interstitialAdUnitId != null
        ) {
            adUnitId = SettingsManager.getInstance(requireActivity())
                .getSettings().fairbidSettings?.interstitialAdUnitId
        }

        if (adUnitId != null) {
            Interstitial.setInterstitialListener(this)
            Interstitial.disableAutoRequesting(adUnitId!!)

            loadButton.setOnClickListener {
                showButton.isEnabled = false
                Interstitial.request(adUnitId!!)
            }

            showButton.setOnClickListener {
                if (Interstitial.isAvailable(adUnitId!!)) {
                    Interstitial.show(adUnitId!!, requireActivity())
                }
                showButton.isEnabled = false
            }
        }

        errorView.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                requireActivity(),
                errorView.text.toString()
            )
        }
    }


    // Fairbid Interstitial Listeners
    override fun onShow(placementId: String, impressionData: ImpressionData) {
        Log.d(FairbidInterstitialFragment_TAG, "onShow")
        showButton.isEnabled = false
    }

    override fun onClick(placementId: String) {
        Log.d(FairbidInterstitialFragment_TAG, "onClick")
    }

    override fun onHide(placementId: String) {
        Log.d(FairbidInterstitialFragment_TAG, "onHide")
    }

    override fun onShowFailure(placementId: String, impressionData: ImpressionData) {
        Log.d(FairbidInterstitialFragment_TAG, "onShowFailure")
        showButton.isEnabled = false
    }

    override fun onAvailable(placementId: String) {
        Log.d(FairbidInterstitialFragment_TAG, "onAvailable")
        showButton.isEnabled = true
        displayLogs()
    }

    override fun onUnavailable(placementId: String) {
        Log.d(FairbidInterstitialFragment_TAG, "onUnavailable")
        showButton.isEnabled = false
    }

    override fun onRequestStart(placementId: String) {
        Log.d(FairbidInterstitialFragment_TAG, "onRequestStart")
    }

    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }
}