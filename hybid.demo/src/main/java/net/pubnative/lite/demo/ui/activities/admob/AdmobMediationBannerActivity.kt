// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.activities.admob

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.admob.AdmobMediationBannerFragment

class AdmobMediationBannerActivity : TabActivity() {
    override fun getAdFragment() = AdmobMediationBannerFragment()

    override fun getActivityTitle() = getString(R.string.admob_banner)
}