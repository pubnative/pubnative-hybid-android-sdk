// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.activities.ironsource

import com.ironsource.mediationsdk.IronSource
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.ironsource.IronSourceMediationRewardedFragment

class IronSourceMediationRewardedActivity : TabActivity() {
    override fun getAdFragment() = IronSourceMediationRewardedFragment()

    override fun getActivityTitle() = getString(R.string.ironsource_rewarded)

    override fun onResume() {
        super.onResume()
        IronSource.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        IronSource.onPause(this)
    }
}