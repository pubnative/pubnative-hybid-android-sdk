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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class DFPSettingsFragment : Fragment() {

    private lateinit var bannerInput: EditText
    private lateinit var mediumInput: EditText
    private lateinit var leaderboardInput: EditText
    private lateinit var interstitialInput: EditText
    private lateinit var settingManager: SettingsManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_dfp_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bannerInput = view.findViewById(R.id.input_dfp_banner)
        mediumInput = view.findViewById(R.id.input_dfp_medium)
        leaderboardInput = view.findViewById(R.id.input_dfp_leaderboard)
        interstitialInput = view.findViewById(R.id.input_dfp_interstitial)

        settingManager = SettingsManager.getInstance(context!!)

        view.findViewById<Button>(R.id.button_save_dfp_settings).setOnClickListener {
            val bannerAdUnitId = bannerInput.text.toString()
            val mediumAdUnitId = mediumInput.text.toString()
            val leaderboardAdUnitId = leaderboardInput.text.toString()
            val interstitialAdUnitId = interstitialInput.text.toString()

            settingManager.setDFPBannerAdUnitId(bannerAdUnitId)
            settingManager.setDFPMediumAdUnitId(mediumAdUnitId)
            settingManager.setDFPLeaderboardAdUnitId(leaderboardAdUnitId)
            settingManager.setDFPInterstitialAdUnitId(interstitialAdUnitId)

            Toast.makeText(activity, "DFP settings saved successfully.", Toast.LENGTH_SHORT).show()
            activity?.finish()
        }

        fillSavedValues()
    }

    private fun fillSavedValues() {
        val settings = settingManager.getSettings()
        bannerInput.setText(settings.dfpBannerAdUnitId)
        mediumInput.setText(settings.dfpMediumAdUnitId)
        leaderboardInput.setText(settings.dfpLeaderboardAdUnitId)
        interstitialInput.setText(settings.dfpInterstitialAdUnitId)
    }
}