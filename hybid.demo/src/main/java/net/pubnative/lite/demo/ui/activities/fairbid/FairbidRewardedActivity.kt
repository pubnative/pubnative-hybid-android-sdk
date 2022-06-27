package net.pubnative.lite.demo.ui.activities.fairbid

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.fairbid.FairbidMediationBannerFragment
import net.pubnative.lite.demo.ui.fragments.fairbid.FairbidRewardedFragment

class FairbidRewardedActivity : TabActivity() {
    override fun getAdFragment() = FairbidRewardedFragment()

    override fun getActivityTitle() = getString(R.string.fairbid_rewarded)

}