package net.pubnative.lite.demo.ui.fragments.mopub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.mopub.mobileads.MoPubErrorCode
import com.mopub.mobileads.MoPubView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity

class MoPubMediationMRectFragment : Fragment(), MoPubView.BannerAdListener {
    val TAG = MoPubMediationMRectFragment::class.java.simpleName

    private lateinit var mopubMedium: MoPubView
    private lateinit var loadButton: Button
    private lateinit var errorView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_mopub_mrect, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        mopubMedium = view.findViewById(R.id.mopub_mrect)

        val adUnitId = SettingsManager.getInstance(activity!!).getSettings().mopubMediationMediumAdUnitId

        mopubMedium.bannerAdListener = this
        mopubMedium.adUnitId = adUnitId
        mopubMedium.autorefreshEnabled = false

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
    override fun onBannerLoaded(banner: MoPubView?) {
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