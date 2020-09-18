package net.pubnative.lite.demo.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.TextUtils
import android.widget.Toast

class ClipboardUtils {
    companion object {
        @JvmStatic
        fun copyToClipboard(context: Context, text: String) {
            if (!TextUtils.isEmpty(text)) {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("hybid_demo_debug", text)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
            }
        }

        @JvmStatic
        fun copyFromClipboard(context: Context): String {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = clipboard.primaryClip
            if (clipData != null) {
                val itemCount = clipData.itemCount
                if (itemCount > 0) {
                    val item = clipData.getItemAt(0)
                    return item.text.toString()
                }
            }
            return ""
        }
    }
}