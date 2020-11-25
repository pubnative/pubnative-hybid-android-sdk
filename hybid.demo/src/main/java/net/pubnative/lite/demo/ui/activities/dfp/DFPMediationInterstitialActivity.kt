package net.pubnative.lite.demo.ui.activities.dfp

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.dfp.DFPMediationInterstitialFragment

class DFPMediationInterstitialActivity : TabActivity(){
    override fun getAdFragment() = DFPMediationInterstitialFragment()

    override fun getActivityTitle() = getString(R.string.dfp_interstitial)
}