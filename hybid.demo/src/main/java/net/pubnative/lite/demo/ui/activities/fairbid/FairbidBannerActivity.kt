package net.pubnative.lite.demo.ui.activities.fairbid

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.fairbid.FairbidBannerFragment

class FairbidBannerActivity : TabActivity() {
    override fun getAdFragment() = FairbidBannerFragment()

    override fun getActivityTitle() = getString(R.string.fairbid_banner)

}