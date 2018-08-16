package net.pubnative.lite.demo

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_navigation.*
import net.pubnative.lite.demo.ui.fragments.*

class NavigationActivity : AppCompatActivity() {

    private var currentSelectedNav = -1

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_standalone -> {
                navigateToStandalone()
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_pre_bid -> {
                navigateToPrebid()
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_mediation -> {
                navigateToMediation()
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_consent -> {
                navigateToConsent()
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_settings -> {
                navigateToSettings()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        navigateToStandalone()
    }

    private fun navigateToStandalone() {
        if (currentSelectedNav != R.id.nav_standalone) {
            currentSelectedNav = R.id.nav_standalone
            setTitle(R.string.nav_standalone)
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, StandaloneNavFragment()).commit()
        }
    }

    private fun navigateToPrebid() {
        if (currentSelectedNav != R.id.nav_pre_bid) {
            currentSelectedNav = R.id.nav_pre_bid
            setTitle(R.string.nav_pre_bid)
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, PrebidNavFragment()).commit()
        }
    }

    private fun navigateToMediation() {
        if (currentSelectedNav != R.id.nav_mediation) {
            currentSelectedNav = R.id.nav_mediation
            setTitle(R.string.nav_mediation)
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MediationNavFragment()).commit()
        }
    }

    private fun navigateToConsent() {
        if (currentSelectedNav != R.id.nav_consent) {
            currentSelectedNav = R.id.nav_consent
            setTitle(R.string.nav_consent)
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ConsentNavFragment()).commit()
        }
    }

    private fun navigateToSettings() {
        if (currentSelectedNav != R.id.nav_settings) {
            currentSelectedNav = R.id.nav_settings
            setTitle(R.string.nav_settings)
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SettingsNavFragment()).commit()
        }
    }
}
