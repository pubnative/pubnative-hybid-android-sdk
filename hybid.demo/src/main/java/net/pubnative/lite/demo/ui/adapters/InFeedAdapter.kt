package net.pubnative.lite.demo.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.models.Quote
import net.pubnative.lite.demo.ui.listeners.InFeedAdListener
import net.pubnative.lite.demo.ui.viewholders.HyBidBannerViewHolder
import net.pubnative.lite.demo.ui.viewholders.SampleTextViewHolder
import net.pubnative.lite.demo.util.SampleQuotes
import net.pubnative.lite.sdk.models.AdSize

class InFeedAdapter(val zoneId: String, val adListener: InFeedAdListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_TEXT = 1
    private val TYPE_MRECT = 2

    private val list: List<Quote> = SampleQuotes.list
    private var adSize: AdSize = AdSize.SIZE_300x250
    private var selectedApi: Int = R.id.radio_api_legacy
    private var selectedFormat: Int = R.id.radio_format_html

    private var shouldLoadAd: Boolean = false
    private var autoRefresh: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_MRECT ->
                HyBidBannerViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_hybid_banner, parent, false), adListener
                )

            else ->
                SampleTextViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_sample_text, parent, false)
                )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HyBidBannerViewHolder -> holder.bind(
                zoneId,
                adSize,
                shouldLoadAd,
                autoRefresh,
                selectedApi,
                selectedFormat
            )

            else -> {
                holder as SampleTextViewHolder
                holder.bind(list[position])
            }
        }
    }

    override fun getItemCount() = list.count()

    override fun getItemViewType(position: Int): Int {
        if (position == 7) {
            return TYPE_MRECT
        }

        return TYPE_TEXT
    }

    fun loadWithAd(adSize: AdSize, selectedApi: Int, selectedFormat: Int) {
        this.adSize = adSize
        this.selectedApi = selectedApi
        this.selectedFormat = selectedFormat
        notifyDataSetChanged()
        shouldLoadAd = true
    }

    fun setAutoRefresh(enableAutoRefresh: Boolean) {
        autoRefresh = enableAutoRefresh
    }
}