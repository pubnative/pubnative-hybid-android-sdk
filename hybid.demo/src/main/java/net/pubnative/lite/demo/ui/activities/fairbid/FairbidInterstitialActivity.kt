package net.pubnative.lite.demo.ui.activities.fairbid

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.fairbid.FairbidInterstitialFragment

class FairbidInterstitialActivity : TabActivity() {
    override fun getAdFragment() = FairbidInterstitialFragment()

    override fun getActivityTitle() = getString(R.string.fairbid_interstitial)

}