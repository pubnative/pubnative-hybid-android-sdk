package net.pubnative.lite.demo.ui.activities.mopub

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.mopub.MoPubMediationInterstitialVideoFragment

class MoPubMediationInterstitialVideoActivity: TabActivity() {
    override fun getAdFragment() = MoPubMediationInterstitialVideoFragment()

    override fun getActivityTitle() = getString(R.string.mopub_interstitial_video)
}