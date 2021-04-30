package net.pubnative.lite.demo.ui.fragments.admob

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.ads.mediationtestsuite.MediationTestSuite
import net.pubnative.lite.demo.R
import net.pubnative.lite.sdk.HyBid


class MediationTestSuiteFragment : Fragment(){
    val TAG = MediationTestSuiteFragment::class.java.simpleName

    private lateinit var appIdText: EditText
    private lateinit var bundleIdText: EditText
    private lateinit var openMediationTestSuiteButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_mediation_test_suite, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appIdText = view.findViewById(R.id.input_app_id)
        bundleIdText = view.findViewById(R.id.input_bundle_id)
        openMediationTestSuiteButton = view.findViewById(R.id.button_open_mediation_test_suite)

        val app = requireContext().packageManager.getApplicationInfo(requireContext().packageName, PackageManager.GET_META_DATA)
        val bundle = app.metaData
        val admobAppId = bundle.getString("com.google.android.gms.ads.APPLICATION_ID")

        appIdText.setText(admobAppId)

        bundleIdText.setText(HyBid.getBundleId())

        openMediationTestSuiteButton.setOnClickListener {
            MediationTestSuite.launch(context);
        }

    }
}