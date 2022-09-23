package net.pubnative.lite.demo.ui.fragments.navigation

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
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
import net.pubnative.lite.demo.ui.activities.fairbid.FairbidBannerActivity
import net.pubnative.lite.demo.ui.activities.fairbid.FairbidInterstitialActivity
import net.pubnative.lite.demo.ui.activities.fairbid.FairbidRewardedActivity
import net.pubnative.lite.demo.ui.adapters.ZoneIdAdapter
import net.pubnative.lite.demo.ui.listeners.ZoneIdClickListener

class PrebidNavFragment : Fragment(R.layout.fragment_nav_prebid) {

    private lateinit var dfpBannerButton: Button
    private lateinit var dfpMediumButton: Button
    private lateinit var dfpLeaderboardButton: Button
    private lateinit var dfpInterstitialButton: Button

    private lateinit var fairbidBannerButton: Button
    private lateinit var fairbidInterstitialButton: Button
    private lateinit var fairbidRewardedButton: Button

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

        dfpBannerButton = view.findViewById(R.id.button_dfp_banner)
        dfpBannerButton.setOnClickListener {
            val intent = Intent(activity, DFPBannerActivity::class.java)
            val zoneId = setZoneId(chosenZoneId, AdFormat.BANNER)
            intent.putExtra(Constants.IntentParams.ZONE_ID, zoneId)
            startActivity(intent)
        }

        dfpMediumButton = view.findViewById(R.id.button_dfp_medium)
        dfpMediumButton.setOnClickListener {
            val intent = Intent(activity, DFPMRectActivity::class.java)
            val zoneId = setZoneId(chosenZoneId, AdFormat.MEDIUM)
            intent.putExtra(Constants.IntentParams.ZONE_ID, zoneId)
            startActivity(intent)
        }

        dfpLeaderboardButton = view.findViewById(R.id.button_dfp_leaderboard)
        dfpLeaderboardButton.setOnClickListener {
            val intent = Intent(activity, DFPLeaderboardActivity::class.java)
            val zoneId = setZoneId(chosenZoneId, AdFormat.LEADERBOARD)
            intent.putExtra(Constants.IntentParams.ZONE_ID, zoneId)
            startActivity(intent)
        }

        dfpInterstitialButton = view.findViewById(R.id.button_dfp_interstitial)
        dfpInterstitialButton.setOnClickListener {
            val intent = Intent(activity, DFPInterstitialActivity::class.java)
            val zoneId = setZoneId(chosenZoneId, AdFormat.INTERSTITIAL)
            intent.putExtra(Constants.IntentParams.ZONE_ID, zoneId)
            startActivity(intent)
        }

        fairbidBannerButton = view.findViewById(R.id.button_fairbid_banner)
        fairbidBannerButton.setOnClickListener {
            val intent = Intent(activity, FairbidBannerActivity::class.java)
            val zoneId = setZoneId(chosenZoneId, AdFormat.BANNER)
            intent.putExtra(Constants.IntentParams.ZONE_ID, zoneId)
            startActivity(intent)
        }

        fairbidInterstitialButton = view.findViewById(R.id.button_fairbid_interstitial)
        fairbidInterstitialButton.setOnClickListener {
            val intent = Intent(activity, FairbidInterstitialActivity::class.java)
            val zoneId = setZoneId(chosenZoneId, AdFormat.INTERSTITIAL)
            intent.putExtra(Constants.IntentParams.ZONE_ID, zoneId)
            startActivity(intent)
        }

        fairbidRewardedButton = view.findViewById(R.id.button_fairbid_rewarded)
        fairbidRewardedButton.setOnClickListener {
            val intent = Intent(activity, FairbidRewardedActivity::class.java)
            val zoneId = setZoneId(chosenZoneId, AdFormat.INTERSTITIAL)
            intent.putExtra(Constants.IntentParams.ZONE_ID, zoneId)
            startActivity(intent)
        }


        enableZones()
    }

    override fun onResume() {
        super.onResume()
        fillSavedZoneIds()
    }

    private fun fillSavedZoneIds() {
        val settings = settingsManager.getSettings().hybidSettings
        adapter.clear()
        val zoneIds = settings?.zoneIds
        if (zoneIds != null) {
            adapter.addZoneIds(zoneIds)
        }
    }

    private fun enableZones() {
        dfpBannerButton.isEnabled = true
        dfpMediumButton.isEnabled = true
        dfpLeaderboardButton.isEnabled = true
        dfpInterstitialButton.isEnabled = true
    }

    private fun disableZones() {
        chosenZoneIdView.text = ""
        chosenZoneId = ""
        dfpBannerButton.isEnabled = false
        dfpMediumButton.isEnabled = false
        dfpLeaderboardButton.isEnabled = false
        dfpInterstitialButton.isEnabled = false
    }

    enum class AdFormat {
        BANNER, MEDIUM, MEDIUM_VIDEO, LEADERBOARD, INTERSTITIAL, INTERSTITIAL_VIDEO, REWARDED, NATIVE,
    }

    private fun setZoneId(zoneId: String?, adFormat: AdFormat): String? {
        if (!TextUtils.isEmpty(zoneId)) {
            return zoneId
        } else {
            return when (adFormat) {
                AdFormat.BANNER -> "2"
                AdFormat.MEDIUM -> "5"
                AdFormat.MEDIUM_VIDEO -> "6"
                AdFormat.LEADERBOARD -> "8"
                AdFormat.INTERSTITIAL -> "3"
                AdFormat.INTERSTITIAL_VIDEO -> "4"
                AdFormat.REWARDED -> "4"
                else -> {
                    "1"
                }
            }
        }
    }
}