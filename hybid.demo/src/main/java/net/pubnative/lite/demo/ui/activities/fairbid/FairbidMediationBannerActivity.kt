package net.pubnative.lite.demo.ui.activities.fairbid

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.fairbid.FairbidMediationBannerFragment

class FairbidMediationBannerActivity : TabActivity() {
    override fun getAdFragment() = FairbidMediationBannerFragment()

    override fun getActivityTitle() = getString(R.string.fairbid_mediation_banner)

}