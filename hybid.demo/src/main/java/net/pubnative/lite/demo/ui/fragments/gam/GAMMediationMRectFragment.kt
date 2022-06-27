package net.pubnative.lite.demo.ui.fragments.gam

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.*
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils

class GAMMediationMRectFragment : Fragment(R.layout.fragment_dfp_mrect) {
    val TAG = GAMMediationMRectFragment::class.java.simpleName

    private lateinit var gamMRect: AdView
    private lateinit var gamMRectContainer: FrameLayout
    private lateinit var loadButton: Button
    private lateinit var errorView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        gamMRectContainer = view.findViewById(R.id.dfp_mrect_container)

        val adUnitId =
            SettingsManager.getInstance(requireActivity()).getSettings().dfpMediationMediumAdUnitId

        gamMRect = AdView(requireActivity())
        gamMRect.setAdSize(AdSize.MEDIUM_RECTANGLE)
        gamMRect.adUnitId = adUnitId
        gamMRect.adListener = adListener

        gamMRectContainer.addView(gamMRect)

        loadButton.setOnClickListener {
            loadButton.isEnabled = false
            errorView.text = ""
            gamMRect.loadAd(
                AdRequest.Builder()
                    .build()
            )
        }

        errorView.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                requireActivity(),
                errorView.text.toString()
            )
        }
    }

    // ------------------ Admob Ad Listener ---------------------
    private val adListener = object : AdListener() {
        override fun onAdLoaded() {
            super.onAdLoaded()
            displayLogs()
            Log.d(TAG, "onAdLoaded")
            enableLoadBtn()
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            super.onAdFailedToLoad(loadAdError)
            displayLogs()
            errorView.text = loadAdError.message
            Log.d(TAG, "onAdFailedToLoad")
            enableLoadBtn()
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
    private fun enableLoadBtn() {
        loadButton.isEnabled = true
    }
}