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
import net.pubnative.lite.demo.util.BeaconDescription
import net.pubnative.lite.demo.util.ClipboardUtils

class BeaconListViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {

    fun bind(beaconDescription: BeaconDescription) {
        val key = itemView.findViewById<TextView>(R.id.view_key)
        key.text = beaconDescription.description
        val inputValue = itemView.findViewById<TextInputEditText>(R.id.view_value)
        val copy = itemView.findViewById<ImageView>(R.id.copy)
        inputValue.setText(beaconDescription.beaconUrl)
        inputValue.isEnabled = false
        copy.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                mView.context, inputValue.text.toString()
            )
        }

        val contentDescriptionValue = beaconDescription.description.plus("_value")
        val contentDescriptionCopy = beaconDescription.description.plus("_copy")
        val contentDescriptionKey = beaconDescription.description.plus("_key")
        inputValue.contentDescription = contentDescriptionValue
        copy.contentDescription = contentDescriptionCopy
        key.contentDescription = contentDescriptionKey
    }
}