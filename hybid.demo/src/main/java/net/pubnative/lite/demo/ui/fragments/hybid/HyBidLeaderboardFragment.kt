package net.pubnative.lite.demo.ui.fragments.hybid

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.views.HyBidLeaderboardAdView
import net.pubnative.lite.sdk.views.PNAdView

class HyBidLeaderboardFragment : Fragment(), PNAdView.Listener {
    val TAG = HyBidLeaderboardFragment::class.java.simpleName

    private var zoneId: String? = null

    private lateinit var hybidLeaderboard: HyBidLeaderboardAdView
    private lateinit var loadButton: Button
    private lateinit var errorView: TextView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_hybid_leaderboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        hybidLeaderboard = view.findViewById(R.id.hybid_leaderboard)

        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)

        loadButton.setOnClickListener {
            errorView.text = ""
            val activity = activity as TabActivity
            activity.notifyAdCleaned()
            loadPNAd()
        }

        errorView.setOnClickListener { ClipboardUtils.copyToClipboard(activity!!, errorView.text.toString()) }
    }

    override fun onDestroy() {
        hybidLeaderboard.destroy()
        super.onDestroy()
    }

    fun loadPNAd() {
        hybidLeaderboard.load(zoneId, this)
    }

    // --------------- PNAdView Listener --------------------
    override fun onAdLoaded() {
        Log.d(TAG, "onAdLoaded")
        displayLogs()
    }

    override fun onAdLoadFailed(error: Throwable?) {
        Log.e(TAG, "onAdLoadFailed", error)
        errorView.text = error?.message
        displayLogs()
    }

    override fun onAdImpression() {
        Log.d(TAG, "onAdImpression")
    }

    override fun onAdClick() {
        Log.d(TAG, "onAdClick")
    }

    private fun displayLogs() {
        val activity = activity as TabActivity
        activity.notifyAdUpdated()
    }
}