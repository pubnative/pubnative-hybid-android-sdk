// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.activities.creativetester

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.creativetester.CreativeTesterFragment

class P161CreativeTesterActivity : TabActivity() {

    override fun getAdFragment() = CreativeTesterFragment()

    override fun getActivityTitle() = getString(R.string.title_creative_tester)
}