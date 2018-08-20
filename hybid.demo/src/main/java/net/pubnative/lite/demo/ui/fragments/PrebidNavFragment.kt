package net.pubnative.lite.demo.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.dfp.DFPActivity
import net.pubnative.lite.demo.ui.activities.mopub.MoPubActivity
import net.pubnative.lite.demo.ui.adapters.ZoneIdAdapter
import net.pubnative.lite.demo.ui.listeners.ZoneIdClickListener

class PrebidNavFragment : Fragment() {

    private lateinit var mopubButton: Button
    private lateinit var dfpButton: Button

    private lateinit var zoneIdList: RecyclerView
    private lateinit var chosenZoneIdView: TextView
    private lateinit var adapter: ZoneIdAdapter
    private lateinit var settingsManager: SettingsManager

    private var chosenZoneId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsManager = SettingsManager.getInstance(activity!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_nav_prebid, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chosenZoneIdView = view.findViewById(R.id.view_chosen_zone_id)
        zoneIdList = view.findViewById(R.id.list_zone_ids)
        val layoutManager = GridLayoutManager(activity, 5, LinearLayoutManager.VERTICAL, false)
        zoneIdList.layoutManager = layoutManager
        adapter = ZoneIdAdapter(object : ZoneIdClickListener {
            override fun onZoneIdClicked(zoneId: String) {
                chosenZoneId = zoneId
                chosenZoneIdView.text = zoneId
                enableZones()
            }
        })
        zoneIdList.adapter = adapter

        mopubButton = view.findViewById(R.id.button_mopub)
        mopubButton.setOnClickListener {
            val intent = Intent(activity, MoPubActivity::class.java)
            intent.putExtra(Constants.IntentParams.ZONE_ID, chosenZoneId)
            startActivity(intent)
        }

        dfpButton = view.findViewById(R.id.button_dfp)
        dfpButton.setOnClickListener {
            val intent = Intent(activity, DFPActivity::class.java)
            intent.putExtra(Constants.IntentParams.ZONE_ID, chosenZoneId)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        fillSavedZoneIds()
    }

    private fun fillSavedZoneIds() {
        val settings = settingsManager.getSettings()
        adapter.clear()
        val zoneIds = settings.zoneIds
        adapter.addZoneIds(zoneIds)
        if (!zoneIds.contains(chosenZoneId)) {
            disableZones()
        }
    }

    private fun enableZones() {
        mopubButton.isEnabled = true
        dfpButton.isEnabled = true
    }

    private fun disableZones() {
        chosenZoneIdView.text = ""
        chosenZoneId = ""
        mopubButton.isEnabled = false
        dfpButton.isEnabled = false
    }
}