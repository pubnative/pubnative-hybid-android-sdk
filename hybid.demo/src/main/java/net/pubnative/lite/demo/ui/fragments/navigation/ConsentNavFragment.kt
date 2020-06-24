package net.pubnative.lite.demo.ui.fragments.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.R
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.consent.UserConsentActivity

class ConsentNavFragment : Fragment() {

    private val REQUEST_CONSENT: Int = 290
    private lateinit var vendorListLabel: TextView
    private lateinit var vendorListView: TextView
    private lateinit var privacyPolicyLabel: TextView
    private lateinit var privacyPolicyView: TextView
    private lateinit var consentResultLabel: TextView
    private lateinit var consentResultView: TextView
    private lateinit var canCollectDataView: TextView
    private lateinit var ccpaConsentStringText: EditText
    private lateinit var ccpaConsentStringView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_nav_consent, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vendorListLabel = view.findViewById(R.id.label_vendor_list)
        vendorListView = view.findViewById(R.id.view_vendor_list)
        privacyPolicyLabel = view.findViewById(R.id.label_privacy_policy)
        privacyPolicyView = view.findViewById(R.id.view_privacy_policy)
        consentResultLabel = view.findViewById(R.id.label_consent_result)
        consentResultView = view.findViewById(R.id.view_consent_result)
        canCollectDataView = view.findViewById(R.id.view_can_collect_data)
        ccpaConsentStringText = view.findViewById(R.id.text_input_ccpa_string)
        ccpaConsentStringView = view.findViewById(R.id.view_ccpa_consent_string)

        view.findViewById<Button>(R.id.button_pn_owned).setOnClickListener {
            /*
            This would be the normal implementation for a regular publisher.
            We remove this condition here for testing purposes

            if (HyBid.getUserDataManager().shouldAskConsent()) {

                val intent = HyBid.getUserDataManager().getConsentScreenIntent(activity)
                startActivityForResult(intent, REQUEST_CONSENT)
            } else {
                Toast.makeText(activity, "Consent has already been answered. If you want to try again please clear your app cache", Toast.LENGTH_LONG).show()
            } */
            val intent = HyBid.getUserDataManager().getConsentScreenIntent(activity)
            startActivityForResult(intent, REQUEST_CONSENT)
        }

        view.findViewById<Button>(R.id.button_publisher_owned).setOnClickListener {
            vendorListLabel.visibility = View.VISIBLE
            vendorListView.visibility = View.VISIBLE
            privacyPolicyLabel.visibility = View.VISIBLE
            privacyPolicyView.visibility = View.VISIBLE
            vendorListView.text = HyBid.getUserDataManager().vendorListLink
            privacyPolicyView.text = HyBid.getUserDataManager().privacyPolicyLink
        }

        view.findViewById<Button>(R.id.button_accept_consent).setOnClickListener {
            HyBid.getUserDataManager().grantConsent()
            notifyConsentResult(true)
        }

        view.findViewById<Button>(R.id.button_deny_consent).setOnClickListener {
            HyBid.getUserDataManager().denyConsent()
            notifyConsentResult(false)
        }

        view.findViewById<Button>(R.id.button_can_collect_data).setOnClickListener {
            notifyCanCollectData(HyBid.getUserDataManager().canCollectData())
        }

        view.findViewById<Button>(R.id.button_set_ccpa_consent).setOnClickListener{
            var ccpaString = ccpaConsentStringText.text.toString()

            if (!ccpaString.isNullOrEmpty()) {
                HyBid.getUserDataManager().iabusPrivacyString = ccpaString
                Toast.makeText(activity, "Stored CCPA String: $ccpaString", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Please enter a CCPA String", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<Button>(R.id.button_get_ccpa_consent).setOnClickListener{
            ccpaConsentStringView.text = HyBid.getUserDataManager().iabusPrivacyString
            ccpaConsentStringView.visibility = View.VISIBLE
        }

        view.findViewById<Button>(R.id.button_remove_ccpa_consent).setOnClickListener{
            HyBid.getUserDataManager().removeIABUSPrivacyString()
            ccpaConsentStringView.visibility = View.GONE
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CONSENT -> {
                if (resultCode == UserConsentActivity.RESULT_CONSENT_ACCEPTED) {
                    notifyConsentResult(true)
                } else if (resultCode == UserConsentActivity.RESULT_CONSENT_REJECTED) {
                    notifyConsentResult(false)
                }
            }
        }
    }

    private fun notifyConsentResult(given: Boolean) {
        consentResultLabel.visibility = View.VISIBLE
        consentResultView.visibility = View.VISIBLE
        if (given) {
            consentResultView.text = getString(R.string.consent_given)
        } else {
            consentResultView.text = getString(R.string.consent_refused)
        }
    }

    private fun notifyCanCollectData(canCollect: Boolean) {
        canCollectDataView.visibility = View.VISIBLE
        if (canCollect) {
            canCollectDataView.text = getString(R.string.yes)
        } else {
            canCollectDataView.text = getString(R.string.no)
        }
    }

}