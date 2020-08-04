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
import com.mopub.mobileads.MoPubView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity

class MoPubMediationLeaderboardFragment : Fragment(), MoPubView.BannerAdListener {
    val TAG = MoPubMediationLeaderboardFragment::class.java.simpleName

    private lateinit var mopubLeaderboard: MoPubView
    private lateinit var loadButton: Button
    private lateinit var errorView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_mopub_leaderboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        mopubLeaderboard = view.findViewById(R.id.mopub_leaderboard)

        val adUnitId = SettingsManager.getInstance(activity!!).getSettings().mopubMediationLeaderboardAdUnitId

        mopubLeaderboard.bannerAdListener = this
        mopubLeaderboard.setAdUnitId(adUnitId)
        mopubLeaderboard.adSize = MoPubView.MoPubAdSize.HEIGHT_90
        mopubLeaderboard.autorefreshEnabled = false

        view.findViewById<Button>(R.id.button_load).setOnClickListener {
            errorView.text = ""
            mopubLeaderboard.loadAd()
        }
    }

    override fun onDestroy() {
        mopubLeaderboard.destroy()
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