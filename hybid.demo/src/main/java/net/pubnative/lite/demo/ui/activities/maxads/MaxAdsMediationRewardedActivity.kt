// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.activities.maxads

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.maxads.MaxAdsMediationRewardedFragment

class MaxAdsMediationRewardedActivity : TabActivity() {
    override fun getAdFragment() = MaxAdsMediationRewardedFragment()

    override fun getActivityTitle() = getString(R.string.maxads_rewarded)
}