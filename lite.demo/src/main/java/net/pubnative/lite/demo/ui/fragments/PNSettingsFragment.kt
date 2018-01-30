package net.pubnative.lite.demo.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.KeywordsActivity
import net.pubnative.lite.demo.ui.activities.ZoneIdsActivity

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class PNSettingsFragment : Fragment() {
    val REQUEST_KEYWORDS = 1
    val REQUEST_ZONE_IDS = 2

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_pn_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_keywords).setOnClickListener {
            val intent = Intent(activity, KeywordsActivity::class.java)
            startActivityForResult(intent, REQUEST_KEYWORDS)
        }

        view.findViewById<Button>(R.id.button_zone_ids).setOnClickListener {
            val intent = Intent(activity, ZoneIdsActivity::class.java)
            startActivityForResult(intent, REQUEST_ZONE_IDS)
        }
    }
}