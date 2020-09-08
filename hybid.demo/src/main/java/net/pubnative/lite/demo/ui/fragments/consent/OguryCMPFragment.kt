package net.pubnative.lite.demo.ui.fragments.consent

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ogury.cm.OguryChoiceManager
import com.ogury.cm.OguryConsentListener
import com.ogury.core.OguryError
import kotlinx.android.synthetic.main.fragment_ogury_cmp.*
import net.pubnative.lite.demo.R
import net.pubnative.lite.sdk.utils.Logger
import java.lang.RuntimeException
import java.util.*

class OguryCMPFragment : Fragment() {
    private val TAG = OguryCMPFragment::class.java.simpleName

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_ogury_cmp, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_ask.setOnClickListener { askConsent() }
        button_edit.setOnClickListener { editConsent() }
        button_get_consent.setOnClickListener { showConsentString() }
        button_check_purpose.setOnClickListener { checkPurposeAccepted() }
        button_check_vendor.setOnClickListener { checkVendor() }
        button_check_vendor_purposes.setOnClickListener { checkVendorAndPurposes() }

        showConsentString()
    }

    private fun askConsent() {
        OguryChoiceManager.ask(requireActivity(), consentListener)
    }

    private fun editConsent() {
        OguryChoiceManager.edit(requireActivity(), consentListener)
    }

    private fun showConsentString() {
        try {
            val consentString = OguryChoiceManager.TcfV2.getIabString()
            if (TextUtils.isEmpty(consentString)) {
                view_consent_string.setText(R.string.consent_string_not_set)
            } else {
                view_consent_string.text = consentString
            }
        } catch (exception: RuntimeException) {
            view_consent_string.setText(R.string.consent_string_not_set)
        }
    }

    private fun checkVendor() {
        val vendorIdString = input_vendor_id.text.toString().trim()
        if (!TextUtils.isEmpty(vendorIdString)) {
            val accepted = OguryChoiceManager.TcfV2.isAccepted(vendorIdString.toInt())
            val consentedString = if (accepted) "accepted" else "denied"
            view_consent_result.text = String.format(Locale.ENGLISH, "Consent has been $consentedString for vendor with id: $vendorIdString")
        }
    }

    private fun checkPurposeAccepted() {
        val purposeId = when(group_purposes.checkedRadioButtonId) {
            R.id.radio_personalised_ads -> OguryChoiceManager.TcfV2.Purpose.CREATE_PERSONALISED_ADS
            R.id.radio_personalised_content -> OguryChoiceManager.TcfV2.Purpose.CREATE_PERSONALISED_CONTENT
            R.id.radio_develop_improve_product -> OguryChoiceManager.TcfV2.Purpose.DEVELOP_AND_IMPROVE_PRODUCTS
            R.id.radio_market_research -> OguryChoiceManager.TcfV2.Purpose.MARKET_RESEARCH
            R.id.radio_measure_ad_performance -> OguryChoiceManager.TcfV2.Purpose.MEASURE_AD_PERFORMANCE
            R.id.radio_measure_content_performance -> OguryChoiceManager.TcfV2.Purpose.MEASURE_CONTENT_PERFORMANCE
            R.id.radio_select_basic_ads -> OguryChoiceManager.TcfV2.Purpose.SELECT_BASIC_ADS
            R.id.radio_select_personalised_ads -> OguryChoiceManager.TcfV2.Purpose.SELECT_PERSONALISED_ADS
            R.id.radio_select_personalised_content -> OguryChoiceManager.TcfV2.Purpose.SELECT_PERSONALISED_CONTENT
            R.id.radio_store_information -> OguryChoiceManager.TcfV2.Purpose.STORE_INFORMATION
            else -> -1
        }

        if (purposeId != -1) {
            val accepted = OguryChoiceManager.TcfV2.isPurposeAccepted(purposeId)
            val consentedString = if (accepted) "accepted" else "denied"
            view_consent_result.text = String.format(Locale.ENGLISH, "Consent has been $consentedString for the purpose with id: $purposeId")
        }
    }

    private fun checkVendorAndPurposes() {
        val vendorIdString = input_vendor_id.text.toString().trim()
        if (!TextUtils.isEmpty(vendorIdString)) {
            val accepted = OguryChoiceManager.TcfV2.isVendorAndItsPurposesAccepted(vendorIdString.toInt())
            val consentedString = if (accepted) "accepted" else "denied"
            view_consent_result.text = String.format(Locale.ENGLISH, "Consent has been $consentedString for vendor with id: $vendorIdString and all it\'s purposes")
        }
    }

    private val consentListener = object : OguryConsentListener {
        override fun onComplete(answer: OguryChoiceManager.Answer?) {
            Logger.d(TAG, "Ogury consent: onComplete")
        }

        override fun onError(error: OguryError?) {
            Logger.e(TAG, "Ogury consent error: ", error)
        }
    }
}