// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.activities.hybid

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.hybid.HyBidRewardedFragment

class HyBidRewardedActivity : TabActivity() {
    override fun getAdFragment() = HyBidRewardedFragment()

    override fun getActivityTitle() = getString(R.string.hybid_rewarded)
}