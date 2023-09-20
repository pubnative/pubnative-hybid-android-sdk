package net.pubnative.lite.demo.ui.fragments.vast

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd
import net.pubnative.lite.sdk.rewarded.HyBidRewardedAd
import net.pubnative.lite.sdk.utils.Logger
import net.pubnative.lite.sdk.utils.URLValidator
import net.pubnative.lite.sdk.utils.URLValidator.URLValidatorListener

class VastTagRequestFragment : Fragment(R.layout.fragment_vast_tag), HyBidInterstitialAd.Listener,
    HyBidRewardedAd.Listener {

    private val TAG = VastTagRequestFragment::class.java.simpleName

    private lateinit var vastTagInput: EditText
    private lateinit var zoneIdInput: EditText
    private lateinit var adSizeGroup: RadioGroup
    private lateinit var loadButton: MaterialButton
    private lateinit var showButton: MaterialButton

    private var interstitial: HyBidInterstitialAd? = null

    private var rewarded: HyBidRewardedAd? = null

    private var vast: VAST = VAST.INTERSTITIAL

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adSizeGroup = view.findViewById(R.id.group_vast_ad_size)
        vastTagInput = view.findViewById(R.id.input_vast_tag)
        zoneIdInput = view.findViewById(R.id.input_zone_id)
        loadButton = view.findViewById(R.id.button_vast_load)
        showButton = view.findViewById(R.id.button_vast_show)

        view.findViewById<ImageButton>(R.id.button_vast_paste_clipboard).setOnClickListener {
            pasteFromClipboard()
        }

        loadButton.setOnClickListener {
            cleanLogs()
            loadVastTag()
        }

        showButton.setOnClickListener {
            when (vast) {
                VAST.INTERSTITIAL -> {
                    interstitial?.show()
                }

                VAST.REWARDED -> {
                    rewarded?.show()
                }
            }
        }

        adSizeGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_vast_size_interstitial -> {
                    vast = VAST.INTERSTITIAL
                    showButton.isEnabled = false
                    loadButton.isEnabled = true
                }

                R.id.radio_vast_size_rewarded -> {
                    vast = VAST.REWARDED
                    showButton.isEnabled = false
                    loadButton.isEnabled = true
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        interstitial?.destroy()
        rewarded?.destroy()
    }

    private fun pasteFromClipboard() {
        val clipboardText = ClipboardUtils.copyFromClipboard(requireActivity())
        if (!TextUtils.isEmpty(clipboardText)) {
            vastTagInput.setText(clipboardText)
        }
    }

    private fun loadVastTag() {
        showButton.isEnabled = false
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
                when (vast) {
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
        URLValidator.isValidURL(
            url
        ) { isValid ->
            if (isValid) {
                interstitial = HyBidInterstitialAd(activity, this@VastTagRequestFragment)
                interstitial?.prepareVideoTag(zoneId, url)
            } else {
                Toast.makeText(activity, "Please enter Valid URL", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun loadRewardedVastTagDirectly(url: String, zoneId: String) {
        URLValidator.isValidURL(
            url
        ) { isValid ->
            if (isValid) {
                rewarded = HyBidRewardedAd(activity, this)
                rewarded?.prepareVideoTag(zoneId, url)
            } else {
                Toast.makeText(activity, "Please enter Valid URL", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    // Interstitial listeners
    override fun onInterstitialLoaded() {
        displayLogs()
        Logger.d(TAG, "onInterstitialAdLoaded")
        showButton.isEnabled = true
    }

    override fun onInterstitialLoadFailed(error: Throwable?) {
        displayLogs()
        Logger.e(TAG, "onInterstitialAdLoadFailed", error)
        showButton.isEnabled = false
    }

    override fun onInterstitialImpression() {
        Logger.d(TAG, "onInterstitialAdImpression")
    }

    override fun onInterstitialDismissed() {
        Logger.d(TAG, "onInterstitialAdDismissed")
        showButton.isEnabled = false
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
        showButton.isEnabled = true
    }

    override fun onRewardedLoadFailed(error: Throwable?) {
        displayLogs()
        Logger.d(TAG, "onRewardedLoadFailed")
        showButton.isEnabled = false
    }

    override fun onRewardedOpened() {
        Logger.d(TAG, "onRewardedOpened")
    }

    override fun onRewardedClosed() {
        Logger.d(TAG, "onRewardedClosed")
        showButton.isEnabled = false
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