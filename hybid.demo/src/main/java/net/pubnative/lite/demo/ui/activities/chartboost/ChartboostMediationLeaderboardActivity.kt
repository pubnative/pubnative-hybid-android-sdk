package net.pubnative.lite.demo.ui.activities.chartboost

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.chartboost.ChartboostMediationLeaderboardFragment

class ChartboostMediationLeaderboardActivity: TabActivity() {
    override fun getAdFragment() = ChartboostMediationLeaderboardFragment()

    override fun getActivityTitle() = getString(R.string.chartboost_mediation_leaderboard)

}