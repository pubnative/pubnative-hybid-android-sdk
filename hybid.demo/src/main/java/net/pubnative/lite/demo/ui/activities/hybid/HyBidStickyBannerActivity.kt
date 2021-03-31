package net.pubnative.lite.demo.ui.activities.hybid

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.hybid.HyBidStickyBannerFragment

class HyBidStickyBannerActivity : TabActivity() {
    override fun getAdFragment() = HyBidStickyBannerFragment()

    override fun getActivityTitle() = getString(R.string.sticky_banner)
}