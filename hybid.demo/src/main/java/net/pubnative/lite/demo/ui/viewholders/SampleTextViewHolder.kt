package net.pubnative.lite.demo.ui.viewholders

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.adapters.MarkupAdapter

class SampleTextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(quote: MarkupAdapter.Quote) {
            itemView.findViewById<TextView>(R.id.view_quote).text = quote.quote
            itemView.findViewById<TextView>(R.id.view_author).text = quote.author
    }
}