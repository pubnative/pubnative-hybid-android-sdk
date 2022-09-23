package net.pubnative.lite.demo.ui.fragments.vast

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd
import net.pubnative.lite.sdk.rewarded.HyBidRewardedAd
import net.pubnative.lite.sdk.utils.Logger

class VastTagRequestFragment : Fragment(R.layout.fragment_vast_tag), HyBidInterstitialAd.Listener,
    HyBidRewardedAd.Listener {

    private val TAG = VastTagRequestFragment::class.java.simpleName

    private lateinit var vastTagInput: EditText
    private lateinit var zoneIdInput: EditText
    private lateinit var adSizeGroup: RadioGroup

    private lateinit var mInterstitial: HyBidInterstitialAd

    private lateinit var mRewarded: HyBidRewardedAd

    private var mVast: VAST = VAST.INTERSTITIAL

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adSizeGroup = view.findViewById(R.id.group_vast_ad_size)
        vastTagInput = view.findViewById(R.id.input_vast_tag)
        zoneIdInput = view.findViewById(R.id.input_zone_id)

        view.findViewById<ImageButton>(R.id.button_vast_paste_clipboard).setOnClickListener {
            pasteFromClipboard()
        }

        view.findViewById<Button>(R.id.button_vast_load).setOnClickListener {
            cleanLogs()
            loadVastTag()
        }

        adSizeGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_vast_size_interstitial -> {
                    mVast = VAST.INTERSTITIAL
                }

                R.id.radio_vast_size_rewarded -> {
                    mVast = VAST.REWARDED
                }
            }
        }
    }

    private fun pasteFromClipboard() {
        val clipboardText = ClipboardUtils.copyFromClipboard(requireActivity())
        if (!TextUtils.isEmpty(clipboardText)) {
            vastTagInput.setText(clipboardText)
        }
    }

    private fun loadVastTag() {
        val vastUrl = vastTagInput.text.toString()
        val zoneId = zoneIdInput.text.toString()

        when {
            TextUtils.isEmpty(vastUrl) -> {
                Toast.makeText(activity, "Please input some vast adserver URL", Toast.LENGTH_SHORT)
                    .show()
            }
            TextUtils.isEmpty(zoneId) -> {
                Toast.makeText(activity, "Please input zone id", Toast.LENGTH_SHORT)
                    .show()
            }
            else -> {
                when (mVast) {
                    VAST.INTERSTITIAL -> {
                        loadInterstitialVastTagDirectly(vastUrl, zoneId)
                    }

                    VAST.REWARDED -> {
                        loadRewardedVastTagDirectly(vastUrl, zoneId)
                    }
                }
            }
        }
    }

    private fun loadInterstitialVastTagDirectly(url: String, zoneId: String) {
        mInterstitial = HyBidInterstitialAd(activity, this)
        mInterstitial.prepareVideoTag(zoneId, url)
    }

    private fun loadRewardedVastTagDirectly(url: String, zoneId: String) {
        mRewarded = HyBidRewardedAd(activity, this)
        mRewarded.prepareVideoTag(zoneId, url)
    }

    // Interstitial listeners
    override fun onInterstitialLoaded() {
        displayLogs()
        Logger.d(TAG, "onInterstitialAdLoaded")
        mInterstitial.show()
    }

    override fun onInterstitialLoadFailed(error: Throwable?) {
        displayLogs()
        Logger.e(TAG, "onInterstitialAdLoadFailed", error)
    }

    override fun onInterstitialImpression() {
        Logger.d(TAG, "onInterstitialAdImpression")
    }

    override fun onInterstitialDismissed() {
        Logger.d(TAG, "onInterstitialAdDismissed")
    }

    override fun onInterstitialClick() {
        Logger.d(TAG, "onInterstitialAdClick")
    }

    enum class VAST {
        INTERSTITIAL, REWARDED
    }

    override fun onRewardedLoaded() {
        displayLogs()
        Logger.d(TAG, "onRewardedLoaded")
        mRewarded.show()
    }

    override fun onRewardedLoadFailed(error: Throwable?) {
        displayLogs()
        Logger.d(TAG, "onRewardedLoadFailed")
    }

    override fun onRewardedOpened() {
        Logger.d(TAG, "onRewardedOpened")
    }

    override fun onRewardedClosed() {
        Logger.d(TAG, "onRewardedClosed")
    }

    override fun onRewardedClick() {
        Logger.d(TAG, "onRewardedClick")
    }

    override fun onReward() {
        Logger.d(TAG, "onReward")
    }

    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }

    private fun cleanLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.clearEventList()
            activity.notifyAdCleaned()
        }
    }
}