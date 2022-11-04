package net.pubnative.lite.demo.ui.activities.apitester

import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.apitester.LegacyApiTesterFragment
import net.pubnative.lite.demo.ui.fragments.vast.VastTagRequestFragment

class LegacyApiTesterActivity : TabActivity() {

    override fun getAdFragment() = LegacyApiTesterFragment()

    override fun getActivityTitle() = getString(R.string.title_legacy_api)
}