// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.fragments.config

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.config.BrowserPriorityActivity
import net.pubnative.lite.demo.ui.activities.config.KeywordsActivity
import net.pubnative.lite.demo.ui.activities.config.ZoneIdsActivity
import net.pubnative.lite.sdk.HyBid

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class HyBidSettingsFragment : Fragment(R.layout.fragment_hybid_settings) {
    private val TAG = HyBidSettingsFragment::class.java.simpleName

    private lateinit var appTokenInput: EditText
    private lateinit var ageInput: EditText
    private lateinit var apiUrlInput: EditText
    private lateinit var coppaSwitch: SwitchCompat
    private lateinit var testModeSwitch: SwitchCompat
    private lateinit var topicsApiSwitch: SwitchCompat
    private lateinit var reportingSwitch: SwitchCompat
    private lateinit var locationTrackingSwitch: SwitchCompat
    private lateinit var cbLocationTracking: CheckBox
    private lateinit var locationUpdatesSwitch: SwitchCompat
    private lateinit var cbLocationUpdates: CheckBox
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var settingManager: SettingsManager
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var precisePermissionStatus: TextView
    private lateinit var permissionStatusText: TextView
    private lateinit var tvAtomStatus: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appTokenInput = view.findViewById(R.id.input_pn_apptoken)
        ageInput = view.findViewById(R.id.input_pn_age)
        apiUrlInput = view.findViewById(R.id.input_pn_api_url)
        coppaSwitch = view.findViewById(R.id.check_coppa)
        testModeSwitch = view.findViewById(R.id.check_test_mode)
        topicsApiSwitch = view.findViewById(R.id.check_topics_api)
        reportingSwitch = view.findViewById(R.id.check_reporting)
        locationTrackingSwitch = view.findViewById(R.id.check_location_tracking)
        locationUpdatesSwitch = view.findViewById(R.id.check_location_updates)
        cbLocationTracking = view.findViewById(R.id.cb_location_tracking)
        cbLocationUpdates = view.findViewById(R.id.cb_location_updates)
        genderRadioGroup = view.findViewById(R.id.group_gender)
        precisePermissionStatus = view.findViewById(R.id.tv_precise_permission_status)
        permissionStatusText = view.findViewById(R.id.tv_geo_permission_status)
        tvAtomStatus = view.findViewById(R.id.tv_atom_status)

        settingManager = SettingsManager.getInstance(requireContext())

        view.findViewById<Button>(R.id.button_keywords).setOnClickListener {
            val intent = Intent(activity, KeywordsActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_zone_ids).setOnClickListener {
            val intent = Intent(activity, ZoneIdsActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_browser_priority).setOnClickListener {
            val intent = Intent(activity, BrowserPriorityActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_geolocation_permission).setOnClickListener {
            checkPermissions()
        }

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                Log.d(TAG, "Permission is granted")
                updateGeolocationPermissionStatus()
            } else {
                Log.d(TAG, "Permission is not granted")
                updateGeolocationPermissionStatus()
            }
        }
        view.findViewById<Button>(R.id.button_save_pn_settings).setOnClickListener {
            saveData()
            Toast.makeText(activity, "PubNative settings saved successfully.", Toast.LENGTH_SHORT)
                .show()
            HyBid.initialize(appTokenInput.text.toString(), activity?.application)
            activity?.finish()
        }
        updateGeolocationPermissionStatus()
        updateAtomStatus()
        fillSavedValues()
    }

    private fun updateAtomStatus() {
        val atomStatus = when (HyBid.isAtomStarted()) {
            true -> "started"
            else -> "stopped"
        }
        tvAtomStatus.text = "Atom is $atomStatus"
    }

    private fun fillSavedValues() {
        val settings = settingManager.getSettings().hybidSettings
        if (settings != null) {
            appTokenInput.setText(settings.appToken)
            apiUrlInput.setText(settings.apiUrl)
            ageInput.setText(settings.age)
            coppaSwitch.isChecked = settings.coppa == true
            testModeSwitch.isChecked = settings.testMode == true
            topicsApiSwitch.isChecked = settings.topicsApi == true
            reportingSwitch.isChecked = settings.reportingEnabled == true
            locationTrackingSwitch.isChecked =
                settingManager.getSettings().adCustomizationSettings?.locationTracking == true
            cbLocationTracking.isChecked =
                settingManager.getSettings().adCustomizationSettings?.locationTrackingEnabled == true
            locationUpdatesSwitch.isChecked =
                settingManager.getSettings().adCustomizationSettings?.locationUpdates == true
            cbLocationUpdates.isChecked =
                settingManager.getSettings().adCustomizationSettings?.locationUpdatesEnabled == true

            val selectedGender = when (settings.gender) {
                "male" -> R.id.radio_gender_male
                "female" -> R.id.radio_gender_female
                else -> R.id.radio_gender_not_set
            }

            genderRadioGroup.check(selectedGender)
        }
    }

    private fun saveData() {
        val appToken = appTokenInput.text.toString()
        val age = ageInput.text.toString()
        val apiUrl = apiUrlInput.text.toString()
        val coppa = coppaSwitch.isChecked
        val testMode = testModeSwitch.isChecked
        val topicsApi = topicsApiSwitch.isChecked
        val reportingEnabled = reportingSwitch.isChecked
        val locationTracking = locationTrackingSwitch.isChecked
        val locationUpdates = locationUpdatesSwitch.isChecked
        val locationTrackingEnabled = cbLocationTracking.isChecked
        val locationUpdatesEnabled = cbLocationUpdates.isChecked

        val gender = when (genderRadioGroup.checkedRadioButtonId) {
            R.id.radio_gender_male -> "male"
            R.id.radio_gender_female -> "female"
            else -> ""
        }

        settingManager.setAppToken(appToken)

        settingManager.setApiUrl(apiUrl)
        settingManager.setAge(age)
        settingManager.setCoppa(coppa)
        settingManager.setTestMode(testMode)
        settingManager.setGender(gender)
        settingManager.setTopicsApi(topicsApi)
        settingManager.setReportingEnabled(reportingEnabled)
        settingManager.setLocationTracking(locationTracking)
        settingManager.setLocationTrackingEnabled(locationTrackingEnabled)
        settingManager.setLocationUpdates(locationUpdates)
        settingManager.setLocationUpdatesEnabled(locationUpdatesEnabled)
    }

    private fun checkPermissions() {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun updateGeolocationPermissionStatus() {
        val fineLocation =
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        val coarseLocation =
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        precisePermissionStatus.visibility = View.GONE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val bgLocation = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
            val isAppLocationPermissionGranted =
                bgLocation == PackageManager.PERMISSION_GRANTED && coarseLocation == PackageManager.PERMISSION_GRANTED
            val preciseLocationAllowed =
                fineLocation == PackageManager.PERMISSION_GRANTED && coarseLocation == PackageManager.PERMISSION_GRANTED
            if (preciseLocationAllowed) {
                precisePermissionStatus.visibility = View.VISIBLE
                precisePermissionStatus.text =
                    resources.getString(R.string.precise_permission_enabled)
            } else {
                precisePermissionStatus.visibility = View.VISIBLE
                precisePermissionStatus.text =
                    resources.getString(R.string.precise_permission_disabled)
            }
            if (isAppLocationPermissionGranted) {
                permissionStatusText.text =
                    resources.getString(R.string.geolocation_allowed_all_time)
            } else if (coarseLocation == PackageManager.PERMISSION_GRANTED) {
                permissionStatusText.text =
                    resources.getString(R.string.geolocation_allowed_using_app)
            } else {
                permissionStatusText.text = resources.getString(R.string.geolocation_not_allowed)
            }

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val bgLocation = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
            val isAppLocationPermissionGranted =
                bgLocation == PackageManager.PERMISSION_GRANTED && coarseLocation == PackageManager.PERMISSION_GRANTED
            if (isAppLocationPermissionGranted) {
                permissionStatusText.text =
                    resources.getString(R.string.geolocation_allowed_all_time)
            } else if (coarseLocation == PackageManager.PERMISSION_GRANTED) {
                permissionStatusText.text =
                    resources.getString(R.string.geolocation_allowed_using_app)
            } else {
                permissionStatusText.text = resources.getString(R.string.geolocation_not_allowed)
            }
        } else {
            val isAppLocationPermissionGranted =
                fineLocation == PackageManager.PERMISSION_GRANTED && coarseLocation == PackageManager.PERMISSION_GRANTED
            if (isAppLocationPermissionGranted) {
                permissionStatusText.text =
                    resources.getString(R.string.geolocation_permission_enabled)
            } else {
                permissionStatusText.text =
                    resources.getString(R.string.geolocation_permission_disabled)
            }
        }
    }
}