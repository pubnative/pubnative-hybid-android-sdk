package net.pubnative.lite.demo.ui.activities.maxads

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.maxads.MaxAdsMediationBannerFragment

class MaxAdsMediationBannerActivity : TabActivity() {
    override fun getAdFragment() = MaxAdsMediationBannerFragment()

    override fun getActivityTitle() = getString(R.string.maxads_banner)
}