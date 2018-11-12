package net.pubnative.lite.demo.ui.activities.mopub

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.mopub.MoPubLeaderboardFragment

class MoPubLeaderboardActivity : TabActivity() {
    override fun getAdFragment() = MoPubLeaderboardFragment()

    override fun getActivityTitle() = getString(R.string.mopub_leaderboard)
}