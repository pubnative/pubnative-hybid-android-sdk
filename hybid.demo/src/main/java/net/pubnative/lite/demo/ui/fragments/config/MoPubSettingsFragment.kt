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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.MoPubManager
import net.pubnative.lite.demo.managers.SettingsManager

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class MoPubSettingsFragment : Fragment(R.layout.fragment_mopub_settings) {

    private lateinit var bannerInput: EditText
    private lateinit var mediumInput: EditText
    private lateinit var mediumVideoInput: EditText
    private lateinit var leaderboardInput: EditText
    private lateinit var interstitialInput: EditText
    private lateinit var interstitialVideoInput: EditText
    private lateinit var rewardedInput: EditText
    private lateinit var mediationBannerInput: EditText
    private lateinit var mediationMediumInput: EditText
    private lateinit var mediationMediumVideoInput: EditText
    private lateinit var mediationLeaderboardInput: EditText
    private lateinit var mediationInterstitialInput: EditText
    private lateinit var mediationInterstitialVideoInput: EditText
    private lateinit var mediationRewardedInput: EditText
    private lateinit var mediationNativeInput: EditText
    private lateinit var settingManager: SettingsManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bannerInput = view.findViewById(R.id.input_mopub_banner)
        mediumInput = view.findViewById(R.id.input_mopub_medium)
        mediumVideoInput = view.findViewById(R.id.input_mopub_medium_video)
        leaderboardInput = view.findViewById(R.id.input_mopub_leaderboard)
        interstitialInput = view.findViewById(R.id.input_mopub_interstitial)
        interstitialVideoInput = view.findViewById(R.id.input_mopub_interstitial_video)
        rewardedInput = view.findViewById(R.id.input_mopub_rewarded)
        mediationBannerInput = view.findViewById(R.id.input_mopub_mediation_banner)
        mediationMediumInput = view.findViewById(R.id.input_mopub_mediation_medium)
        mediationMediumVideoInput = view.findViewById(R.id.input_mopub_mediation_medium_video)
        mediationLeaderboardInput = view.findViewById(R.id.input_mopub_mediation_leaderboard)
        mediationInterstitialInput = view.findViewById(R.id.input_mopub_mediation_interstitial)
        mediationInterstitialVideoInput = view.findViewById(R.id.input_mopub_mediation_interstitial_video)
        mediationRewardedInput = view.findViewById(R.id.input_mopub_mediation_rewarded)
        mediationNativeInput = view.findViewById(R.id.input_mopub_mediation_native)

        settingManager = SettingsManager.getInstance(requireContext())

        view.findViewById<Button>(R.id.button_save_mopub_settings).setOnClickListener {
            val bannerAdUnitId = bannerInput.text.toString()
            val mediumAdUnitId = mediumInput.text.toString()
            val mediumVideoAdUnitId = mediumVideoInput.text.toString()
            val leaderboardAdUnitId = leaderboardInput.text.toString()
            val interstitialAdUnitId = interstitialInput.text.toString()
            val interstitialVideoAdUnitId = interstitialVideoInput.text.toString()
            val rewardedAdUnitId = rewardedInput.text.toString()
            val mediationBannerAdUnitId = mediationBannerInput.text.toString()
            val mediationMediumAdUnitId = mediationMediumInput.text.toString()
            val mediationMediumVideoAdUnitId = mediationMediumVideoInput.text.toString()
            val mediationLeaderboardAdUnitId = mediationLeaderboardInput.text.toString()
            val mediationInterstitialAdUnitId = mediationInterstitialInput.text.toString()
            val mediationInterstitialVideoAdUnitId = mediationInterstitialVideoInput.text.toString()
            val mediationRewardedAdUnitId = mediationRewardedInput.text.toString()
            val mediationNativeAdUnitId = mediationNativeInput.text.toString()

            settingManager.setMoPubBannerAdUnitId(bannerAdUnitId)
            settingManager.setMoPubMediumAdUnitId(mediumAdUnitId)
            settingManager.setMoPubMediumVideoAdUnitId(mediumVideoAdUnitId)
            settingManager.setMoPubLeaderboardAdUnitId(leaderboardAdUnitId)
            settingManager.setMoPubInterstitialAdUnitId(interstitialAdUnitId)
            settingManager.setMoPubInterstitialVideoAdUnitId(interstitialVideoAdUnitId)
            settingManager.setMoPubRewardedAdUnitId(rewardedAdUnitId)
            settingManager.setMoPubMediationBannerAdUnitId(mediationBannerAdUnitId)
            settingManager.setMoPubMediationMediumAdUnitId(mediationMediumAdUnitId)
            settingManager.setMoPubMediationMediumVideoAdUnitId(mediationMediumVideoAdUnitId)
            settingManager.setMoPubMediationLeaderboardAdUnitId(mediationLeaderboardAdUnitId)
            settingManager.setMoPubMediationInterstitialAdUnitId(mediationInterstitialAdUnitId)
            settingManager.setMoPubMediationInterstitialVideoAdUnitId(mediationInterstitialVideoAdUnitId)
            settingManager.setMoPubMediationRewardedAdUnitId(mediationRewardedAdUnitId)
            settingManager.setMoPubMediationNativeAdUnitId(mediationNativeAdUnitId)

            val appToken = settingManager.getSettings().appToken

            MoPubManager.initMoPubSdk(activity, bannerAdUnitId, appToken) {
                Toast.makeText(activity, "MoPub settings saved successfully.", Toast.LENGTH_SHORT).show()
                activity?.finish()
            }
        }

        fillSavedValues()
    }

    private fun fillSavedValues() {
        val settings = settingManager.getSettings()
        bannerInput.setText(settings.mopubBannerAdUnitId)
        mediumInput.setText(settings.mopubMediumAdUnitId)
        mediumVideoInput.setText(settings.mopubMediumVideoAdUnitId)
        leaderboardInput.setText(settings.mopubLeaderboardAdUnitId)
        interstitialInput.setText(settings.mopubInterstitialAdUnitId)
        interstitialVideoInput.setText(settings.mopubInterstitialVideoAdUnitId)
        rewardedInput.setText(settings.mopubRewardedAdUnitId)
        mediationBannerInput.setText(settings.mopubMediationBannerAdUnitId)
        mediationMediumInput.setText(settings.mopubMediationMediumAdUnitId)
        mediationMediumVideoInput.setText(settings.mopubMediationMediumVideoAdUnitId)
        mediationLeaderboardInput.setText(settings.mopubMediationLeaderboardAdUnitId)
        mediationInterstitialInput.setText(settings.mopubMediationInterstitialAdUnitId)
        mediationInterstitialVideoInput.setText(settings.mopubMediationInterstitialVideoAdUnitId)
        mediationRewardedInput.setText(settings.mopubMediationRewardedAdUnitId)
        mediationNativeInput.setText(settings.mopubMediationNativeAdUnitId)
    }
}