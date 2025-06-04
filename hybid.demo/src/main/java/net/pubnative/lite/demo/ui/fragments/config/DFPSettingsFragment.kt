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

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class DFPSettingsFragment : Fragment(R.layout.fragment_dfp_settings) {

    private lateinit var mediationBannerInput: EditText
    private lateinit var mediationMediumInput: EditText
    private lateinit var mediationLeaderboardInput: EditText
    private lateinit var mediationInterstitialInput: EditText
    private lateinit var mediationRewardedInput: EditText
    private lateinit var settingManager: SettingsManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediationBannerInput = view.findViewById(R.id.input_dfp_mediation_banner)
        mediationMediumInput = view.findViewById(R.id.input_dfp_mediation_medium)
        mediationLeaderboardInput = view.findViewById(R.id.input_dfp_mediation_leaderboard)
        mediationInterstitialInput = view.findViewById(R.id.input_dfp_mediation_interstitial)
        mediationRewardedInput = view.findViewById(R.id.input_dfp_mediation_rewarded)

        settingManager = SettingsManager.getInstance(requireContext())

        view.findViewById<Button>(R.id.button_save_dfp_settings).setOnClickListener {
            val mediationBannerAdUnitId = mediationBannerInput.text.toString()
            val mediationMediumAdUnitId = mediationMediumInput.text.toString()
            val mediationLeaderboardAdUnitId = mediationLeaderboardInput.text.toString()
            val mediationInterstitialAdUnitId = mediationInterstitialInput.text.toString()
            val mediationRewardedAdUnitId = mediationRewardedInput.text.toString()

            settingManager.setDFPMediationBannerAdUnitId(mediationBannerAdUnitId)
            settingManager.setDFPMediationMediumAdUnitId(mediationMediumAdUnitId)
            settingManager.setDFPMediationLeaderboardAdUnitId(mediationLeaderboardAdUnitId)
            settingManager.setDFPMediationInterstitialAdUnitId(mediationInterstitialAdUnitId)
            settingManager.setDFPMediationRewardedAdUnitId(mediationRewardedAdUnitId)

            Toast.makeText(activity, "DFP settings saved successfully.", Toast.LENGTH_SHORT).show()
            activity?.finish()
        }

        fillSavedValues()
    }

    private fun fillSavedValues() {
        val settings = settingManager.getSettings().dfpSettings
        if (settings != null) {
            mediationBannerInput.setText(settings.mediationBannerAdUnitId)
            mediationMediumInput.setText(settings.mediationMediumAdUnitId)
            mediationLeaderboardInput.setText(settings.mediationLeaderboardAdUnitId)
            mediationInterstitialInput.setText(settings.mediationInterstitialAdUnitId)
            mediationRewardedInput.setText(settings.mediationRewardedAdUnitId)
        }
    }
}