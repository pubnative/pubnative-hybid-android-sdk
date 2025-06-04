// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.activities.chartboost

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.chartboost.ChartboostMediationInterstitialFragment

class ChartboostMediationInterstitialActivity: TabActivity() {
    override fun getAdFragment() = ChartboostMediationInterstitialFragment()

    override fun getActivityTitle() = getString(R.string.chartboost_mediation_interstitial)

}