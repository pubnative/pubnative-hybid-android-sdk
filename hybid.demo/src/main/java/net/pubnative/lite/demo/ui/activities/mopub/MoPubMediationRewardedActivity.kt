package net.pubnative.lite.demo.ui.activities.mopub

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.mopub.MoPubMediationRewardedFragment

class MoPubMediationRewardedActivity : TabActivity() {
    override fun getAdFragment() = MoPubMediationRewardedFragment()

    override fun getActivityTitle() = getString(R.string.mopub_rewarded)
}