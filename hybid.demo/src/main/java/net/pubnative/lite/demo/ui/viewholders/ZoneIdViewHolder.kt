// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.ui.listeners.ZoneIdClickListener

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class ZoneIdViewHolder(itemView: View, private var listener: ZoneIdClickListener?) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    private var zoneId: String = ""

    init {
        itemView.setOnClickListener(this)
    }

    fun bind(zoneId: String) {
        val textView = itemView as TextView
        textView.text = zoneId
        this.zoneId = zoneId
    }

    override fun onClick(v: View?) {
        listener?.onZoneIdClicked(zoneId)
    }
}