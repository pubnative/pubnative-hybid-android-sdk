package net.pubnative.lite.demo.ui.adapters

import android.support.v7.widget.RecyclerView
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

    private val list: List<String>

    init {
        val s1 = "fjkdkdkd"
        val s2 = "djdjdjdjdj"
        val s3 = ""
        val s4 = "djdjdjdjdj"
        val s5 = "djdjdjdjdj"
        val s6 = "djdjdjdjdj"
        list = listOf(s1, s2, s3, s4, s5, s6)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_MARKUP_BANNER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_markup_banner, parent, false)
                return MarkupBannerViewHolder(view)
            }
            TYPE_MARKUP_MRECT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_markup_mrect, parent, false)
                return MarkupMRectViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sample_text, parent, false)
                return SampleTextViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MarkupBannerViewHolder) {
            holder.bind(markup)
        } else if (holder is MarkupMRectViewHolder) {
            holder.bind(markup)
        } else {
            holder as SampleTextViewHolder
            holder.bind(list.get(position))
        }
    }

    override fun getItemCount() = list.count() + 1

    override fun getItemViewType(position: Int): Int {
        if (position == 2) {
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