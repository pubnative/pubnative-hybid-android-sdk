package net.pubnative.lite.demo.ui.fragments.config

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager

class MaxAdsSettingsFragment : Fragment(R.layout.fragment_maxads_settings) {

    private lateinit var sdkKeyInput: EditText
    private lateinit var bannerInput: EditText
    private lateinit var mediumInput: EditText
    private lateinit var mediumVideoInput: EditText
    private lateinit var interstitialInput: EditText
    private lateinit var interstitialVideoInput: EditText
    private lateinit var nativeInput: EditText
    private lateinit var rewardedInput: EditText
    private lateinit var rewardedVideoInput: EditText
    private lateinit var settingManager: SettingsManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sdkKeyInput = view.findViewById(R.id.input_maxads_sdk_key)
        bannerInput = view.findViewById(R.id.input_maxads_banner)
        mediumInput = view.findViewById(R.id.input_maxads_medium)
        mediumVideoInput = view.findViewById(R.id.input_maxads_medium_video)
        interstitialInput = view.findViewById(R.id.input_maxads_interstitial)
        interstitialVideoInput = view.findViewById(R.id.input_maxads_interstitial_video)
        rewardedInput = view.findViewById(R.id.input_maxads_rewarded)
        rewardedVideoInput = view.findViewById(R.id.input_maxads_rewarded_video)
        nativeInput = view.findViewById(R.id.input_maxads_native)

        settingManager = SettingsManager.getInstance(requireContext())

        view.findViewById<Button>(R.id.button_save_maxads_settings).setOnClickListener {
            val sdkKey = sdkKeyInput.text.toString()
            val bannerAdUnitId = bannerInput.text.toString()
            val mediumAdUnitId = mediumInput.text.toString()
            val mediumVideoAdUnitId = mediumInput.text.toString()
            val interstitialAdUnitId = interstitialInput.text.toString()
            val interstitialVideoAdUnitId = interstitialInput.text.toString()
            val rewardedAdUnitId = rewardedInput.text.toString()
            val rewardedVideoAdUnitId = rewardedInput.text.toString()
            val nativeAdUnitId = nativeInput.text.toString()

            settingManager.setMaxAdsSdkKey(sdkKey)
            settingManager.setMaxAdsBannerAdUnitId(bannerAdUnitId)
            settingManager.setMaxAdsMRectAdUnitId(mediumAdUnitId)
            settingManager.setMaxAdsMRectVideoAdUnitId(mediumVideoAdUnitId)
            settingManager.setMaxAdsInterstitialAdUnitId(interstitialAdUnitId)
            settingManager.setMaxAdsInterstitialVideoAdUnitId(interstitialVideoAdUnitId)
            settingManager.setMaxAdsRewardedAdUnitId(rewardedAdUnitId)
            settingManager.setMaxAdsRewardedVideoAdUnitId(rewardedVideoAdUnitId)
            settingManager.setMaxAdsNativeAdUnitId(nativeAdUnitId)

            Toast.makeText(activity, "MaxAds settings saved successfully.", Toast.LENGTH_SHORT)
                .show()
            activity?.finish()
        }

        fillSavedValues()
    }

    private fun fillSavedValues() {
        val settings = settingManager.getSettings().maxAdsSettings
        if (settings != null) {
            sdkKeyInput.setText(settings.sdkKey)
            bannerInput.setText(settings.bannerAdUnitId)
            mediumInput.setText(settings.mRectAdUnitId)
            mediumVideoInput.setText(settings.mRectVideoAdUnitId)
            interstitialInput.setText(settings.interstitialAdUnitId)
            interstitialVideoInput.setText(settings.interstitialVideoAdUnitId)
            rewardedInput.setText(settings.rewardedAdUnitId)
            rewardedVideoInput.setText(settings.rewardedVideoAdUnitId)
            nativeInput.setText(settings.nativeAdUnitId)
        }
    }
}