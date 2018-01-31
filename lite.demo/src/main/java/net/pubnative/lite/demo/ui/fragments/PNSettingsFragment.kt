package net.pubnative.lite.demo.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.KeywordsActivity
import net.pubnative.lite.demo.ui.activities.ZoneIdsActivity

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class PNSettingsFragment : Fragment() {
    private val REQUEST_KEYWORDS = 1
    private val REQUEST_ZONE_IDS = 2

    private lateinit var appTokenInput: EditText
    private lateinit var ageInput: EditText
    private lateinit var coppaSwitch: Switch
    private lateinit var testModeSwitch: Switch
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var settingManager: SettingsManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_pn_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appTokenInput = view.findViewById(R.id.input_pn_apptoken)
        ageInput = view.findViewById(R.id.input_pn_age)
        coppaSwitch = view.findViewById(R.id.check_coppa)
        testModeSwitch = view.findViewById(R.id.check_test_mode)
        genderRadioGroup = view.findViewById(R.id.group_gender)

        settingManager = SettingsManager.getInstance(context!!)

        view.findViewById<Button>(R.id.button_keywords).setOnClickListener {
            val intent = Intent(activity, KeywordsActivity::class.java)
            startActivityForResult(intent, REQUEST_KEYWORDS)
        }

        view.findViewById<Button>(R.id.button_zone_ids).setOnClickListener {
            val intent = Intent(activity, ZoneIdsActivity::class.java)
            startActivityForResult(intent, REQUEST_ZONE_IDS)
        }

        view.findViewById<Button>(R.id.button_save_pn_settings).setOnClickListener {
            saveData()
            Toast.makeText(activity, "PubNative settings saved successfully.", Toast.LENGTH_SHORT).show()
            activity?.finish()
        }

        fillSavedValues()
    }

    private fun fillSavedValues() {
        val settings = settingManager.getSettings()
        appTokenInput.setText(settings.appToken)
        ageInput.setText(settings.age)
        coppaSwitch.isChecked = settings.coppa
        testModeSwitch.isChecked = settings.testMode

        val selectedGender = when (settings.gender) {
            "male" -> R.id.radio_gender_male
            "female" -> R.id.radio_gender_female
            else -> R.id.radio_gender_not_set
        }

        genderRadioGroup.check(selectedGender)
    }

    private fun saveData() {
        val appToken = appTokenInput.text.toString()
        val age = ageInput.text.toString()
        val coppa = coppaSwitch.isChecked
        val testMode = testModeSwitch.isChecked

        val gender = when (genderRadioGroup.checkedRadioButtonId) {
            R.id.radio_gender_male -> "male"
            R.id.radio_gender_female -> "female"
            else -> ""
        }

        settingManager.setAppToken(appToken)
        settingManager.setAge(age)
        settingManager.setCoppa(coppa)
        settingManager.setTestMode(testMode)
        settingManager.setGender(gender)
    }
}