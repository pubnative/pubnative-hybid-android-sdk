package net.pubnative.lite.demo.ui.fragments.config

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.fyber.FairBid
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager

class FairbidSettingsFragment : Fragment(R.layout.fragment_fairbid_settings) {
    private lateinit var appId: EditText

    private lateinit var mediationBannerInput: EditText
    private lateinit var mediationInterstitialInput: EditText
    private lateinit var mediationRewardedInput: EditText

    private lateinit var bannerInput: EditText
    private lateinit var interstitialInput: EditText
    private lateinit var rewardedInput: EditText

    private lateinit var settingManager: SettingsManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appId = view.findViewById(R.id.input_fairbid_app_id)
        mediationBannerInput = view.findViewById(R.id.input_fairbid_mediation_banner)
        mediationInterstitialInput = view.findViewById(R.id.input_fairbid_mediation_interstitial)
        mediationRewardedInput = view.findViewById(R.id.input_fairbid_mediation_rewarded)

        bannerInput = view.findViewById(R.id.input_fairbid_banner)
        interstitialInput = view.findViewById(R.id.input_fairbid_interstitial)
        rewardedInput = view.findViewById(R.id.input_fairbid_rewarded)

        settingManager = SettingsManager.getInstance(requireContext())

        view.findViewById<Button>(R.id.button_save_fairbid_settings).setOnClickListener {
            val fairbidAppId = appId.text.toString()

            val mediationBannerAdUnitId = mediationBannerInput.text.toString()
            val mediationInterstitialAdUnitId = mediationInterstitialInput.text.toString()
            val mediationRewardedAdUnitId = mediationRewardedInput.text.toString()

            val bannerAdUnitId = bannerInput.text.toString()
            val interstitialAdUnitId = interstitialInput.text.toString()
            val rewardedAdUnitId = rewardedInput.text.toString()

            settingManager.setFairbidAppId(fairbidAppId)
            settingManager.setFairbidMediationBannerAdUnitId(mediationBannerAdUnitId)
            settingManager.setFairbidMediationInterstitialAdUnitId(mediationInterstitialAdUnitId)
            settingManager.setFairbidMediationRewardedAdUnitId(mediationRewardedAdUnitId)

            settingManager.setFairbidBannerAdUnitId(bannerAdUnitId)
            settingManager.setFairbidInterstitialAdUnitId(interstitialAdUnitId)
            settingManager.setFairbidRewardedAdUnitId(rewardedAdUnitId)

            Toast.makeText(activity, "Fairbid settings saved successfully.", Toast.LENGTH_SHORT)
                .show()
            activity?.finish()
        }

        view.findViewById<Button>(R.id.button_init_fairbid).setOnClickListener {
            initializeFairbid()
        }

        fillSavedValues()
    }

    private fun fillSavedValues() {
        val settings = settingManager.getSettings()
        appId.setText(settings.fairbidAppId)
        mediationBannerInput.setText(settings.fairbidMediationBannerAdUnitId)
        mediationInterstitialInput.setText(settings.fairbidMediationInterstitialAdUnitId)
        mediationRewardedInput.setText(settings.fairbidMediationRewardedAdUnitId)

        bannerInput.setText(settings.fairbidBannerAdUnitId)
        interstitialInput.setText(settings.fairbidInterstitialAdUnitId)
        rewardedInput.setText(settings.fairbidRewardedAdUnitId)
    }

    private fun initializeFairbid() {
        val settings = settingManager.getSettings()
        val appKey = settings.fairbidAppId
        val isInitialized = FairBid.hasStarted()
        if (!TextUtils.isEmpty(appKey) && !isInitialized) {
            FairBid.start(Constants.FAIRBID_APP_ID, activity)
        }
    }
}