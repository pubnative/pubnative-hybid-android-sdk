package net.pubnative.lite.demo.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.models.Quote
import net.pubnative.lite.demo.ui.viewholders.MarkupBannerViewHolder
import net.pubnative.lite.demo.ui.viewholders.MarkupMRectViewHolder
import net.pubnative.lite.demo.ui.viewholders.SampleTextViewHolder
import net.pubnative.lite.demo.util.SampleQuotes

class MarkupAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_TEXT = 1
    private val TYPE_MARKUP_BANNER = 2
    private val TYPE_MARKUP_MRECT = 3

    private var selectedSize = R.id.radio_size_banner
    private var markup = ""

    private val list: List<Quote> = SampleQuotes.list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_MARKUP_BANNER ->
                MarkupBannerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_markup_banner, parent, false))
            TYPE_MARKUP_MRECT ->
                MarkupMRectViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_markup_mrect, parent, false))
            else ->
                SampleTextViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_sample_text, parent, false))
        }
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MarkupBannerViewHolder -> holder.bind(markup)
            is MarkupMRectViewHolder -> holder.bind(markup)
            else -> {
                holder as SampleTextViewHolder
                holder.bind(list[position])
            }
        }
    }

    override fun getItemCount() = list.count()

    override fun getItemViewType(position: Int): Int {
        if (position == 2 && !TextUtils.isEmpty(markup)) {
            if (selectedSize == R.id.radio_size_banner) {
                return TYPE_MARKUP_BANNER
            } else if (selectedSize == R.id.radio_size_medium) {
                return TYPE_MARKUP_MRECT
            }
        }

        return TYPE_TEXT
    }

    fun refreshWithMarkup(markup: String, selectedSize: Int) {
        this.markup = markup
        this.selectedSize = selectedSize
        notifyDataSetChanged()
    }
}