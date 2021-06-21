package net.pubnative.lite.demo.ui.activities.mopub

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.mopub.MoPubMediationNativeRecyclerViewFragment

class MoPubMediationNativeRecyclerViewActivity : TabActivity() {
    override fun getAdFragment() = MoPubMediationNativeRecyclerViewFragment()

    override fun getActivityTitle() = getString(R.string.mopub_native_recycler)
}