// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.activities.admob

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.admob.AdmobMediationMRectVideoFragment

class AdmobMediationMRectVideoActivity : TabActivity() {
    override fun getAdFragment() = AdmobMediationMRectVideoFragment()

    override fun getActivityTitle() = getString(R.string.admob_medium_video)
}