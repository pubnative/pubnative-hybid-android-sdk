package net.pubnative.lite.demo.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.models.Quote
import net.pubnative.lite.demo.ui.listeners.InFeedAdListener
import net.pubnative.lite.demo.ui.viewholders.HyBidMRectViewHolder
import net.pubnative.lite.demo.ui.viewholders.SampleTextViewHolder
import net.pubnative.lite.demo.util.SampleQuotes

class InFeedAdapter(val zoneId: String, val adListener: InFeedAdListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_TEXT = 1
    private val TYPE_MRECT = 2

    private val list: List<Quote> = SampleQuotes.list

    private var shouldLoadAd: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_MRECT ->
                HyBidMRectViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_hybid_mrect, parent, false), adListener)
            else ->
                SampleTextViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_sample_text, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HyBidMRectViewHolder -> holder.bind(zoneId, shouldLoadAd)
            else -> {
                holder as SampleTextViewHolder
                holder.bind(list[position])
            }
        }
    }

    override fun getItemCount() = list.count()

    override fun getItemViewType(position: Int): Int {
        if (position == 2) {
            return TYPE_MRECT
        }

        return TYPE_TEXT
    }

    fun loadWithAd() {
        notifyDataSetChanged()
        shouldLoadAd = true
    }
}