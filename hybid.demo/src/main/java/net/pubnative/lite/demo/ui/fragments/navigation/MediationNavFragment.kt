package net.pubnative.lite.demo.ui.fragments.navigation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.admob.*
import net.pubnative.lite.demo.ui.activities.chartboost.ChartboostMediationBannerActivity
import net.pubnative.lite.demo.ui.activities.chartboost.ChartboostMediationInterstitialActivity
import net.pubnative.lite.demo.ui.activities.chartboost.ChartboostMediationInterstitialVideoActivity
import net.pubnative.lite.demo.ui.activities.chartboost.ChartboostMediationLeaderboardActivity
import net.pubnative.lite.demo.ui.activities.chartboost.ChartboostMediationMRectActivity
import net.pubnative.lite.demo.ui.activities.chartboost.ChartboostMediationMRectVideoActivity
import net.pubnative.lite.demo.ui.activities.chartboost.ChartboostMediationRewardedActivity
import net.pubnative.lite.demo.ui.activities.chartboost.ChartboostMediationRewardedHtmlActivity
import net.pubnative.lite.demo.ui.activities.dfp.*
import net.pubnative.lite.demo.ui.activities.fairbid.FairbidMediationBannerActivity
import net.pubnative.lite.demo.ui.activities.fairbid.FairbidMediationInterstitialActivity
import net.pubnative.lite.demo.ui.activities.fairbid.FairbidMediationRewardedActivity
import net.pubnative.lite.demo.ui.activities.ironsource.IronSourceMediationBannerActivity
import net.pubnative.lite.demo.ui.activities.ironsource.IronSourceMediationInterstitialActivity
import net.pubnative.lite.demo.ui.activities.ironsource.IronSourceMediationRewardedActivity
import net.pubnative.lite.demo.ui.activities.maxads.*

class MediationNavFragment : Fragment(R.layout.fragment_nav_mediation) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_max_banner).setOnClickListener {
            val intent = Intent(activity, MaxAdsMediationBannerActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_max_mrect).setOnClickListener {
            val intent = Intent(activity, MaxAdsMediationMRectActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_max_interstitial).setOnClickListener {
            val intent = Intent(activity, MaxAdsMediationInterstitialActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_max_rewarded).setOnClickListener {
            val intent = Intent(activity, MaxAdsMediationRewardedActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_max_native).setOnClickListener {
            val intent = Intent(activity, MaxAdsMediationNativeActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_admob_banner).setOnClickListener {
            val intent = Intent(activity, AdmobMediationBannerActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_admob_medium).setOnClickListener {
            val intent = Intent(activity, AdmobMediationMRectActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_admob_medium_video).setOnClickListener {
            val intent = Intent(activity, AdmobMediationMRectVideoActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_admob_leaderboard).setOnClickListener {
            val intent = Intent(activity, AdmobMediationLeaderboardActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_admob_interstitial).setOnClickListener {
            val intent = Intent(activity, AdmobMediationInterstitialActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_admob_interstitial_video).setOnClickListener {
            val intent = Intent(activity, AdmobMediationInterstitialVideoActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_admob_rewarded).setOnClickListener {
            val intent = Intent(activity, AdmobMediationRewardedActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_dfp_banner).setOnClickListener {
            val intent = Intent(activity, DFPMediationBannerActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_dfp_medium).setOnClickListener {
            val intent = Intent(activity, DFPMediationMRectActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_dfp_leaderboard).setOnClickListener {
            val intent = Intent(activity, DFPMediationLeaderboardActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_dfp_interstitial).setOnClickListener {
            val intent = Intent(activity, DFPMediationInterstitialActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_admob_native).setOnClickListener {
            val intent = Intent(activity, AdmobMediationNativeActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_ironsource_banner).setOnClickListener {
            val intent = Intent(activity, IronSourceMediationBannerActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_ironsource_interstitial).setOnClickListener {
            val intent = Intent(activity, IronSourceMediationInterstitialActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_ironsource_rewarded).setOnClickListener {
            val intent = Intent(activity, IronSourceMediationRewardedActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_fairbid_banner).setOnClickListener {
            val intent = Intent(activity, FairbidMediationBannerActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_fairbid_interstitial).setOnClickListener {
            val intent = Intent(activity, FairbidMediationInterstitialActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_fairbid_rewarded).setOnClickListener {
            val intent = Intent(activity, FairbidMediationRewardedActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_chartboost_banner).setOnClickListener {
            val intent = Intent(activity, ChartboostMediationBannerActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_chartboost_medium).setOnClickListener {
            val intent = Intent(activity, ChartboostMediationMRectActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_chartboost_medium_video).setOnClickListener {
            val intent = Intent(activity, ChartboostMediationMRectVideoActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_chartboost_leaderboard).setOnClickListener {
            val intent = Intent(activity, ChartboostMediationLeaderboardActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_chartboost_interstitial).setOnClickListener {
            val intent = Intent(activity, ChartboostMediationInterstitialActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_chartboost_interstitial_video).setOnClickListener {
            val intent = Intent(activity, ChartboostMediationInterstitialVideoActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_chartboost_rewarded_html).setOnClickListener {
            val intent = Intent(activity, ChartboostMediationRewardedHtmlActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_chartboost_rewarded).setOnClickListener {
            val intent = Intent(activity, ChartboostMediationRewardedActivity::class.java)
            startActivity(intent)
        }
    }
}