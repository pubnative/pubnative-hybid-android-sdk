// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.fragments.consent

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.R
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.api.ApiManager
import net.pubnative.lite.sdk.utils.AtomManager

class ConsentStringsFragment : Fragment() {
    companion object {
        private const val KEY_CCPA_PUBLIC_CONSENT = "IABUSPrivacy_String"
        private const val KEY_GDPR_PUBLIC_CONSENT = "IABTCF_TCString"
    }

    private lateinit var gdprConsentInput: EditText
    private lateinit var ccpaConsentInput: EditText
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_consent_strings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gdprConsentInput = view.findViewById(R.id.input_gdpr_consent)
        ccpaConsentInput = view.findViewById(R.id.input_ccpa_consent)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext().applicationContext)

        view.findViewById<Button>(R.id.button_set_gdpr_consent).setOnClickListener {
            val gdprConsentString = gdprConsentInput.text.toString()

            if (TextUtils.isEmpty(gdprConsentString.trim())) {
                Toast.makeText(requireContext(), R.string.empty_gdpr_consent_field, Toast.LENGTH_SHORT).show()
            } else {
                sharedPreferences.edit().putString(KEY_GDPR_PUBLIC_CONSENT, gdprConsentString).apply()
                AtomManager.stopAtom()
                ApiManager.fetchConfigs()
                Toast.makeText(requireContext(), R.string.updated_gdpr_consent, Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<Button>(R.id.button_remove_gdpr_consent).setOnClickListener {
            sharedPreferences.edit().remove(KEY_GDPR_PUBLIC_CONSENT).apply()
            gdprConsentInput.setText("")
            ApiManager.fetchConfigs()
            Toast.makeText(requireContext(), R.string.removed_gdpr_consent, Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.button_set_ccpa_consent).setOnClickListener {
            val ccpaConsentString = ccpaConsentInput.text.toString()

            if (TextUtils.isEmpty(ccpaConsentString.trim())) {
                Toast.makeText(requireContext(), R.string.empty_ccpa_consent_field, Toast.LENGTH_SHORT).show()
            } else {
                sharedPreferences.edit().putString(KEY_CCPA_PUBLIC_CONSENT, ccpaConsentString).apply()
                Toast.makeText(requireContext(), R.string.updated_ccpa_consent, Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<Button>(R.id.button_remove_ccpa_consent).setOnClickListener {
            sharedPreferences.edit().remove(KEY_CCPA_PUBLIC_CONSENT).apply()
            ccpaConsentInput.setText("")
            Toast.makeText(requireContext(), R.string.removed_ccpa_consent, Toast.LENGTH_SHORT).show()
        }

        setupStrings()
    }

    private fun setupStrings() {
        if (HyBid.getUserDataManager() != null) {
            val gdprConsentString = HyBid.getUserDataManager().iabgdprConsentString
            if (!TextUtils.isEmpty(gdprConsentString)) {
                gdprConsentInput.setText(gdprConsentString)
            }

            val ccpaConsentString = HyBid.getUserDataManager().iabusPrivacyString
            if (!TextUtils.isEmpty(ccpaConsentString)) {
                ccpaConsentInput.setText(ccpaConsentString)
            }
        }
    }
}