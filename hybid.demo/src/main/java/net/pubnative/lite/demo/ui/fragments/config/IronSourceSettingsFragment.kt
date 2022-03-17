package net.pubnative.lite.demo.ui.fragments.config

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.ironsource.mediationsdk.IronSource
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager

class IronSourceSettingsFragment : Fragment(R.layout.fragment_ironsource_settings) {

    private lateinit var appKeyInput: EditText
    private lateinit var mediationBannerInput: EditText
    private lateinit var mediationInterstitialInput: EditText
    private lateinit var mediationRewardedInput: EditText
    private lateinit var settingManager: SettingsManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appKeyInput = view.findViewById(R.id.input_ironsource_app_key)
        mediationBannerInput = view.findViewById(R.id.input_ironsource_banner)
        mediationInterstitialInput = view.findViewById(R.id.input_ironsource_interstitial)
        mediationRewardedInput = view.findViewById(R.id.input_ironsource_rewarded)

        settingManager = SettingsManager.getInstance(requireContext())

        view.findViewById<Button>(R.id.button_save_ironsource_settings).setOnClickListener {
            val appKey = appKeyInput.text.toString()
            val bannerAdUnitId = mediationBannerInput.text.toString()
            val interstitialAdUnitId = mediationInterstitialInput.text.toString()
            val rewardedAdUnitId = mediationRewardedInput.text.toString()

            settingManager.setIronSourceAppKey(appKey)
            settingManager.setIronSourceBannerAdUnitId(bannerAdUnitId)
            settingManager.setIronSourceInterstitialAdUnitId(interstitialAdUnitId)
            settingManager.setIronSourceRewardedAdUnitId(rewardedAdUnitId)

            IronSource.init(requireActivity(), appKey)
        }

        view.findViewById<Button>(R.id.button_init_ironsource).setOnClickListener {
            initializeIronSource()
        }

        fillSavedValues()
    }

    private fun fillSavedValues() {
        val settings = settingManager.getSettings()
        appKeyInput.setText(settings.ironSourceAppKey)
        mediationBannerInput.setText(settings.ironSourceBannerAdUnitId)
        mediationInterstitialInput.setText(settings.ironSourceInterstitialAdUnitId)
        mediationRewardedInput.setText(settings.ironSourceRewardedAdUnitId)
    }

    private fun initializeIronSource() {
        val settings = settingManager.getSettings()
        val appKey = settings.ironSourceAppKey
        if (!TextUtils.isEmpty(appKey)) {
            IronSource.init(
                requireActivity(), appKey, IronSource.AD_UNIT.BANNER,
                IronSource.AD_UNIT.INTERSTITIAL, IronSource.AD_UNIT.REWARDED_VIDEO
            )
        }
    }
}