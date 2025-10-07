// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.activities.ironsource

import com.ironsource.mediationsdk.IronSource
import com.unity3d.mediation.LevelPlay
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.ironsource.IronSourceMediationBannerFragment

class IronSourceMediationBannerActivity : TabActivity() {

    override fun getAdFragment() = IronSourceMediationBannerFragment()

    override fun getActivityTitle() = getString(R.string.ironsource_banner)
}