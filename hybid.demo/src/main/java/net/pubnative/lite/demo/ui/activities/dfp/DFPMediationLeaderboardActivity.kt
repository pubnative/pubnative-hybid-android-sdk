package net.pubnative.lite.demo.ui.activities.dfp

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.gam.GAMMediationLeaderboardFragment

class DFPMediationLeaderboardActivity  : TabActivity(){
    override fun getAdFragment() = GAMMediationLeaderboardFragment()

    override fun getActivityTitle() = getString(R.string.dfp_leaderboard)
}