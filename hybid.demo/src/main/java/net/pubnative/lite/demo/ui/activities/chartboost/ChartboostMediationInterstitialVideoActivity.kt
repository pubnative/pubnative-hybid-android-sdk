package net.pubnative.lite.demo.ui.activities.chartboost

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.chartboost.ChartboostMediationInterstitialVideoFragment

class ChartboostMediationInterstitialVideoActivity : TabActivity() {
    override fun getAdFragment() = ChartboostMediationInterstitialVideoFragment()

    override fun getActivityTitle() = getString(R.string.chartboost_mediation_interstitial_video)
}