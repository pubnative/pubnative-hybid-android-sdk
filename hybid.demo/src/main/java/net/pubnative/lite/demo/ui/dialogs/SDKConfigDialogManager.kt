// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.dialogs

import androidx.fragment.app.FragmentManager

class SDKConfigDialogManager {

    private var dialog: SDKConfigDialog? = null

    fun showDialog(
        listener: SDKConfigDialog.OnDismissListener,
        fragmentManager: FragmentManager
    ) {
        if (dialog == null || dialog?.isVisible == false) {
            dialog = SDKConfigDialog()
            dialog?.setDismissListener(listener)
            dialog?.show(fragmentManager, "SDKConfigDialog")
        }
    }

    fun hideDialog() {
        if (dialog?.isVisible == true) {
            dialog?.dismiss()
        }
    }

    companion object {
        private var sInstance: SDKConfigDialogManager? = null

        fun getInstance(): SDKConfigDialogManager? {
            if (sInstance == null) {
                synchronized(SDKConfigDialogManager::class.java) { // synchronized to avoid concurrency problem
                    if (sInstance == null) {
                        sInstance = SDKConfigDialogManager()
                    }
                }
            }
            return sInstance
        }
    }
}