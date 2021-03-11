package net.pubnative.lite.demo.ui.activities.unity

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.unity.StickyBannerFragment

class StickyTopBottomBannerActivity : TabActivity() {
    override fun getAdFragment() = StickyBannerFragment()

    override fun getActivityTitle() = getString(R.string.sticky_banner)
}