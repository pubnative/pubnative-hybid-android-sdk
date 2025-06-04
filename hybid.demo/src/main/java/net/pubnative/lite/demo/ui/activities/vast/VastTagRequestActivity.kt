// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.activities.vast

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.vast.VastTagRequestFragment

class VastTagRequestActivity : TabActivity() {

    override fun getAdFragment() = VastTagRequestFragment()

    override fun getActivityTitle() = getString(R.string.title_vast_tag)
}