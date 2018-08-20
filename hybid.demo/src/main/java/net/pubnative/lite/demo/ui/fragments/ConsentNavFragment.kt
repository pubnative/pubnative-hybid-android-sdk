package net.pubnative.lite.demo.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_nav_consent, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vendorListLabel = view.findViewById(R.id.label_vendor_list)
        vendorListView = view.findViewById(R.id.view_vendor_list)
        privacyPolicyLabel = view.findViewById(R.id.label_privacy_policy)
        privacyPolicyView = view.findViewById(R.id.view_privacy_policy)
        consentResultLabel = view.findViewById(R.id.label_consent_result)
        consentResultView = view.findViewById(R.id.view_consent_result)

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

}