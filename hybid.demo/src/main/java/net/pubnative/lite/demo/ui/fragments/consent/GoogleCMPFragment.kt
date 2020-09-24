package net.pubnative.lite.demo.ui.fragments.consent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.ump.*
import kotlinx.android.synthetic.main.fragment_google_cmp.*
import net.pubnative.lite.demo.R
import net.pubnative.lite.sdk.utils.Logger

class GoogleCMPFragment : Fragment() {
    private val TAG = GoogleCMPFragment::class.java.simpleName

    private lateinit var consentInformation: ConsentInformation
    private var consentForm: ConsentForm? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_google_cmp, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        consentInformation = UserMessagingPlatform.getConsentInformation(requireActivity())

        button_request.setOnClickListener {
            requestConsentInfo()
        }

        button_load.setOnClickListener {
            loadForm()
        }

        button_show.setOnClickListener {
            showForm()
        }
    }

    private val updateSuccessListener = ConsentInformation.OnConsentInfoUpdateSuccessListener {
        Logger.d(TAG,"onConsentInfoUpdateSuccess")
        Toast.makeText(requireContext(), R.string.consent_info_update_success, Toast.LENGTH_SHORT).show()
    }

    private val updateFailureListener = ConsentInformation.OnConsentInfoUpdateFailureListener { error ->
        Logger.e(TAG,"onConsentInfoUpdateFailure: " + error.message)
        view_error.text = error.message
    }

    private val consentFormLoadSuccessListener = UserMessagingPlatform.OnConsentFormLoadSuccessListener { form ->
        consentForm = form
        Logger.d(TAG,"onConsentFormLoadSuccess")
        Toast.makeText(requireContext(), R.string.consent_form_load_success, Toast.LENGTH_SHORT).show()
    }

    private val consentFormLoadFailureListener = UserMessagingPlatform.OnConsentFormLoadFailureListener { error ->
        Logger.e(TAG,"onConsentFormLoadFailure: " + error.message)
        view_error.text = error.message
    }

    private val consentFormDismissedListener = ConsentForm.OnConsentFormDismissedListener { error ->
        if (error != null) {
            Logger.e(TAG,"onConsentFormDismissed: " + error.message)
            view_error.text = error.message
        } else {
            Logger.d(TAG,"onConsentFormDismissed")
        }
    }

    private fun requestConsentInfo() {
        val params = ConsentRequestParameters.Builder().build()
        consentInformation.requestConsentInfoUpdate(requireActivity(), params, updateSuccessListener, updateFailureListener)
    }

    private fun loadForm() {
        if (consentInformation.isConsentFormAvailable) {
            UserMessagingPlatform.loadConsentForm(requireActivity(), consentFormLoadSuccessListener, consentFormLoadFailureListener)
        } else {
            Toast.makeText(requireContext(), R.string.consent_info_not_available, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showForm() {
        // This should be the real implementation
        /*if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
            consentForm?.show(requireActivity(), consentFormDismissedListener)
        }*/
        if (consentForm != null) {
            consentForm?.show(requireActivity(), consentFormDismissedListener)
        } else {
            Toast.makeText(requireContext(), R.string.consent_form_not_available, Toast.LENGTH_SHORT).show()
        }
    }
}