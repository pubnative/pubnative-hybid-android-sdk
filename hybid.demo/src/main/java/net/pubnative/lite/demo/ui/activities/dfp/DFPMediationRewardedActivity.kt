package net.pubnative.lite.demo.ui.activities.dfp

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.gam.GAMMediationRewardedFragment

class DFPMediationRewardedActivity : TabActivity(){
    override fun getAdFragment() = GAMMediationRewardedFragment()

    override fun getActivityTitle() = getString(R.string.dfp_rewarded)
}