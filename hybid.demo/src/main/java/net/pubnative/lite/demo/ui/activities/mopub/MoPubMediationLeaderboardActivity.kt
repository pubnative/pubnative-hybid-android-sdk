package net.pubnative.lite.demo.ui.activities.mopub

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.mopub.MoPubMediationLeaderboardFragment

class MoPubMediationLeaderboardActivity : TabActivity() {
    override fun getAdFragment() = MoPubMediationLeaderboardFragment()

    override fun getActivityTitle() = getString(R.string.mopub_leaderboard)
}