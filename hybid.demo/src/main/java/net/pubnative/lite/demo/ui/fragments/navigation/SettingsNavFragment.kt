package net.pubnative.lite.demo.ui.fragments.navigation

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.BuildConfig
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.AdCustomizationActivity
import net.pubnative.lite.demo.ui.activities.CustomSDKDataActivity
import net.pubnative.lite.demo.ui.activities.admob.MediationTestSuiteActivity
import net.pubnative.lite.demo.ui.activities.config.*
import net.pubnative.lite.demo.ui.activities.creativetester.P161CreativeTesterActivity
import net.pubnative.lite.demo.ui.activities.markup.MarkupActivity
import net.pubnative.lite.demo.ui.activities.signaldata.SignalDataActivity
import net.pubnative.lite.demo.ui.activities.vast.VastTagRequestActivity

class SettingsNavFragment : Fragment(R.layout.fragment_nav_settings) {

    private lateinit var versionTextView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        versionTextView = view.findViewById(R.id.text_version)

        view.findViewById<TextView>(R.id.button_pn_settings).setOnClickListener {
            val intent = Intent(activity, HyBidSettingsActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<TextView>(R.id.button_maxads_settings).setOnClickListener {
            val intent = Intent(activity, MaxAdsSettingsActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<TextView>(R.id.button_admob_settings).setOnClickListener {
            val intent = Intent(activity, AdmobSettingsActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<TextView>(R.id.button_fairbid_settings).setOnClickListener {
            val intent = Intent(activity, FairbidSettingsActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<TextView>(R.id.button_dfp_settings).setOnClickListener {
            val intent = Intent(activity, DFPSettingsActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<TextView>(R.id.button_ironsource_settings).setOnClickListener {
            val intent = Intent(activity, IronSourceSettingsActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<TextView>(R.id.button_markup).setOnClickListener {
            val intent = Intent(activity, MarkupActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<TextView>(R.id.button_creative_tester).setOnClickListener {
            val intent = Intent(activity, P161CreativeTesterActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<TextView>(R.id.button_signal_data).setOnClickListener {
            val intent = Intent(activity, SignalDataActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<TextView>(R.id.button_render_vast).setOnClickListener {
            val intent = Intent(activity, VastTagRequestActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<TextView>(R.id.button_mediation_test_suite).setOnClickListener {
            val intent = Intent(activity, MediationTestSuiteActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<TextView>(R.id.button_custom_sdk_data).setOnClickListener {
            val intent = Intent(activity, CustomSDKDataActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<TextView>(R.id.button_ad_customization).setOnClickListener {
            val intent = Intent(activity, AdCustomizationActivity::class.java)
            startActivity(intent)
        }

        setBuildAndVersion()
    }

    private fun setBuildAndVersion() {
        val buildVersion = BuildConfig.VERSION_CODE
        val sdkVersion = net.pubnative.lite.sdk.BuildConfig.SDK_VERSION

        versionTextView.text = String.format(
            getString(R.string.sdk_and_build_version_concat),
            sdkVersion,
            buildVersion
        )
    }
}