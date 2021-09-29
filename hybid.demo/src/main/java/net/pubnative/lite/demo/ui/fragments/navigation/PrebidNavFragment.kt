package net.pubnative.lite.demo.ui.fragments.navigation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.dfp.DFPBannerActivity
import net.pubnative.lite.demo.ui.activities.dfp.DFPInterstitialActivity
import net.pubnative.lite.demo.ui.activities.dfp.DFPLeaderboardActivity
import net.pubnative.lite.demo.ui.activities.dfp.DFPMRectActivity
import net.pubnative.lite.demo.ui.activities.mopub.*
import net.pubnative.lite.demo.ui.adapters.ZoneIdAdapter
import net.pubnative.lite.demo.ui.listeners.ZoneIdClickListener

class PrebidNavFragment : Fragment(R.layout.fragment_nav_prebid) {

    private lateinit var mopubBannerButton: Button
    private lateinit var mopubMediumButton: Button
    private lateinit var mopubMediumVideoButton: Button
    private lateinit var mopubLeaderboardButton: Button
    private lateinit var mopubInterstitialButton: Button
    private lateinit var mopubInterstitialVideoButton: Button
    private lateinit var mopubRewardedButton: Button
    private lateinit var dfpBannerButton: Button
    private lateinit var dfpMediumButton: Button
    private lateinit var dfpLeaderboardButton: Button
    private lateinit var dfpInterstitialButton: Button

    private lateinit var zoneIdList: RecyclerView
    private lateinit var chosenZoneIdView: TextView
    private lateinit var adapter: ZoneIdAdapter
    private lateinit var settingsManager: SettingsManager

    private var chosenZoneId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsManager = SettingsManager.getInstance(requireActivity())
    }

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

        mopubMediumVideoButton = view.findViewById(R.id.button_mopub_medium_video)
        mopubMediumVideoButton.setOnClickListener {
            val intent = Intent(activity, MoPubMRectVideoActivity::class.java)
            intent.putExtra(Constants.IntentParams.ZONE_ID, chosenZoneId)
            startActivity(intent)
        }

        mopubLeaderboardButton = view.findViewById(R.id.button_mopub_leaderboard)
        mopubLeaderboardButton.setOnClickListener {
            val intent = Intent(activity, MoPubLeaderboardActivity::class.java)
            intent.putExtra(Constants.IntentParams.ZONE_ID, chosenZoneId)
            startActivity(intent)
        }

        mopubInterstitialButton = view.findViewById(R.id.button_mopub_interstitial)
        mopubInterstitialButton.setOnClickListener {
            val intent = Intent(activity, MoPubInterstitialActivity::class.java)
            intent.putExtra(Constants.IntentParams.ZONE_ID, chosenZoneId)
            startActivity(intent)
        }

        mopubInterstitialVideoButton = view.findViewById(R.id.button_mopub_interstitial_video)
        mopubInterstitialVideoButton.setOnClickListener {
            val intent = Intent(activity, MoPubInterstitialVideoActivity::class.java)
            intent.putExtra(Constants.IntentParams.ZONE_ID, chosenZoneId)
            startActivity(intent)
        }

        mopubRewardedButton = view.findViewById(R.id.button_mopub_rewarded)
        mopubRewardedButton.setOnClickListener {
            val intent = Intent(activity, MoPubRewardedActivity::class.java)
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

        dfpLeaderboardButton = view.findViewById(R.id.button_dfp_leaderboard)
        dfpLeaderboardButton.setOnClickListener {
            val intent = Intent(activity, DFPLeaderboardActivity::class.java)
            intent.putExtra(Constants.IntentParams.ZONE_ID, chosenZoneId)
            startActivity(intent)
        }

        dfpInterstitialButton = view.findViewById(R.id.button_dfp_interstitial)
        dfpInterstitialButton.setOnClickListener {
            val intent = Intent(activity, DFPInterstitialActivity::class.java)
            intent.putExtra(Constants.IntentParams.ZONE_ID, chosenZoneId)
            startActivity(intent)
        }
        disableZones()
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
        mopubMediumVideoButton.isEnabled = true
        mopubLeaderboardButton.isEnabled = true
        mopubInterstitialButton.isEnabled = true
        mopubInterstitialVideoButton.isEnabled = true
        mopubRewardedButton.isEnabled = true
        dfpBannerButton.isEnabled = true
        dfpMediumButton.isEnabled = true
        dfpLeaderboardButton.isEnabled = true
        dfpInterstitialButton.isEnabled = true
    }

    private fun disableZones() {
        chosenZoneIdView.text = ""
        chosenZoneId = ""
        mopubBannerButton.isEnabled = false
        mopubMediumButton.isEnabled = false
        mopubMediumVideoButton.isEnabled = false
        mopubLeaderboardButton.isEnabled = false
        mopubInterstitialButton.isEnabled = false
        mopubInterstitialVideoButton.isEnabled = false
        mopubRewardedButton.isEnabled = false
        dfpBannerButton.isEnabled = false
        dfpMediumButton.isEnabled = false
        dfpLeaderboardButton.isEnabled = false
        dfpInterstitialButton.isEnabled = false
    }
}