package net.pubnative.lite.demo.ui.fragments.navigation

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
import net.pubnative.lite.demo.ui.activities.dfp.DFPBannerActivity
import net.pubnative.lite.demo.ui.activities.dfp.DFPInterstitialActivity
import net.pubnative.lite.demo.ui.activities.dfp.DFPMRectActivity
import net.pubnative.lite.demo.ui.activities.mopub.MoPubBannerActivity
import net.pubnative.lite.demo.ui.activities.mopub.MoPubInterstitialActivity
import net.pubnative.lite.demo.ui.activities.mopub.MoPubMRectActivity
import net.pubnative.lite.demo.ui.adapters.ZoneIdAdapter
import net.pubnative.lite.demo.ui.listeners.ZoneIdClickListener

class PrebidNavFragment : Fragment() {

    private lateinit var mopubBannerButton: Button
    private lateinit var mopubMediumButton: Button
    private lateinit var mopubInterstitialButton: Button
    private lateinit var dfpBannerButton: Button
    private lateinit var dfpMediumButton: Button
    private lateinit var dfpInterstitialButton: Button

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

        mopubBannerButton = view.findViewById(R.id.button_mopub_banner)
        mopubBannerButton.setOnClickListener {
            val intent = Intent(activity, MoPubBannerActivity::class.java)
            intent.putExtra(Constants.IntentParams.ZONE_ID, chosenZoneId)
            startActivity(intent)
        }

        mopubMediumButton = view.findViewById(R.id.button_mopub_medium)
        mopubMediumButton.setOnClickListener {
            val intent = Intent(activity, MoPubMRectActivity::class.java)
            intent.putExtra(Constants.IntentParams.ZONE_ID, chosenZoneId)
            startActivity(intent)
        }

        mopubInterstitialButton = view.findViewById(R.id.button_mopub_interstitial)
        mopubInterstitialButton.setOnClickListener {
            val intent = Intent(activity, MoPubInterstitialActivity::class.java)
            intent.putExtra(Constants.IntentParams.ZONE_ID, chosenZoneId)
            startActivity(intent)
        }

        dfpBannerButton = view.findViewById(R.id.button_dfp_banner)
        dfpBannerButton.setOnClickListener {
            val intent = Intent(activity, DFPBannerActivity::class.java)
            intent.putExtra(Constants.IntentParams.ZONE_ID, chosenZoneId)
            startActivity(intent)
        }

        dfpMediumButton = view.findViewById(R.id.button_dfp_medium)
        dfpMediumButton.setOnClickListener {
            val intent = Intent(activity, DFPMRectActivity::class.java)
            intent.putExtra(Constants.IntentParams.ZONE_ID, chosenZoneId)
            startActivity(intent)
        }

        dfpInterstitialButton = view.findViewById(R.id.button_dfp_interstitial)
        dfpInterstitialButton.setOnClickListener {
            val intent = Intent(activity, DFPInterstitialActivity::class.java)
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
        mopubBannerButton.isEnabled = true
        mopubMediumButton.isEnabled = true
        mopubInterstitialButton.isEnabled = true
        dfpBannerButton.isEnabled = true
        dfpMediumButton.isEnabled = true
        dfpInterstitialButton.isEnabled = true
    }

    private fun disableZones() {
        chosenZoneIdView.text = ""
        chosenZoneId = ""
        mopubBannerButton.isEnabled = false
        mopubMediumButton.isEnabled = false
        mopubInterstitialButton.isEnabled = false
        dfpBannerButton.isEnabled = false
        dfpMediumButton.isEnabled = false
        dfpInterstitialButton.isEnabled = false
    }
}