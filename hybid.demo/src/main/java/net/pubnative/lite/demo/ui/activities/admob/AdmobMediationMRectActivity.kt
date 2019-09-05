package net.pubnative.lite.demo.ui.activities.admob

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.admob.AdmobMediationBannerFragment
import net.pubnative.lite.demo.ui.fragments.admob.AdmobMediationMRectFragment

class AdmobMediationMRectActivity : TabActivity() {
    override fun getAdFragment() = AdmobMediationMRectFragment()

    override fun getActivityTitle() = getString(R.string.admob_medium)
}