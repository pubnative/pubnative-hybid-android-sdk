// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.models.SDKDataItem
import net.pubnative.lite.demo.ui.viewholders.SDKDataItemViewHolder

class CustomSDKDataAdapter(val items: List<SDKDataItem>) :
    RecyclerView.Adapter<SDKDataItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SDKDataItemViewHolder {
        return SDKDataItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_sdk_data, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SDKDataItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}