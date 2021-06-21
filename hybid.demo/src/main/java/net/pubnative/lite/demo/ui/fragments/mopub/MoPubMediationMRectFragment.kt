package net.pubnative.lite.demo.ui.fragments.mopub

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mopub.mobileads.MoPubErrorCode
import com.mopub.mobileads.MoPubView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity

class MoPubMediationMRectFragment : Fragment(R.layout.fragment_mopub_mrect), MoPubView.BannerAdListener {
    val TAG = MoPubMediationMRectFragment::class.java.simpleName

    private lateinit var mopubMedium: MoPubView
    private lateinit var autoRefreshSwitch: Switch
    private lateinit var loadButton: Button
    private lateinit var errorView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        mopubMedium = view.findViewById(R.id.mopub_mrect)
        autoRefreshSwitch = view.findViewById(R.id.check_auto_refresh)

        val adUnitId = SettingsManager.getInstance(requireActivity()).getSettings().mopubMediationMediumAdUnitId

        mopubMedium.bannerAdListener = this
        mopubMedium.setAdUnitId(adUnitId)
        mopubMedium.adSize = MoPubView.MoPubAdSize.HEIGHT_250
        mopubMedium.autorefreshEnabled = false

        autoRefreshSwitch.visibility = View.VISIBLE
        autoRefreshSwitch.isChecked = false
        autoRefreshSwitch.setOnCheckedChangeListener { _, isChecked ->
            mopubMedium.autorefreshEnabled = isChecked
        }

        view.findViewById<Button>(R.id.button_load).setOnClickListener {
            errorView.text = ""
            mopubMedium.loadAd()
        }
    }

    override fun onDestroy() {
        mopubMedium.destroy()
        super.onDestroy()
    }

    // ---------------- MoPub Banner Listener ---------------------
    override fun onBannerLoaded(banner: MoPubView) {
        Log.d(TAG, "onAdLoaded")
        displayLogs()
    }

    override fun onBannerFailed(banner: MoPubView?, errorCode: MoPubErrorCode?) {
        Log.d(TAG, "onBannerFailed")
        displayLogs()
        errorView.text = errorCode.toString()
    }

    override fun onBannerExpanded(banner: MoPubView?) {
        Log.d(TAG, "onBannerExpanded")
    }

    override fun onBannerCollapsed(banner: MoPubView?) {
        Log.d(TAG, "onBannerCollapsed")
    }

    override fun onBannerClicked(banner: MoPubView?) {
        Log.d(TAG, "onAdClicked")
    }

    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }
}