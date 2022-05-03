package net.pubnative.lite.demo.ui.fragments.consent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.ump.*
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.databinding.FragmentGoogleCmpBinding
import net.pubnative.lite.sdk.utils.Logger

class GoogleCMPFragment : Fragment() {

    private val TAG = GoogleCMPFragment::class.java.simpleName

    private var _binding: FragmentGoogleCmpBinding? = null

    private val binding get() = _binding!!

    private lateinit var consentInformation: ConsentInformation
    private var consentForm: ConsentForm? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoogleCmpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        consentInformation = UserMessagingPlatform.getConsentInformation(requireActivity())

        _binding?.buttonRequest?.setOnClickListener {
            requestConsentInfo()
        }

        _binding?.buttonLoad?.setOnClickListener {
            loadForm()
        }

        _binding?.buttonShow?.setOnClickListener {
            showForm()
        }
    }

    private val updateSuccessListener = ConsentInformation.OnConsentInfoUpdateSuccessListener {
        Logger.d(TAG, "onConsentInfoUpdateSuccess")
        Toast.makeText(requireContext(), R.string.consent_info_update_success, Toast.LENGTH_SHORT)
            .show()
    }

    private val updateFailureListener =
        ConsentInformation.OnConsentInfoUpdateFailureListener { error ->
            Logger.e(TAG, "onConsentInfoUpdateFailure: " + error.message)
            _binding?.viewError?.text = error.message
        }

    private val consentFormLoadSuccessListener =
        UserMessagingPlatform.OnConsentFormLoadSuccessListener { form ->
            consentForm = form
            Logger.d(TAG, "onConsentFormLoadSuccess")
            Toast.makeText(requireContext(), R.string.consent_form_load_success, Toast.LENGTH_SHORT)
                .show()
        }

    private val consentFormLoadFailureListener =
        UserMessagingPlatform.OnConsentFormLoadFailureListener { error ->
            Logger.e(TAG, "onConsentFormLoadFailure: " + error.message)
            _binding?.viewError?.text = error.message
        }

    private val consentFormDismissedListener = ConsentForm.OnConsentFormDismissedListener { error ->
        if (error != null) {
            Logger.e(TAG, "onConsentFormDismissed: " + error.message)
            _binding?.viewError?.text = error.message
        } else {
            Logger.d(TAG, "onConsentFormDismissed")
        }
    }

    private fun requestConsentInfo() {
        val params = ConsentRequestParameters.Builder().build()
        consentInformation.requestConsentInfoUpdate(
            requireActivity(),
            params,
            updateSuccessListener,
            updateFailureListener
        )
    }

    private fun loadForm() {
        if (consentInformation.isConsentFormAvailable) {
            UserMessagingPlatform.loadConsentForm(
                requireActivity(),
                consentFormLoadSuccessListener,
                consentFormLoadFailureListener
            )
        } else {
            Toast.makeText(
                requireContext(),
                R.string.consent_info_not_available,
                Toast.LENGTH_SHORT
            ).show()
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
            Toast.makeText(
                requireContext(),
                R.string.consent_form_not_available,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}