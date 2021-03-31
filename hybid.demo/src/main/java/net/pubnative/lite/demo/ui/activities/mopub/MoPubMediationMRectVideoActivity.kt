package net.pubnative.lite.demo.ui.activities.mopub

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.mopub.MoPubMediationMRectVideoFragment

class MoPubMediationMRectVideoActivity : TabActivity() {
    override fun getAdFragment() = MoPubMediationMRectVideoFragment()

    override fun getActivityTitle() = getString(R.string.mopub_medium_video)
}