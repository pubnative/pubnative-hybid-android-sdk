package net.pubnative.lite.demo.ui.activities.ironsource

import com.ironsource.mediationsdk.IronSource
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.ironsource.IronSourceMediationBannerFragment

class IronSourceMediationBannerActivity : TabActivity() {

    override fun getAdFragment() = IronSourceMediationBannerFragment()

    override fun getActivityTitle() = getString(R.string.ironsource_banner)

    override fun onResume() {
        super.onResume()
        IronSource.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        IronSource.onPause(this)
    }
}