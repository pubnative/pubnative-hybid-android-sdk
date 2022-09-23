package net.pubnative.lite.demo.ui.activities.vast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.creativetester.CreativeTesterFragment
import net.pubnative.lite.demo.ui.fragments.vast.VastTagRequestFragment

class VastTagRequestActivity : TabActivity() {

    override fun getAdFragment() = VastTagRequestFragment()

    override fun getActivityTitle() = getString(R.string.title_vast_tag)
}