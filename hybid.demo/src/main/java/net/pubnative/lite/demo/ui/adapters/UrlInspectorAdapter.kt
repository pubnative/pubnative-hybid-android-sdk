// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.viewholders.UrlInspectorViewHolder
import java.util.TreeMap

class UrlInspectorAdapter(uri: TreeMap<String, String>?) : RecyclerView.Adapter<UrlInspectorViewHolder>(){
    private var parameters:  TreeMap<String, String>? = uri

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = UrlInspectorViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.item_key_value, parent, false)
    )

    override fun onBindViewHolder(holder: UrlInspectorViewHolder, position: Int) {
        val key = parameters?.keys?.elementAt(position)
        val value = parameters?.get(key)
        holder.bind(key, value)
    }

    override fun getItemCount() = parameters!!.size

}