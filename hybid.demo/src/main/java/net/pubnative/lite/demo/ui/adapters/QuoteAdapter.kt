package net.pubnative.lite.demo.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.models.Quote
import net.pubnative.lite.demo.ui.viewholders.SampleTextViewHolder
import net.pubnative.lite.demo.util.SampleQuotes

class QuoteAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val list: List<Quote> = SampleQuotes.list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SampleTextViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_sample_text, parent, false)
    )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as SampleTextViewHolder
        holder.bind(list[position])
    }

    override fun getItemCount() = list.count()
}