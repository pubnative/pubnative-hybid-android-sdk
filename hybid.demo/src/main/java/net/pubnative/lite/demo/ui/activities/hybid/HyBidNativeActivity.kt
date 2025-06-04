// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.activities.hybid

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.hybid.HyBidNativeFragment

class HyBidNativeActivity : TabActivity() {
    override fun getAdFragment() = HyBidNativeFragment()

    override fun getActivityTitle() = getString(R.string.hybid_native)
}