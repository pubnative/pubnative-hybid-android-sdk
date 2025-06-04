// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.models.SDKDataItem

class SDKDataItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(dataItem: SDKDataItem) {
        itemView.findViewById<TextView>(R.id.view_title).text = dataItem.title
        itemView.findViewById<TextView>(R.id.view_value).text = dataItem.value
    }
}