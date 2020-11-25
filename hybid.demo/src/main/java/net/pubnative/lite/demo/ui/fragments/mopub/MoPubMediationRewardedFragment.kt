package net.pubnative.lite.demo.ui.fragments.mopub

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mopub.common.MoPub
import com.mopub.common.MoPubReward
import com.mopub.mobileads.MoPubErrorCode
import com.mopub.mobileads.MoPubRewardedVideoListener
import com.mopub.mobileads.MoPubRewardedVideos
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.MoPubManager
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.sdk.HyBid

class MoPubMediationRewardedFragment : Fragment(), MoPubRewardedVideoListener{
    val TAG = MoPubMediationRewardedFragment::class.java.simpleName
    private lateinit var mopubRewardedVideoListener: MoPubRewardedVideoListener
    private lateinit var loadButton: Button
    private lateinit var showButton: Button
    private lateinit var errorView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_mopub_rewarded, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        showButton = view.findViewById(R.id.button_show)
        showButton.isEnabled = false

        val adUnitId = SettingsManager.getInstance(activity!!).getSettings().mopubMediationRewardedAdUnitId

        mopubRewardedVideoListener = this

        MoPubRewardedVideos.setRewardedVideoListener(mopubRewardedVideoListener)

        view.findViewById<Button>(R.id.button_load).setOnClickListener {
            errorView.text = ""

            MoPubRewardedVideos.loadRewardedVideo(adUnitId)
        }

        view.findViewById<Button>(R.id.button_show).setOnClickListener{
            MoPubRewardedVideos.showRewardedVideo(adUnitId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MoPubManager.initMoPubSdk(requireActivity(), HyBid.getAppToken());
        MoPub.onCreate(requireActivity())
    }

    override fun onPause() {
        super.onPause()
        MoPub.onPause(requireActivity())
    }

    override fun onStop() {
        super.onStop()
        MoPub.onStop(requireActivity())
    }

    override fun onResume() {
        super.onResume()
        MoPub.onResume(requireActivity())
    }

    override fun onDestroy() {
        MoPub.onDestroy(requireActivity())
        super.onDestroy()
    }


    // ------------- MoPub Rewarded Listener ------------------

    override fun onRewardedVideoLoadSuccess(adUnitId: String) {
        Log.d(TAG, "onRewardedVideoLoadSuccess")
        displayLogs()
        showButton.isEnabled = true
    }

    override fun onRewardedVideoLoadFailure(adUnitId: String, errorCode: MoPubErrorCode) {
        Log.d(TAG, "onRewardedVideoLoadFailure")
        displayLogs()
        errorView.text = errorCode.toString()
    }

    override fun onRewardedVideoStarted(adUnitId: String) {
        Log.d(TAG, "onRewardedVideoStarted")
    }

    override fun onRewardedVideoPlaybackError(adUnitId: String, errorCode: MoPubErrorCode) {
        Log.d(TAG, "onRewardedVideoPlaybackError")
    }

    override fun onRewardedVideoClicked(adUnitId: String) {
        Log.d(TAG, "onRewardedVideoClicked")
    }

    override fun onRewardedVideoClosed(adUnitId: String) {
        Log.d(TAG, "onRewardedVideoClosed")
    }

    override fun onRewardedVideoCompleted(adUnitIds: MutableSet<String>, reward: MoPubReward) {
        Log.d(TAG, "onRewardedVideoCompleted")
    }

    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }
}