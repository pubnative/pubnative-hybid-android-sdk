package net.pubnative.lite.demo.ui.activities.mopub

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.mopub.MoPubMediationMRectFragment

class MoPubMediationMRectActivity : TabActivity() {
    override fun getAdFragment() = MoPubMediationMRectFragment()

    override fun getActivityTitle() = getString(R.string.mopub_medium)
}