package net.pubnative.lite.demo.ui.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_navigation.*
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.fragments.navigation.*

class NavigationActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST = 1000

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

        checkPermissions()

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

    fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            val permissionList = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(this, permissionList, PERMISSION_REQUEST)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST -> {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Location permission denied. You can change this on the app settings.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}
