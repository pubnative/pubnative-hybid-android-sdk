package net.pubnative.lite.demo.ui.activities.admob

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.admob.AdmobMediationBannerFragment
import net.pubnative.lite.demo.ui.fragments.admob.AdmobMediationInterstitialFragment
import net.pubnative.lite.demo.ui.fragments.admob.AdmobMediationLeaderboardFragment

class AdmobMediationInterstitialActivity : TabActivity() {
    override fun getAdFragment() = AdmobMediationInterstitialFragment()

    override fun getActivityTitle() = getString(R.string.admob_interstitial)
}