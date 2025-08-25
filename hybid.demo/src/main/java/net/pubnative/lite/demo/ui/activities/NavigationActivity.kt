// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.activities

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.databinding.ActivityNavigationBinding
import net.pubnative.lite.demo.ui.dialogs.SDKConfigDialog
import net.pubnative.lite.demo.ui.dialogs.SDKConfigDialogManager
import net.pubnative.lite.demo.util.OneTrustManager
import net.pubnative.lite.sdk.api.ApiManager


class NavigationActivity : HybidDemoMainActivity(), SDKConfigDialog.OnDismissListener {

    private val PERMISSION_REQUEST = 1000

    private lateinit var binding: ActivityNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.appBarLayout.setPadding(0, systemBars.top, 0, 0)
            binding.navigation.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }
        setupToolbar()
        setupNavigationController()
        initializeOpenTrustSDK()
        showSDKConfigDialog()
    }

    private fun setupNavigationController() {
        val navController = findNavController(R.id.fragment_nav_host)
        NavigationUI.setupActionBarWithNavController(this, navController)
        NavigationUI.setupWithNavController(binding.navigation, navController)
    }

    private fun showSDKConfigDialog() {
        val instance = SDKConfigDialogManager.getInstance()
        instance?.showDialog(this, supportFragmentManager)
    }

    private fun initializeOpenTrustSDK() {
        OneTrustManager.getInstance(this).initializeOpenTrustSDK()
    }

    override fun onDismiss(url: String?) {
        ApiManager.setSDKConfigURL(url)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp() = findNavController(R.id.fragment_nav_host).navigateUp()

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this,
                        "Location permission denied. You can change this on the app settings.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}