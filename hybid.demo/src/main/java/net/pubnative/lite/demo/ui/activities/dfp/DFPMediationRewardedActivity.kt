package net.pubnative.lite.demo.ui.activities.dfp

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.dfp.DFPMediationRewardedFragment

class DFPMediationRewardedActivity : TabActivity(){
    override fun getAdFragment() = DFPMediationRewardedFragment()

    override fun getActivityTitle() = getString(R.string.dfp_rewarded)
}