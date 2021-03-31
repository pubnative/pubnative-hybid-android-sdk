package net.pubnative.lite.demo.ui.fragments.consent

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.R
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.consent.UserConsentActivity

class PNCMPFragment : Fragment() {
    companion object {
        private const val REQUEST_CONSENT: Int = 290
    }

    private lateinit var vendorListLabel: TextView
    private lateinit var vendorListView: TextView
    private lateinit var privacyPolicyLabel: TextView
    private lateinit var privacyPolicyView: TextView
    private lateinit var consentResultLabel: TextView
    private lateinit var consentResultView: TextView
    private lateinit var canCollectDataView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_pn_cmp, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vendorListLabel = view.findViewById(R.id.label_vendor_list)
        vendorListView = view.findViewById(R.id.view_vendor_list)
        privacyPolicyLabel = view.findViewById(R.id.label_privacy_policy)
        privacyPolicyView = view.findViewById(R.id.view_privacy_policy)
        consentResultLabel = view.findViewById(R.id.label_consent_result)
        consentResultView = view.findViewById(R.id.view_consent_result)
        canCollectDataView = view.findViewById(R.id.view_can_collect_data)

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
            if (HyBid.getUserDataManager() != null) {
                val intent = HyBid.getUserDataManager().getConsentScreenIntent(activity)
                startActivityForResult(intent, REQUEST_CONSENT)
            }
        }

        view.findViewById<Button>(R.id.button_publisher_owned).setOnClickListener {
            if (HyBid.getUserDataManager() != null) {
                vendorListLabel.visibility = View.VISIBLE
                vendorListView.visibility = View.VISIBLE
                privacyPolicyLabel.visibility = View.VISIBLE
                privacyPolicyView.visibility = View.VISIBLE
                vendorListView.text = HyBid.getUserDataManager().vendorListLink
                privacyPolicyView.text = HyBid.getUserDataManager().privacyPolicyLink
            }
        }

        view.findViewById<Button>(R.id.button_accept_consent).setOnClickListener {
            if (HyBid.getUserDataManager() != null) {
                HyBid.getUserDataManager().grantConsent()
                notifyConsentResult(true)
            }
        }

        view.findViewById<Button>(R.id.button_deny_consent).setOnClickListener {
            if (HyBid.getUserDataManager() != null) {
                HyBid.getUserDataManager().denyConsent()
                notifyConsentResult(false)
            }
        }

        view.findViewById<Button>(R.id.button_can_collect_data).setOnClickListener {
            if (HyBid.getUserDataManager() != null) {
                notifyCanCollectData(HyBid.getUserDataManager().canCollectData())
            }
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