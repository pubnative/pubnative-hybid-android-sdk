package net.pubnative.lite.demo.ui.activities.mopub

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.mopub.MoPubMediationInterstitialFragment

class MoPubMediationInterstitialActivity : TabActivity() {
    override fun getAdFragment() = MoPubMediationInterstitialFragment()

    override fun getActivityTitle() = getString(R.string.mopub_interstitial)
}