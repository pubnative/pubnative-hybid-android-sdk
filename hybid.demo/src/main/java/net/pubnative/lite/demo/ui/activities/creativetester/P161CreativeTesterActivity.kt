package net.pubnative.lite.demo.ui.activities.creativetester

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.creativetester.P161CreativeTesterFragment

class P161CreativeTesterActivity : TabActivity() {

    override fun getAdFragment() = P161CreativeTesterFragment()

    override fun getActivityTitle() = getString(R.string.title_creative_tester)
}