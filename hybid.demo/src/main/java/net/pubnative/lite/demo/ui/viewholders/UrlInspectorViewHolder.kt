// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.util.ClipboardUtils

class UrlInspectorViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {

    fun bind(key: String?, value: String?) {
        itemView.findViewById<TextView>(R.id.view_key).text = key
        val inputValue = itemView.findViewById<TextInputEditText>(R.id.view_value)
        val copy = itemView.findViewById<ImageView>(R.id.copy)
        inputValue.setText(value)
        inputValue.isEnabled = false
        copy.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                mView.context, inputValue.text.toString()
            )
        }

        val contentDescriptionValue = key?.plus("_value")
        val contentDescriptionCopy = key?.plus("_copy")
        inputValue.contentDescription = contentDescriptionValue
        copy.contentDescription = contentDescriptionCopy
    }
}