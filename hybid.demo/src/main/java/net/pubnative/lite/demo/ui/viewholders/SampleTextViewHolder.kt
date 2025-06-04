// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.models.Quote
import net.pubnative.lite.demo.ui.adapters.OnLogDisplayListener

class SampleTextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(quote: Quote) {
        itemView.findViewById<TextView>(R.id.view_quote).text = quote.quote
        itemView.findViewById<TextView>(R.id.view_author).text = quote.author
    }
}