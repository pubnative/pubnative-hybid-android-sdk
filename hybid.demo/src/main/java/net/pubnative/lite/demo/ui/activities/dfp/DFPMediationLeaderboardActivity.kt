package net.pubnative.lite.demo.ui.activities.dfp

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.dfp.DFPMediationLeaderboardFragment

class DFPMediationLeaderboardActivity  : TabActivity(){
    override fun getAdFragment() = DFPMediationLeaderboardFragment()

    override fun getActivityTitle() = getString(R.string.dfp_leaderboard)
}