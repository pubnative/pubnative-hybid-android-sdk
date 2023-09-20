package net.pubnative.lite.demo.ui.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.fyber.FairBid
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.databinding.ActivityNavigationBinding
import net.pubnative.lite.demo.util.OneTrustManager

class NavigationActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST = 1000

    private lateinit var binding: ActivityNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNavigationBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        val navController = findNavController(R.id.fragment_nav_host)
        NavigationUI.setupActionBarWithNavController(this, navController)
        NavigationUI.setupWithNavController(binding.navigation, navController)

        FairBid.start(Constants.FAIRBID_APP_ID, this)

        checkPermissions()

        initializeOpenTrustSDK()
    }

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissionList = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(this, permissionList, PERMISSION_REQUEST)
        }
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

    private fun initializeOpenTrustSDK() {
        OneTrustManager.getInstance(this).initializeOpenTrustSDK()
    }

}
