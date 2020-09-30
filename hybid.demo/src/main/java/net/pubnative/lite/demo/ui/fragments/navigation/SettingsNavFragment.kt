package net.pubnative.lite.demo.ui.fragments.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.config.AdmobSettingsActivity
import net.pubnative.lite.demo.ui.activities.config.DFPSettingsActivity
import net.pubnative.lite.demo.ui.activities.config.HyBidSettingsActivity
import net.pubnative.lite.demo.ui.activities.config.MoPubSettingsActivity
import net.pubnative.lite.demo.ui.activities.markup.MarkupActivity
import net.pubnative.lite.demo.ui.activities.signaldata.SignalDataActivity
import net.pubnative.lite.demo.ui.activities.vast.VastTagRequestActivity

class SettingsNavFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_nav_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.button_pn_settings).setOnClickListener {
            val intent = Intent(activity, HyBidSettingsActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<TextView>(R.id.button_mopub_settings).setOnClickListener {
            val intent = Intent(activity, MoPubSettingsActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<TextView>(R.id.button_admob_settings).setOnClickListener {
            val intent = Intent(activity, AdmobSettingsActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<TextView>(R.id.button_dfp_settings).setOnClickListener {
            val intent = Intent(activity, DFPSettingsActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<TextView>(R.id.button_markup).setOnClickListener {
            val intent = Intent(activity, MarkupActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<TextView>(R.id.button_signal_data).setOnClickListener {
            val intent = Intent(activity, SignalDataActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<TextView>(R.id.button_render_vast).setOnClickListener{
            val intent = Intent(activity, VastTagRequestActivity::class.java)
            startActivity(intent)
        }
    }
}