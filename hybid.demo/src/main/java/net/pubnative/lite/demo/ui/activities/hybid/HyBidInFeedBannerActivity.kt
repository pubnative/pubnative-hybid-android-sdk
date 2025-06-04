// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.activities.hybid

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.hybid.HyBidInFeedFragment

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class HyBidInFeedBannerActivity : TabActivity() {
    override fun getAdFragment() = HyBidInFeedFragment()

    override fun getActivityTitle() = getString(R.string.hybid_banner)
}