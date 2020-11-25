package net.pubnative.lite.demo.ui.activities.dfp

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.dfp.DFPMediationMRectFragment

class DFPMediationMRectActivity : TabActivity(){
    override fun getAdFragment() = DFPMediationMRectFragment()

    override fun getActivityTitle() = getString(R.string.dfp_medium)
}