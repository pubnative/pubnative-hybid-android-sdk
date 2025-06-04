// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//

package net.pubnative.lite.demo.ui.activities

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.fragments.DebugFragment
import net.pubnative.lite.demo.viewmodel.DebugViewModel
import net.pubnative.lite.sdk.mraid.utils.MraidCloseAdRepo

abstract class TabActivity : AppCompatActivity() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    lateinit var debugViewModel: DebugViewModel

    private lateinit var container: androidx.viewpager.widget.ViewPager
    private lateinit var tabs: TabLayout

    private lateinit var debugFragment: DebugFragment
    private lateinit var adFragment: androidx.fragment.app.Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        supportActionBar?.title = getActivityTitle()

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container = findViewById(R.id.container)
        tabs = findViewById(R.id.tabs)

        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                MraidCloseAdRepo.getInstance().notifyTabChanged()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Tab unselected
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Tab reselected
            }
        })
        debugViewModel = ViewModelProvider(this).get(DebugViewModel::class.java)

        debugFragment = DebugFragment()
        adFragment = getAdFragment()
    }

    override fun onSupportNavigateUp(): Boolean {
        onKeyDown(KeyEvent.KEYCODE_BACK, null)
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }

    inner class SectionsPagerAdapter(fm: androidx.fragment.app.FragmentManager) :
        FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): androidx.fragment.app.Fragment {
            val fragment = when (position) {
                0 -> adFragment
                else -> debugFragment
            }
            return fragment
        }

        override fun getCount(): Int {
            return 2
        }
    }

    fun notifyAdCleaned() {
        debugViewModel.clearLogs()
    }

    fun clearEventList() {
        debugViewModel.clearEventList()
    }

    fun clearTrackerList() {
        debugViewModel.clearTrackerList()
    }

    fun clearRequestUrlString() {
        debugViewModel.clearRequestUri()
    }

    fun cacheEventList() {
        debugViewModel.cacheEventList()
    }

    fun notifyAdUpdated() {
        debugViewModel.updateLogs()
    }

    abstract fun getAdFragment(): androidx.fragment.app.Fragment

    abstract fun getActivityTitle(): String
}
