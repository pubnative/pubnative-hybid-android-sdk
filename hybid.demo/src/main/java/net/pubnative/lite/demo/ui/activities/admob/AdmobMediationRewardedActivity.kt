package net.pubnative.lite.demo.ui.activities.admob

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.admob.AdmobMediationRewardedFragment

class AdmobMediationRewardedActivity : TabActivity() {
    override fun getAdFragment() = AdmobMediationRewardedFragment()

    override fun getActivityTitle() = getString(R.string.admob_rewarded)
}