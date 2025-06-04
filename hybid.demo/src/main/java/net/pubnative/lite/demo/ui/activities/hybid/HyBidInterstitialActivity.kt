// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.activities.hybid

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.hybid.HyBidInterstitialFragment

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class HyBidInterstitialActivity : TabActivity() {
    override fun getAdFragment() = HyBidInterstitialFragment()

    override fun getActivityTitle() = getString(R.string.hybid_interstitial)
}