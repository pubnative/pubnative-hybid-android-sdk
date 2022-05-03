package net.pubnative.lite.demo.ui.activities.fairbid

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.fairbid.FairbidMediationInterstitialFragment

class FairbidMediationInterstitialActivity : TabActivity() {
    override fun getAdFragment() = FairbidMediationInterstitialFragment()

    override fun getActivityTitle() = getString(R.string.fairbid_interstitial)

}