package net.pubnative.lite.demo.ui.activities.chartboost

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.chartboost.ChartboostMediationRewardedHtmlFragment

class ChartboostMediationRewardedHtmlActivity : TabActivity() {
    override fun getAdFragment() = ChartboostMediationRewardedHtmlFragment()

    override fun getActivityTitle() = getString(R.string.chartboost_mediation_rewarded_html)

}