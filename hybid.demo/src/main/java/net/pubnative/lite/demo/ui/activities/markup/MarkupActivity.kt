package net.pubnative.lite.demo.ui.activities.markup

import android.view.KeyEvent
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.markup.MarkupFragment

class MarkupActivity : TabActivity() {

    override fun getAdFragment(): Fragment {
        return MarkupFragment()
    }

    override fun getActivityTitle(): String {
        return getString(R.string.custom_markup)
    }
}