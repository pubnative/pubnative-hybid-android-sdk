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
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.api.LeaderboardRequestManager
import net.pubnative.lite.sdk.api.RequestManager
import net.pubnative.lite.sdk.models.Ad
import net.pubnative.lite.sdk.utils.PrebidUtils

class MoPubLeaderboardFragment : Fragment(), RequestManager.RequestListener, MoPubView.BannerAdListener {
    val TAG = MoPubLeaderboardFragment::class.java.simpleName

    private lateinit var requestManager: RequestManager
    private var zoneId: String? = null
    private var adUnitId: String? = null

    private lateinit var mopubLeaderboard: MoPubView
    private lateinit var loadButton: Button
    private lateinit var errorView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_mopub_leaderboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        mopubLeaderboard = view.findViewById(R.id.mopub_leaderboard)
        mopubLeaderboard.bannerAdListener = this
        mopubLeaderboard.autorefreshEnabled = false

        requestManager = LeaderboardRequestManager()

        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)
        adUnitId = SettingsManager.getInstance(activity!!).getSettings().mopubLeaderboardAdUnitId

        loadButton.setOnClickListener {
            errorView.text = ""
            val activity = activity as TabActivity
            activity.notifyAdCleaned()
            loadPNAd()
        }

        errorView.setOnClickListener { ClipboardUtils.copyToClipboard(activity!!, errorView.text.toString()) }
    }

    override fun onDestroy() {
        super.onDestroy()
        mopubLeaderboard.destroy()
    }

    fun loadPNAd() {
        requestManager.setZoneId(zoneId)
        requestManager.setRequestListener(this)
        requestManager.requestAd()
    }

    // --------------- HyBid Request Listener --------------------
    override fun onRequestSuccess(ad: Ad?) {
        mopubLeaderboard.adUnitId = adUnitId
        mopubLeaderboard.keywords = PrebidUtils.getPrebidKeywords(ad)
        mopubLeaderboard.loadAd()

        Log.d(TAG, "onRequestSuccess")
        displayLogs()
    }

    override fun onRequestFail(throwable: Throwable?) {
        Log.d(TAG, "onRequestFail: ", throwable)
        errorView.text = throwable?.message
        displayLogs()
    }

    // ---------------- MoPub Banner Listener ---------------------
    override fun onBannerLoaded(banner: MoPubView?) {
        Log.d(TAG, "onAdLoaded")
    }

    override fun onBannerFailed(banner: MoPubView?, errorCode: MoPubErrorCode?) {
        Log.d(TAG, "onBannerFailed")
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
        val activity = activity as TabActivity
        activity.notifyAdUpdated()
    }
}