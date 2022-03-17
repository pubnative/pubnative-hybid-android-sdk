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
    private lateinit var interstitialInput: EditText
    private lateinit var nativeInput: EditText
    private lateinit var rewardedInput: EditText
    private lateinit var settingManager: SettingsManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sdkKeyInput = view.findViewById(R.id.input_maxads_sdk_key)
        bannerInput = view.findViewById(R.id.input_maxads_banner)
        mediumInput = view.findViewById(R.id.input_maxads_medium)
        interstitialInput = view.findViewById(R.id.input_maxads_interstitial)
        rewardedInput = view.findViewById(R.id.input_maxads_rewarded)
        nativeInput = view.findViewById(R.id.input_maxads_native)

        settingManager = SettingsManager.getInstance(requireContext())

        view.findViewById<Button>(R.id.button_save_maxads_settings).setOnClickListener {
            val sdkKey = sdkKeyInput.text.toString()
            val bannerAdUnitId = bannerInput.text.toString()
            val mediumAdUnitId = mediumInput.text.toString()
            val interstitialAdUnitId = interstitialInput.text.toString()
            val rewardedAdUnitId = rewardedInput.text.toString()
            val nativeAdUnitId = nativeInput.text.toString()

            settingManager.setMaxAdsSdkKey(sdkKey)
            settingManager.setMaxAdsBannerAdUnitId(bannerAdUnitId)
            settingManager.setMaxAdsMRectAdUnitId(mediumAdUnitId)
            settingManager.setMaxAdsInterstitialAdUnitId(interstitialAdUnitId)
            settingManager.setMaxAdsNativeAdUnitId(nativeAdUnitId)
            settingManager.setMaxAdsRewardedAdUnitId(rewardedAdUnitId)

            Toast.makeText(activity, "MaxAds settings saved successfully.", Toast.LENGTH_SHORT)
                .show()
            activity?.finish()
        }

        fillSavedValues()
    }

    private fun fillSavedValues() {
        val settings = settingManager.getSettings()
        sdkKeyInput.setText(settings.maxAdsSdkKey)
        bannerInput.setText(settings.maxAdsBannerAdUnitId)
        mediumInput.setText(settings.maxAdsMRectAdUnitId)
        interstitialInput.setText(settings.maxAdsInterstitialAdUnitId)
        rewardedInput.setText(settings.maxAdsRewardedAdUnitId)
        nativeInput.setText(settings.maxAdsNativeAdUnitId)
    }
}