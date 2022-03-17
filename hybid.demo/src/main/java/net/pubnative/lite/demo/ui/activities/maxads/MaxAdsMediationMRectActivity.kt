package net.pubnative.lite.demo.ui.activities.maxads

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.maxads.MaxAdsMediationMRectFragment

class MaxAdsMediationMRectActivity : TabActivity() {
    override fun getAdFragment() = MaxAdsMediationMRectFragment()

    override fun getActivityTitle() = getString(R.string.maxads_mrect)
}