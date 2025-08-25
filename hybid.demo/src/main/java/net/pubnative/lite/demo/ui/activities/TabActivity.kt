// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.activities

import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.databinding.ActivityTabBinding
import net.pubnative.lite.demo.ui.fragments.DebugFragment
import net.pubnative.lite.demo.viewmodel.DebugViewModel
import net.pubnative.lite.sdk.mraid.utils.MraidCloseAdRepo

abstract class TabActivity() : HybidDemoMainActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    lateinit var debugViewModel: DebugViewModel
    lateinit var binding: ActivityTabBinding

    private lateinit var debugFragment: DebugFragment
    private lateinit var adFragment: androidx.fragment.app.Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTabBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupEdgeToEdge()
        setupToolbar()
        setupTabs()
        setupViewPager()
        debugViewModel = ViewModelProvider(this).get(DebugViewModel::class.java)
        debugFragment = DebugFragment()
        adFragment = getAdFragment()
    }

    private fun setupTabs() {
        binding.tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(binding.container))
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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
    }

    private fun setupViewPager() {
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        binding.container.adapter = mSectionsPagerAdapter
        binding.container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabs))
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout) { view, insets ->
            val sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, sysBars.top, 0, sysBars.bottom)
            insets
        }
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

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getActivityTitle()
    }

    abstract fun getAdFragment(): androidx.fragment.app.Fragment

    abstract fun getActivityTitle(): String
}
