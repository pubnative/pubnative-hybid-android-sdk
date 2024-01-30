package net.pubnative.lite.demo.ui.fragments.vast

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.AdCustomizationActivity
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd
import net.pubnative.lite.demo.viewmodel.VastTagRequestViewModel
import net.pubnative.lite.sdk.rewarded.HyBidRewardedAd
import net.pubnative.lite.sdk.utils.Logger
import net.pubnative.lite.sdk.utils.URLValidator

class VastTagRequestFragment : Fragment(R.layout.fragment_vast_tag), HyBidInterstitialAd.Listener,
    HyBidRewardedAd.Listener {

    private val tagClassName: String = VastTagRequestFragment::class.java.simpleName

    private lateinit var vastTagInput: EditText
    private lateinit var zoneIdInput: EditText
    private lateinit var adSizeGroup: RadioGroup
    private lateinit var loadButton: MaterialButton
    private lateinit var showButton: MaterialButton
    private lateinit var adCustomisation: MaterialButton

    private lateinit var vastTagRequestViewModel: VastTagRequestViewModel

    private var interstitial: HyBidInterstitialAd? = null

    private var rewarded: HyBidRewardedAd? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vastTagRequestViewModel = ViewModelProvider(this)[VastTagRequestViewModel::class.java]

        adSizeGroup = view.findViewById(R.id.group_vast_ad_size)
        vastTagInput = view.findViewById(R.id.input_vast_tag)
        zoneIdInput = view.findViewById(R.id.input_zone_id)
        loadButton = view.findViewById(R.id.button_vast_load)
        showButton = view.findViewById(R.id.button_vast_show)
        adCustomisation = view.findViewById(R.id.button_customize)

        view.findViewById<ImageButton>(R.id.button_vast_paste_clipboard).setOnClickListener {
            pasteFromClipboard()
        }

        loadButton.setOnClickListener {
            cleanLogs()
            loadVastTag()
        }

        vastTagRequestViewModel.loadAdInterstitial.observe(viewLifecycleOwner) {
            interstitial?.prepareAd(it)
        }

        vastTagRequestViewModel.loadAdRewarded.observe(viewLifecycleOwner) {
            rewarded?.prepareAd(it)
        }

        vastTagRequestViewModel.onAdLoadFailed.observe(viewLifecycleOwner) {
            Toast.makeText(activity, it, Toast.LENGTH_SHORT)
                .show()
        }

        showButton.setOnClickListener {
            when (vastTagRequestViewModel.getSelectedVast()) {
                VastTagRequestViewModel.VAST.INTERSTITIAL -> {
                    interstitial?.show()
                }

                VastTagRequestViewModel.VAST.REWARDED -> {
                    rewarded?.show()
                }
            }
        }

        adCustomisation.setOnClickListener {
            val intent = Intent(context, AdCustomizationActivity::class.java)
            startActivity(intent)
        }

        adSizeGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_vast_size_interstitial -> {
                    vastTagRequestViewModel.interstitialRadioButtonSelected()
                    showButton.isEnabled = false
                    loadButton.isEnabled = true
                }

                R.id.radio_vast_size_rewarded -> {
                    vastTagRequestViewModel.rewardedRadioButtonSelected()
                    showButton.isEnabled = false
                    loadButton.isEnabled = true
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

        showButton.isEnabled = false

        val vastUrl = vastTagInput.text.toString()
        val zoneId = zoneIdInput.text.toString()

        when {
            TextUtils.isEmpty(vastUrl) -> {
                Toast.makeText(activity, "Please input some vast ad server URL", Toast.LENGTH_SHORT)
                    .show()
            }

            TextUtils.isEmpty(zoneId) -> {
                Toast.makeText(activity, "Please input zone id", Toast.LENGTH_SHORT).show()
            }

            else -> {
                when (vastTagRequestViewModel.getSelectedVast()) {
                    VastTagRequestViewModel.VAST.INTERSTITIAL -> {
                        loadInterstitialVastTagDirectly(vastUrl, zoneId)
                    }

                    VastTagRequestViewModel.VAST.REWARDED -> {
                        loadRewardedVastTagDirectly(vastUrl, zoneId)
                    }
                }
            }
        }
    }

    private fun loadInterstitialVastTagDirectly(url: String, zoneId: String) {
        if (URLValidator.isValidURL(url)) {
            interstitial = HyBidInterstitialAd(activity, this)
            vastTagRequestViewModel.prepareVideoTag(zoneId, url)
        } else {
            Toast.makeText(activity, "Please enter Valid URL", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadRewardedVastTagDirectly(url: String, zoneId: String) {
        if (URLValidator.isValidURL(url)) {
            rewarded = HyBidRewardedAd(activity, this)
            vastTagRequestViewModel.prepareVideoTag(zoneId, url)
        } else {
            Toast.makeText(activity, "Please enter Valid URL", Toast.LENGTH_SHORT).show()
        }
    }

    // Interstitial listeners
    override fun onInterstitialLoaded() {
        displayLogs()
        Logger.d(tagClassName, "onInterstitialAdLoaded")
        showButton.isEnabled = true
    }

    override fun onInterstitialLoadFailed(error: Throwable?) {
        displayLogs()
        Logger.e(tagClassName, "onInterstitialAdLoadFailed", error)
        showButton.isEnabled = false
    }

    override fun onInterstitialImpression() {
        Logger.d(tagClassName, "onInterstitialAdImpression")
    }

    override fun onInterstitialDismissed() {
        Logger.d(tagClassName, "onInterstitialAdDismissed")
        showButton.isEnabled = false
    }

    override fun onInterstitialClick() {
        Logger.d(tagClassName, "onInterstitialAdClick")
    }

    override fun onRewardedLoaded() {
        displayLogs()
        Logger.d(tagClassName, "onRewardedLoaded")
        showButton.isEnabled = true
    }

    override fun onRewardedLoadFailed(error: Throwable?) {
        displayLogs()
        Logger.d(tagClassName, "onRewardedLoadFailed")
        showButton.isEnabled = false
    }

    override fun onRewardedOpened() {
        Logger.d(tagClassName, "onRewardedOpened")
    }

    override fun onRewardedClosed() {
        Logger.d(tagClassName, "onRewardedClosed")
        showButton.isEnabled = false
    }

    override fun onRewardedClick() {
        Logger.d(tagClassName, "onRewardedClick")
    }

    override fun onReward() {
        Logger.d(tagClassName, "onReward")
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
            activity.clearRequestUrlString()
            activity.notifyAdCleaned()
        }
    }

    override fun onResume() {
        vastTagRequestViewModel.fetchAdCustomisationConfigs()
        super.onResume()
    }

    override fun onDestroy() {
        interstitial?.destroy()
        rewarded?.destroy()
        super.onDestroy()
    }
}