package net.pubnative.lite.demo.ui.activities.dfp

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.dfp.DFPLeaderboardFragment

class DFPLeaderboardActivity : TabActivity() {
    override fun getAdFragment() = DFPLeaderboardFragment()

    override fun getActivityTitle() = getString(R.string.dfp_leaderboard)
}