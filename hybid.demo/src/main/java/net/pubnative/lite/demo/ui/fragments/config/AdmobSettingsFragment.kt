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

class AdmobSettingsFragment : Fragment(R.layout.fragment_admob_settings) {

    private lateinit var appIdInput: EditText
    private lateinit var bannerInput: EditText
    private lateinit var mediumInput: EditText
    private lateinit var mediumVideoInput: EditText
    private lateinit var leaderboardInput: EditText
    private lateinit var interstitialInput: EditText
    private lateinit var interstitialVideoInput: EditText
    private lateinit var nativeInput: EditText
    private lateinit var rewardedInput: EditText
    private lateinit var settingManager: SettingsManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appIdInput = view.findViewById(R.id.input_admob_app_id)
        bannerInput = view.findViewById(R.id.input_admob_banner)
        mediumInput = view.findViewById(R.id.input_admob_medium)
        mediumVideoInput = view.findViewById(R.id.input_admob_medium_video)
        leaderboardInput = view.findViewById(R.id.input_admob_leaderboard)
        interstitialInput = view.findViewById(R.id.input_admob_interstitial)
        interstitialVideoInput = view.findViewById(R.id.input_admob_interstitial_video)
        rewardedInput = view.findViewById(R.id.input_admob_rewarded)
        nativeInput = view.findViewById(R.id.input_admob_native)

        settingManager = SettingsManager.getInstance(requireContext())

        view.findViewById<Button>(R.id.button_save_admob_settings).setOnClickListener {
            val appId = appIdInput.text.toString()
            val bannerAdUnitId = bannerInput.text.toString()
            val mediumAdUnitId = mediumInput.text.toString()
            val mediumVideoAdUnitId = mediumVideoInput.text.toString()
            val leaderboardAdUnitId = leaderboardInput.text.toString()
            val interstitialAdUnitId = interstitialInput.text.toString()
            val interstitialVideoAdUnitId = interstitialVideoInput.text.toString()
            val rewardedAdUnitId = rewardedInput.text.toString()
            val nativeAdUnitId = nativeInput.text.toString()

            settingManager.setAdmobAppId(appId)
            settingManager.setAdmobBannerAdUnitId(bannerAdUnitId)
            settingManager.setAdmobMediumAdUnitId(mediumAdUnitId)
            settingManager.setAdmobMediumVideoAdUnitId(mediumVideoAdUnitId)
            settingManager.setAdmobLeaderboardAdUnitId(leaderboardAdUnitId)
            settingManager.setAdmobInterstitialAdUnitId(interstitialAdUnitId)
            settingManager.setAdmobInterstitialVideoAdUnitId(interstitialVideoAdUnitId)
            settingManager.setAdmobNativeAdUnitId(nativeAdUnitId)
            settingManager.setAdmobRewardedAdUnitId(rewardedAdUnitId)

            Toast.makeText(activity, "Admob settings saved successfully.", Toast.LENGTH_SHORT)
                .show()
            activity?.finish()
        }

        fillSavedValues()
    }

    private fun fillSavedValues() {
        val settings = settingManager.getSettings().admobSettings
        if (settings != null) {
            appIdInput.setText(settings.appId)
            bannerInput.setText(settings.bannerAdUnitId)
            mediumInput.setText(settings.mediumAdUnitId)
            mediumVideoInput.setText(settings.mediumVideoAdUnitId)
            leaderboardInput.setText(settings.leaderboardAdUnitId)
            interstitialInput.setText(settings.interstitialAdUnitId)
            interstitialVideoInput.setText(settings.interstitialVideoAdUnitId)
            rewardedInput.setText(settings.rewardedAdUnitId)
            nativeInput.setText(settings.nativeAdUnitId)
        }
    }
}