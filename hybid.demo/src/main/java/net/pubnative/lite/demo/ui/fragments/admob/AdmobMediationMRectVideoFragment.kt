package net.pubnative.lite.demo.ui.fragments.admob

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.*
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils

class AdmobMediationMRectVideoFragment : Fragment(R.layout.fragment_admob_mrect_video) {
    val TAG = AdmobMediationMRectVideoFragment::class.java.simpleName

    private lateinit var admobMRect: AdView
    private lateinit var admobMRectContainer: FrameLayout
    private lateinit var loadButton: Button
    private lateinit var errorView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        admobMRectContainer = view.findViewById(R.id.admob_mrect_container)

        val adUnitId =
            SettingsManager.getInstance(requireActivity()).getSettings().admobMediumVideoAdUnitId

        admobMRect = AdView(requireActivity())
        admobMRect.adSize = AdSize.MEDIUM_RECTANGLE
        admobMRect.adUnitId = adUnitId
        admobMRect.adListener = adListener

        admobMRectContainer.addView(admobMRect)

        loadButton.setOnClickListener {
            loadButton.isEnabled = false
            errorView.text = ""
            admobMRect.loadAd(
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

        override fun onAdFailedToLoad(error: LoadAdError) {
            super.onAdFailedToLoad(error)
            displayLogs()
            errorView.text = error.message
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

        override fun onAdImpression() {
            super.onAdImpression()
            Log.d(TAG, "onAdImpression")
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