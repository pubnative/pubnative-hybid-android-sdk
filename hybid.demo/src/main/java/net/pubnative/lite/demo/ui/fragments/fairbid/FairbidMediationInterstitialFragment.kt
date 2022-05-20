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

class FairbidMediationInterstitialFragment : Fragment(R.layout.fragment_fairbid_interstitial),
    InterstitialListener {
    val TAG = FairbidMediationInterstitialFragment::class.java.simpleName

    private lateinit var loadButton: Button
    private lateinit var showButton: Button
    private lateinit var errorView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        showButton = view.findViewById(R.id.button_show)
        showButton.isEnabled = false

        val adUnitId =
            SettingsManager.getInstance(requireActivity())
                .getSettings().fairbidMediationInterstitialAdUnitId

        Interstitial.setInterstitialListener(this)

        loadButton.setOnClickListener {
            showButton.isEnabled = false
            Interstitial.request(adUnitId)
        }

        showButton.setOnClickListener {
            if (Interstitial.isAvailable(adUnitId)) {
                Interstitial.show(adUnitId, requireActivity())
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
    }


    // Fairbid Interstitial Listeners
    override fun onShow(placementId: String, impressionData: ImpressionData) {
        Log.d(TAG, "onShow")
        showButton.isEnabled = false
    }

    override fun onClick(placementId: String) {
        Log.d(TAG, "onClick")
    }

    override fun onHide(placementId: String) {
        Log.d(TAG, "onHide")
    }

    override fun onShowFailure(placementId: String, impressionData: ImpressionData) {
        Log.d(TAG, "onShowFailure")
        showButton.isEnabled = false
    }

    override fun onAvailable(placementId: String) {
        Log.d(TAG, "onAvailable")
        showButton.isEnabled = true
        displayLogs()
    }

    override fun onUnavailable(placementId: String) {
        Log.d(TAG, "onUnavailable")
        showButton.isEnabled = false
    }

    override fun onRequestStart(placementId: String) {
        Log.d(TAG, "onRequestStart")
    }


    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }
}