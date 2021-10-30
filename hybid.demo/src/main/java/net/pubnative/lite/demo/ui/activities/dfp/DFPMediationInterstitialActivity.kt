package net.pubnative.lite.demo.ui.activities.dfp

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.gam.GAMMediationInterstitialFragment

class DFPMediationInterstitialActivity : TabActivity(){
    override fun getAdFragment() = GAMMediationInterstitialFragment()

    override fun getActivityTitle() = getString(R.string.dfp_interstitial)
}