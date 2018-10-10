package net.pubnative.lite.demo.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.viewholders.MarkupBannerViewHolder
import net.pubnative.lite.demo.ui.viewholders.MarkupMRectViewHolder
import net.pubnative.lite.demo.ui.viewholders.SampleTextViewHolder

class MarkupAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_TEXT = 1
    private val TYPE_MARKUP_BANNER = 2
    private val TYPE_MARKUP_MRECT = 3

    private var selectedSize = R.id.radio_size_banner
    private var markup = ""

    private val list: List<Quote>

    init {
        list = getSampleQuotes()
    }

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

    fun getSampleQuotes(): List<Quote> {
        val q1 = Quote(
                "Our world is built on biology and once we begin to understand it, it then becomes a technology.",
                "Ryan Bethencourt")
        val q2 = Quote(
                "Happiness is not an ideal of reason but of imagination",
                "Immanuel Kant")
        val q3 = Quote(
                "Science and technology revolutionize our lives, but memory, tradition and myth frame our response.",
                "Arthur M. Schlesinger")
        val q4 = Quote(
                "It's not a faith in technology. It's faith in people.",
                "Steve Jobs")
        val q5 = Quote(
                "We can't blame the technology when we make mistakes.",
                "Tim Berners-Lee")
        val q6 = Quote(
                "Life must be understood backward. But it must be lived forward",
                "Søren Kierkegaard")
        return listOf(q1, q2, q3, q4, q5, q6)
    }

    data class Quote(val quote: String, val author: String)
}