package net.pubnative.lite.demo.ui.activities.hybid

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.hybid.HyBidLeaderboardFragment

class HyBidLeaderboardActivity : TabActivity() {
        override fun getAdFragment() = HyBidLeaderboardFragment()

        override fun getActivityTitle() = getString(R.string.hybid_leaderboard)
}