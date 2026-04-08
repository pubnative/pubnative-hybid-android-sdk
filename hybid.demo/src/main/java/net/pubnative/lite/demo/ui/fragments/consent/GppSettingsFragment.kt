// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.fragments.consent

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import net.pubnative.lite.demo.databinding.FragmentGppSettingsBinding
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.UserDataManager

class GppSettingsFragment : Fragment() {
    private val TAG = GppSettingsFragment::class.java.simpleName

    private var userDataManager: UserDataManager? = null

    private var _binding: FragmentGppSettingsBinding? = null

    private val KEY_PUBLIC_GPP_STRING = "IABGPP_HDR_GppString"
    private val KEY_PUBLIC_GPP_ID = "IABGPP_GppSID"
    private val KEY_GPP_STRING = "gpp_string"
    private val KEY_GPP_ID = "gpp_id"
    private val PREFERENCES_CONSENT = "net.pubnative.lite.dataconsent"

    private var appPreferences: SharedPreferences? = null
    private var internalPreferences: SharedPreferences? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGppSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userDataManager = HyBid.getUserDataManager()

        internalPreferences =
            context?.getSharedPreferences(PREFERENCES_CONSENT, Context.MODE_PRIVATE)
        internalPreferences?.registerOnSharedPreferenceChangeListener(internalPrefsListener)

        appPreferences =
            context?.let { PreferenceManager.getDefaultSharedPreferences(it.applicationContext) }
        appPreferences?.registerOnSharedPreferenceChangeListener(appPrefsListener)

        _binding?.buttonSetGppString?.setOnClickListener { setGppString() }
        _binding?.buttonSetGppsid?.setOnClickListener { setGppSid() }
        _binding?.buttonDeleteGppString?.setOnClickListener { deleteGppString() }
        _binding?.buttonDeleteGppsid?.setOnClickListener { deleteGppSid() }
        _binding?.buttonDeleteGppData?.setOnClickListener { deleteGppData() }

        _binding?.viewInternalGppString?.text = userDataManager?.gppString
        _binding?.viewInternalGppsidString?.text = userDataManager?.gppSid
        _binding?.viewPublicGppString?.text = getPublicGppString(appPreferences)
        _binding?.viewPublicGppsidString?.text = getPublicGppId(appPreferences)
    }

    private fun setGppString() {
        if (binding.inputGppString.text.toString().isNotEmpty()) {
            userDataManager?.gppString = binding.inputGppString.text.toString()
        }
    }
    private fun setGppSid() {
        if (binding.inputGppsid.text.toString().isNotEmpty()) {
            userDataManager?.gppSid = binding.inputGppsid.text.toString()
        }
    }
    private fun deleteGppString() {
        userDataManager?.removeGppString()
    }
    private fun deleteGppSid() {
        userDataManager?.removeGppSid()
    }

    private fun deleteGppData() {
        userDataManager?.removeGppData()
    }

    //------------------------------------------- GPP ---------------------------------------------
    private fun getPublicGppString(sharedPreferences: SharedPreferences?): String? {
        return sharedPreferences?.getString(KEY_PUBLIC_GPP_STRING, null)
    }

    private fun getPublicGppId(sharedPreferences: SharedPreferences?): String? {
        return sharedPreferences?.getString(KEY_PUBLIC_GPP_ID, null)
    }

    private fun setPublicGppString(text: String) {
        appPreferences?.edit()?.putString(KEY_PUBLIC_GPP_STRING, text)?.apply()
    }

    private fun setPublicGppId(text: String) {
        appPreferences?.edit()?.putString(KEY_PUBLIC_GPP_ID, text)?.apply()
    }

    private val appPrefsListener =
        OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (!TextUtils.isEmpty(key)) {
                when (key) {
                    KEY_PUBLIC_GPP_STRING -> {
                        val gppString = getPublicGppString(sharedPreferences)
                        if (!TextUtils.isEmpty(gppString)) {
                            _binding?.viewPublicGppString?.text = gppString
                        } else {
                            _binding?.viewPublicGppString?.text = ""
                        }
                    }
                    KEY_PUBLIC_GPP_ID -> {
                        val gppId = getPublicGppId(sharedPreferences)
                        if (!TextUtils.isEmpty(gppId)) {
                            _binding?.viewPublicGppsidString?.text = gppId
                        } else {
                            _binding?.viewPublicGppsidString?.text = ""
                        }
                    }
                }
            }
        }

    private val internalPrefsListener =
        OnSharedPreferenceChangeListener { _, key ->
            if (!TextUtils.isEmpty(key)) {
                when (key) {
                    KEY_GPP_STRING -> {
                        val gppString = userDataManager?.gppString
                        if (!TextUtils.isEmpty(gppString)) {
                            _binding?.viewInternalGppString?.text = gppString
                        } else {
                            _binding?.viewInternalGppString?.text = ""
                        }
                    }
                    KEY_GPP_ID -> {
                        val gppId = userDataManager?.gppSid
                        if (!TextUtils.isEmpty(gppId)) {
                            _binding?.viewInternalGppsidString?.text = gppId
                        } else {
                            _binding?.viewInternalGppsidString?.text = ""
                        }
                    }
                }
            }
        }


}