package net.pubnative.lite.demo.ui.activities.chartboost

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.chartboost.ChartboostMediationMrectFragment

class ChartboostMediationMRectActivity : TabActivity() {
    override fun getAdFragment() = ChartboostMediationMrectFragment()

    override fun getActivityTitle() = getString(R.string.chartboost_mediation_mrect)

}