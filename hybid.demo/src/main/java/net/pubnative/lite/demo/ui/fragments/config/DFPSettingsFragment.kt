// The MIT License (MIT)
//
// Copyright (c) 2018 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
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