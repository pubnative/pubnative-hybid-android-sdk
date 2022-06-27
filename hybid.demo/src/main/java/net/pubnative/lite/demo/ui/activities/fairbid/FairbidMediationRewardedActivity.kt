package net.pubnative.lite.demo.ui.activities.fairbid

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.fairbid.FairbidMediationRewardedFragment

class FairbidMediationRewardedActivity : TabActivity() {
    override fun getAdFragment() = FairbidMediationRewardedFragment()

    override fun getActivityTitle() = getString(R.string.fairbid_mediation_rewarded)

}