package net.pubnative.lite.demo.ui.fragments.mopub

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mopub.mobileads.MoPubErrorCode
import com.mopub.mobileads.MoPubInterstitial
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity

class MoPubMediationInterstitialVideoFragment: Fragment(), MoPubInterstitial.InterstitialAdListener {
    val TAG = MoPubMediationInterstitialVideoFragment::class.java.simpleName

    private lateinit var mopubInterstitial: MoPubInterstitial
    private lateinit var loadButton: Button
    private lateinit var showButton: Button
    private lateinit var errorView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_mopub_interstitial_video, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        showButton = view.findViewById(R.id.button_show)
        showButton.isEnabled = false

        val adUnitId = SettingsManager.getInstance(requireActivity()).getSettings().mopubMediationInterstitialVideoAdUnitId

        mopubInterstitial = MoPubInterstitial(requireActivity(), adUnitId)
        mopubInterstitial.interstitialAdListener = this

        loadButton.setOnClickListener {
            errorView.text = ""
            mopubInterstitial.load()
        }

        showButton.setOnClickListener {
            mopubInterstitial.show()
        }
    }

    override fun onDestroy() {
        mopubInterstitial.destroy()
        super.onDestroy()
    }

    // ------------- MoPub Interstitial Listener ------------------
    override fun onInterstitialLoaded(interstitial: MoPubInterstitial?) {
        showButton.isEnabled = true
        Log.d(TAG, "onInterstitialLoaded")
        displayLogs()
    }

    override fun onInterstitialFailed(interstitial: MoPubInterstitial?, errorCode: MoPubErrorCode?) {
        Log.d(TAG, "onInterstitialFailed")
        displayLogs()
        errorView.text = errorCode.toString()
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

    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }
}