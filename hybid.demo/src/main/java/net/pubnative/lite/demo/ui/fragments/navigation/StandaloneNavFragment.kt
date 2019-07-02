package net.pubnative.lite.demo.ui.fragments.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.hybid.*
import net.pubnative.lite.demo.ui.adapters.ZoneIdAdapter
import net.pubnative.lite.demo.ui.listeners.ZoneIdClickListener

class StandaloneNavFragment : Fragment() {

    private lateinit var bannerButton: Button
    private lateinit var mediumButton: Button
    private lateinit var leaderboardButton: Button
    private lateinit var interstitialButton: Button
    private lateinit var nativeButton: Button

    private lateinit var zoneIdList: RecyclerView
    private lateinit var chosenZoneIdView: TextView
    private lateinit var adapter: ZoneIdAdapter
    private lateinit var settingsManager: SettingsManager

    private var chosenZoneId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsManager = SettingsManager.getInstance(activity!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_nav_standalone, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chosenZoneIdView = view.findViewById(R.id.view_chosen_zone_id)
        zoneIdList = view.findViewById(R.id.list_zone_ids)
        val layoutManager = GridLayoutManager(activity, 5, RecyclerView.VERTICAL, false)
        zoneIdList.layoutManager = layoutManager
        adapter = ZoneIdAdapter(object : ZoneIdClickListener {
            override fun onZoneIdClicked(zoneId: String) {
                chosenZoneId = zoneId
                chosenZoneIdView.text = zoneId
                enableZones()
            }
        })
        zoneIdList.adapter = adapter

        bannerButton = view.findViewById(R.id.button_banner)
        bannerButton.setOnClickListener {
            val intent = Intent(activity, HyBidBannerActivity::class.java)
            intent.putExtra(Constants.IntentParams.ZONE_ID, chosenZoneId)
            startActivity(intent)
        }

        mediumButton = view.findViewById(R.id.button_medium)
        mediumButton.setOnClickListener {
            val intent = Intent(activity, HyBidMRectActivity::class.java)
            intent.putExtra(Constants.IntentParams.ZONE_ID, chosenZoneId)
            startActivity(intent)
        }

        leaderboardButton = view.findViewById(R.id.button_leaderboard)
        leaderboardButton.setOnClickListener {
            val intent = Intent(activity, HyBidLeaderboardActivity::class.java)
            intent.putExtra(Constants.IntentParams.ZONE_ID, chosenZoneId)
            startActivity(intent)
        }

        interstitialButton = view.findViewById(R.id.button_interstitial)
        interstitialButton.setOnClickListener {
            val intent = Intent(activity, HyBidInterstitialActivity::class.java)
            intent.putExtra(Constants.IntentParams.ZONE_ID, chosenZoneId)
            startActivity(intent)
        }

        nativeButton = view.findViewById(R.id.button_native)
        nativeButton.setOnClickListener {
            val intent = Intent(activity, HyBidNativeActivity::class.java)
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
        bannerButton.isEnabled = true
        mediumButton.isEnabled = true
        leaderboardButton.isEnabled = true
        interstitialButton.isEnabled = true
        nativeButton.isEnabled = true
    }

    private fun disableZones() {
        chosenZoneIdView.text = ""
        chosenZoneId = ""
        bannerButton.isEnabled = false
        mediumButton.isEnabled = false
        leaderboardButton.isEnabled = false
        interstitialButton.isEnabled = false
        nativeButton.isEnabled = false
    }
}