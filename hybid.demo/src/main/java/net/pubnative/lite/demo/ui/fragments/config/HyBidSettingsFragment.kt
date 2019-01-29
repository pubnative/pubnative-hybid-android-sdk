// The MIT License (MIT)
//
// Copyright (c) 2018 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package net.pubnative.lite.demo.ui.fragments.config

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.config.KeywordsActivity
import net.pubnative.lite.demo.ui.activities.config.ZoneIdsActivity
import net.pubnative.lite.sdk.HyBid

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class HyBidSettingsFragment : Fragment() {
    private val REQUEST_KEYWORDS = 1
    private val REQUEST_ZONE_IDS = 2

    private lateinit var appTokenInput: EditText
    private lateinit var ageInput: EditText
    private lateinit var coppaSwitch: Switch
    private lateinit var testModeSwitch: Switch
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var settingManager: SettingsManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_hybid_settings, container, false)

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

        HyBid.setAppToken(appToken)
        HyBid.setAge(age)
        HyBid.setCoppaEnabled(coppa)
        HyBid.setTestMode(testMode)
        HyBid.setGender(gender)
    }
}