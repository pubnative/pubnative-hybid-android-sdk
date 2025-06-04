// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.activities.maxads

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.maxads.*

class MaxAdsMediationNativeActivity : TabActivity() {
    override fun getAdFragment() = MaxAdsMediationNativeFragment()

    override fun getActivityTitle() = getString(R.string.maxads_native)
}