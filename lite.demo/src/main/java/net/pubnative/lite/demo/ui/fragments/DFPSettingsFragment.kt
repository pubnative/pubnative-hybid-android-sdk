package net.pubnative.lite.demo.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
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
    private lateinit var interstitialInput: EditText
    private lateinit var settingManager: SettingsManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_dfp_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bannerInput = view.findViewById(R.id.input_dfp_banner)
        mediumInput = view.findViewById(R.id.input_dfp_medium)
        interstitialInput = view.findViewById(R.id.input_dfp_interstitial)

        settingManager = SettingsManager.getInstance(context!!)

        view.findViewById<Button>(R.id.button_save_dfp_settings).setOnClickListener {
            val bannerAdUnitId = bannerInput.text.toString()
            val mediumAdUnitId = mediumInput.text.toString()
            val interstitialAdUnitId = interstitialInput.text.toString()

            settingManager.setDFPBannerAdUnitId(bannerAdUnitId)
            settingManager.setDFPMediumAdUnitId(mediumAdUnitId)
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
        interstitialInput.setText(settings.dfpInterstitialAdUnitId)
    }
}