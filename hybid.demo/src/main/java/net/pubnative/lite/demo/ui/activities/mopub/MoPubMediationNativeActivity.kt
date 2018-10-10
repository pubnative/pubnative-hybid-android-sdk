package net.pubnative.lite.demo.ui.activities.mopub

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.mopub.MoPubMediationNativeFragment

class MoPubMediationNativeActivity: TabActivity() {
    override fun getAdFragment() = MoPubMediationNativeFragment()

    override fun getActivityTitle() = getString(R.string.mopub_native)
}