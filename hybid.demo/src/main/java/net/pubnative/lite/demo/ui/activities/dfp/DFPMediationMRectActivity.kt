package net.pubnative.lite.demo.ui.activities.dfp

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.gam.GAMMediationMRectFragment

class DFPMediationMRectActivity : TabActivity(){
    override fun getAdFragment() = GAMMediationMRectFragment()

    override fun getActivityTitle() = getString(R.string.dfp_medium)
}