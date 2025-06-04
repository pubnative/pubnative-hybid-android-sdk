// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.fragments.config

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager

class ChartboostSettingsFragment : Fragment(R.layout.fragment_chartboost_settings) {
    private lateinit var appIdInput: EditText
    private lateinit var signatureInput: EditText
    private lateinit var mediationBannerInput: EditText
    private lateinit var mediationInterstitialInput: EditText
    private lateinit var mediationInterstitialVideoInput: EditText
    private lateinit var mediationRewardedHtmlInput: EditText
    private lateinit var mediationRewardedVideoInput: EditText

    private lateinit var settingManager: SettingsManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appIdInput = view.findViewById(R.id.input_chartboost_app_id)
        signatureInput = view.findViewById(R.id.input_chartboost_app_signature)
        mediationBannerInput = view.findViewById(R.id.input_chartboost_mediation_banner)
        mediationInterstitialInput = view.findViewById(R.id.input_chartboost_mediation_interstitial)
        mediationInterstitialVideoInput = view.findViewById(R.id.input_chartboost_mediation_interstitial_video)
        mediationRewardedHtmlInput = view.findViewById(R.id.input_chartboost_mediation_rewarded)
        mediationRewardedVideoInput = view.findViewById(R.id.input_chartboost_mediation_rewarded_video)

        settingManager = SettingsManager.getInstance(requireContext())

        view.findViewById<Button>(R.id.button_save_chartboost_settings).setOnClickListener {
            val chartboostAppId = appIdInput.text.toString()
            val chartboostSignature = signatureInput.text.toString()
            val mediationBannerAdUnitId = mediationBannerInput.text.toString()
            val mediationInterstitialAdUnitId = mediationInterstitialInput.text.toString()
            val mediationInterstitialVideoAdUnitId = mediationInterstitialVideoInput.text.toString()
            val mediationRewardedHtmlAdUnitId = mediationRewardedHtmlInput.text.toString()
            val mediationRewardedVideoAdUnitId = mediationRewardedVideoInput.text.toString()

            settingManager.setChartboostAppId(chartboostAppId)
            settingManager.setChartboostSignature(chartboostSignature)
            settingManager.setChartboostMediationBannerAdUnitId(mediationBannerAdUnitId)
            settingManager.setChartboostMediationInterstitialAdUnitId(mediationInterstitialAdUnitId)
            settingManager.setChartboostMediationInterstitialVideoAdUnitId(mediationInterstitialVideoAdUnitId)
            settingManager.setChartboostMediationRewardedHtmlAdUnitId(mediationRewardedHtmlAdUnitId)
            settingManager.setChartboostMediationRewardedVideoAdUnitId(mediationRewardedVideoAdUnitId)


            Toast.makeText(activity, "Chartboost settings saved successfully.", Toast.LENGTH_SHORT)
                .show()
            activity?.finish()
        }

        fillSavedValues()
    }

    private fun fillSavedValues() {
        val settings = settingManager.getSettings().chartboostSettings
        if (settings != null) {
            appIdInput.setText(settings.heliumAppId)
            signatureInput.setText(settings.heliumAppSignature)
            mediationBannerInput.setText(settings.mediationBannerAdUnitId)
            mediationInterstitialInput.setText(settings.mediationInterstitialAdUnitId)
            mediationInterstitialVideoInput.setText(settings.mediationInterstitialVideoAdUnitId)
            mediationRewardedHtmlInput.setText(settings.mediationRewardedHtmlAdUnitId)
            mediationRewardedVideoInput.setText(settings.mediationRewardedVideoAdUnitId)
        }

    }
}