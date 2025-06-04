// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import net.pubnative.lite.demo.R
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.api.ApiManager
import net.pubnative.lite.sdk.api.SDKConfigAPiClient.ConfigType

class SDKConfigDialog : DialogFragment() {

    private var configURL: String = ""
    private var configType: ConfigType = ConfigType.PRODUCTION
    private lateinit var groupSDKConfigSelection: RadioGroup
    private lateinit var inputSDKConfigTestingUrl: EditText

    var listener: OnDismissListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.sdk_config_dialog_layout, container, true)
        dialog!!.requestWindowFeature(STYLE_NO_TITLE)
        isCancelable = false
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        groupSDKConfigSelection = view.findViewById(R.id.groupSDKConfigSelection)
        inputSDKConfigTestingUrl = view.findViewById(R.id.input_sdk_config_testing_url)
        groupSDKConfigSelection.setOnCheckedChangeListener { _, _ ->
            when (groupSDKConfigSelection.checkedRadioButtonId) {
                R.id.radio_production_url -> inputSDKConfigTestingUrl.setEnabled(false)
                R.id.radio_testing_url -> inputSDKConfigTestingUrl.setEnabled(true)
                else -> inputSDKConfigTestingUrl.setEnabled(false)
            }
        }

        view.findViewById<Button>(R.id.button_save).setOnClickListener {
            when (groupSDKConfigSelection.checkedRadioButtonId) {
                R.id.radio_production_url -> {
                    configURL = ""
                    configType = ConfigType.PRODUCTION
                }

                R.id.radio_testing_url -> {
                    configURL = inputSDKConfigTestingUrl.text.toString()
                    configType = ConfigType.TESTING
                }

                else -> {
                    configURL = ""
                    configType = ConfigType.PRODUCTION
                }
            }

            if (configType == ConfigType.PRODUCTION) {
                hideDialog()
            } else {
                if (configURL.isNotEmpty()) {
                    hideDialog()
                } else {
                    activity?.let {
                        Toast.makeText(it, "Please enter a valid testing URL", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        if (HyBid.isInitialized()) {
            when (ApiManager.getSDKConfigType()) {
                ConfigType.PRODUCTION -> {
                    groupSDKConfigSelection.check(R.id.radio_production_url)
                    inputSDKConfigTestingUrl.setText("")
                }

                else -> {
                    groupSDKConfigSelection.check(R.id.radio_testing_url)
                    inputSDKConfigTestingUrl.isEnabled = true
                    inputSDKConfigTestingUrl.setText(ApiManager.getSDKConfigURL())
                }
            }
        }
    }

    private fun hideDialog() {
        listener?.onDismiss(configURL)
        SDKConfigDialogManager.getInstance()?.hideDialog()
    }

    fun setDismissListener(listener: OnDismissListener) {
        this.listener = listener
    }

    interface OnDismissListener {
        fun onDismiss(url: String?)
    }
}