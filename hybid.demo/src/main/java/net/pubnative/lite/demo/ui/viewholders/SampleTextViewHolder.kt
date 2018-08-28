package net.pubnative.lite.demo.ui.viewholders

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import net.pubnative.lite.demo.R

class SampleTextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(text: String) {
        if (TextUtils.isEmpty(text)) {
            itemView.findViewById<TextView>(R.id.view_text).text = text
        }
    }
}