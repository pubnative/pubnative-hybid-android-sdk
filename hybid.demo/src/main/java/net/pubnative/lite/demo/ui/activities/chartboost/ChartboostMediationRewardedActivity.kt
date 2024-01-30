package net.pubnative.lite.demo.ui.activities.chartboost

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.chartboost.ChartboostMediationRewardedFragment

class ChartboostMediationRewardedActivity : TabActivity() {
    override fun getAdFragment() = ChartboostMediationRewardedFragment()

    override fun getActivityTitle() = getString(R.string.chartboost_mediation_rewarded)

}