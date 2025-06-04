// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.activities.maxads

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.maxads.MaxAdsMediationInterstitialFragment

class MaxAdsMediationInterstitialActivity : TabActivity() {
    override fun getAdFragment() = MaxAdsMediationInterstitialFragment()

    override fun getActivityTitle() = getString(R.string.maxads_interstitial)
}