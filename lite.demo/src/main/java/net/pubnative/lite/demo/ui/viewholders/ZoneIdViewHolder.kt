package net.pubnative.lite.demo.ui.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class ZoneIdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(zoneId: String) {
        val textView = itemView as TextView
        textView.text = zoneId
    }
}