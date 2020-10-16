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
import net.pubnative.lite.sdk.rewarded.HyBidRewardedAd

class HyBidRewardedFragment : Fragment(), HyBidRewardedAd.Listener{
    val TAG = HyBidRewardedFragment::class.java.simpleName

    private var zoneId: String? = null

    private lateinit var loadButton: Button
    private lateinit var errorView: TextView
    private lateinit var creativeIdView: TextView
    private var rewardedAd: HyBidRewardedAd? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_hybid_rewarded, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        creativeIdView = view.findViewById(R.id.view_creative_id)
        loadButton = view.findViewById(R.id.button_load)


        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)

        loadButton.setOnClickListener {
            errorView.text = ""
            val activity = activity as TabActivity
            activity.notifyAdCleaned()
            loadPNRewardedAd()
        }

        errorView.setOnClickListener { ClipboardUtils.copyToClipboard(activity!!, errorView.text.toString()) }
        creativeIdView.setOnClickListener { ClipboardUtils.copyToClipboard(activity!!, creativeIdView.text.toString()) }
    }


    private fun loadPNRewardedAd(){
        rewardedAd = HyBidRewardedAd(activity, zoneId, this)
        rewardedAd?.load()
    }


    // -------------- Listeners ---------------

    override fun onReward() {
        Log.d(TAG, "onReward")
    }

    override fun onRewardedLoaded() {
        Log.d(TAG, "onRewardedLoaded")
        rewardedAd?.show()
        displayLogs()
    }

    override fun onRewardedOpened() {
        Log.d(TAG, "onRewardedOpened")
    }

    override fun onRewardedClosed() {
        Log.d(TAG, "onRewardedClosed")
    }

    override fun onRewardedClick() {
        Log.d(TAG, "onRewardedClick")
    }

    override fun onRewardedLoadFailed(error: Throwable?) {
        Log.e(TAG, "onRewardedLoadFailed", error)
        errorView.text = error?.message
        displayLogs()
    }


    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }
}