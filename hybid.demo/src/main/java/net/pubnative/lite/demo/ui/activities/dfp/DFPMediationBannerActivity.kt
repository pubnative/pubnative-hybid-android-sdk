package net.pubnative.lite.demo.ui.activities.dfp

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.gam.GAMMediationBannerFragment

class DFPMediationBannerActivity : TabActivity(){
    override fun getAdFragment() = GAMMediationBannerFragment()

    override fun getActivityTitle() = getString(R.string.dfp_banner)
}