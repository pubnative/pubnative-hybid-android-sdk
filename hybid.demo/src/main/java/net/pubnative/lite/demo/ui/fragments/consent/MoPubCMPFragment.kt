package net.pubnative.lite.demo.ui.fragments.consent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mopub.common.MoPub
import com.mopub.common.privacy.ConsentDialogListener
import com.mopub.common.privacy.ConsentStatus
import com.mopub.common.privacy.PersonalInfoManager
import com.mopub.mobileads.MoPubErrorCode
import net.pubnative.lite.demo.R


class MoPubCMPFragment : Fragment(), ConsentDialogListener{
    private val TAG = MoPubCMPFragment::class.java.simpleName

    private lateinit var mPersonalInfoManager: PersonalInfoManager
    private lateinit var consentResultLabel: TextView
    private lateinit var consentResultView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_mopub_cmp, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPersonalInfoManager = MoPub.getPersonalInformationManager()!!

        consentResultLabel = view.findViewById(R.id.label_consent_result)
        consentResultView =  view.findViewById(R.id.view_consent_result)

        view.findViewById<Button>(R.id.button_show_mopub_consent).setOnClickListener {
            showMopubConsentDialog()
        }

        view.findViewById<Button>(R.id.button_can_collect_data).setOnClickListener {
            notifyConsentResult()
        }
    }

    private fun getMopubConsentStatus(): ConsentStatus {
        return mPersonalInfoManager.personalInfoConsentStatus
    }

    private fun showMopubConsentDialog(){
        mPersonalInfoManager.loadConsentDialog(this)
    }

    private fun notifyConsentResult() {
        consentResultLabel.visibility = View.VISIBLE
        consentResultView.visibility = View.VISIBLE
        when (getMopubConsentStatus()) {
            ConsentStatus.DNT -> consentResultView.text = getString(R.string.consent_dnt)
            ConsentStatus.EXPLICIT_NO -> consentResultView.text = getString(R.string.consent_refused)
            ConsentStatus.EXPLICIT_YES -> consentResultView.text = getString(R.string.consent_given)
            else -> {
                consentResultView.text = getString(R.string.consent_unknown)
            }
        }
    }

    // -------- Listener ---------
    override fun onConsentDialogLoaded() {
        mPersonalInfoManager.showConsentDialog()
    }

    override fun onConsentDialogLoadFailed(moPubErrorCode: MoPubErrorCode) {
        Log.d(TAG, "MoPub failed to load the consent dialog")
    }
}