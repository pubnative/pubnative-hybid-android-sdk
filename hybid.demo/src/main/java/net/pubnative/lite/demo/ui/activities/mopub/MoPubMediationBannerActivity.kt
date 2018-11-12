package net.pubnative.lite.demo.ui.activities.mopub

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.mopub.MoPubMediationBannerFragment

class MoPubMediationBannerActivity : TabActivity() {
    override fun getAdFragment() = MoPubMediationBannerFragment()

    override fun getActivityTitle() = getString(R.string.mopub_banner)
}