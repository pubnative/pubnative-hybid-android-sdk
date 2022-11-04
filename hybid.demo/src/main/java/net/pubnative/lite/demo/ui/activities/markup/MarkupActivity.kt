package net.pubnative.lite.demo.ui.activities.markup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.markup.MarkupFragment

class MarkupActivity : TabActivity() {

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_markup)
//        val toolbar = findViewById<Toolbar>(R.id.toolbar)
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//    }
//
//    override fun onSupportNavigateUp(): Boolean {
//        onBackPressed()
//        return true
//    }

    override fun getAdFragment(): Fragment {
        return MarkupFragment()
    }

    override fun getActivityTitle(): String {
        return getString(R.string.custom_markup)
    }
}