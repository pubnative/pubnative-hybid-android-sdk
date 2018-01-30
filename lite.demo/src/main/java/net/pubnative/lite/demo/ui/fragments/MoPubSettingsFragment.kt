package net.pubnative.lite.demo.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import net.pubnative.lite.demo.R

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class MoPubSettingsFragment : Fragment() {

    private lateinit var bannerInput: EditText
    private lateinit var mediumInput: EditText
    private lateinit var interstitialInput: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_mopub_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bannerInput = view.findViewById(R.id.input_mopub_banner)
        mediumInput = view.findViewById(R.id.input_mopub_medium)
        interstitialInput = view.findViewById(R.id.input_mopub_interstitial)

        view.findViewById<Button>(R.id.button_save_mopub_settings).setOnClickListener {

        }
    }
}