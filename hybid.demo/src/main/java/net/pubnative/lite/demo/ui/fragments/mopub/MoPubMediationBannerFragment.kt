package net.pubnative.lite.demo.ui.fragments.mopub

import android.os.Bundle
import android.support.v4.app.Fragment
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

class MoPubMediationBannerFragment : Fragment(), MoPubView.BannerAdListener {
    val TAG = MoPubMediationBannerFragment::class.java.simpleName

    private lateinit var mopubBanner: MoPubView
    private lateinit var loadButton: Button
    private lateinit var errorView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_mopub_banner, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        mopubBanner = view.findViewById(R.id.mopub_banner)

        val adUnitId = SettingsManager.getInstance(activity!!).getSettings().mopubMediationBannerAdUnitId

        mopubBanner.bannerAdListener = this
        mopubBanner.adUnitId = adUnitId
        mopubBanner.autorefreshEnabled = false

        view.findViewById<Button>(R.id.button_load).setOnClickListener {
            errorView.text = ""
            mopubBanner.loadAd()
        }
    }

    override fun onDestroy() {
        mopubBanner.destroy()
        super.onDestroy()
    }

    // ---------------- MoPub Banner Listener ---------------------
    override fun onBannerLoaded(banner: MoPubView?) {
        Log.d(TAG, "onBannerLoaded")
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
        Log.d(TAG, "onBannerClicked")
    }

    private fun displayLogs() {
        val activity = activity as TabActivity
        activity.notifyAdUpdated()
    }
}