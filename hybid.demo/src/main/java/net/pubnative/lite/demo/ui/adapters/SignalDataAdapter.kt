package net.pubnative.lite.demo.ui.adapters

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.models.Quote
import net.pubnative.lite.demo.ui.viewholders.SampleTextViewHolder
import net.pubnative.lite.demo.ui.viewholders.SignalDataBannerViewHolder
import net.pubnative.lite.demo.ui.viewholders.SignalDataLeaderboardViewHolder
import net.pubnative.lite.demo.ui.viewholders.SignalDataMRectViewHolder
import net.pubnative.lite.demo.util.SampleQuotes

class SignalDataAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_TEXT = 1
        private const val TYPE_SIGNAL_DATA_BANNER = 2
        private const val TYPE_SIGNAL_DATA_MRECT = 3
        private const val TYPE_SIGNAL_DATA_LEADERBOARD = 4
    }

    private var selectedSize = R.id.radio_size_banner
    private var signalData = ""

    private val list: List<Quote> = SampleQuotes.list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_SIGNAL_DATA_BANNER ->
                SignalDataBannerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_markup_banner, parent, false))
            TYPE_SIGNAL_DATA_MRECT ->
                SignalDataMRectViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_markup_mrect, parent, false))
            TYPE_SIGNAL_DATA_LEADERBOARD ->
                SignalDataLeaderboardViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_markup_leaderboard, parent, false))
            else ->
                SampleTextViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_sample_text, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SignalDataBannerViewHolder -> holder.bind(signalData)
            is SignalDataMRectViewHolder -> holder.bind(signalData)
            is SignalDataLeaderboardViewHolder -> holder.bind(signalData)
            else -> {
                holder as SampleTextViewHolder
                holder.bind(list[position])
            }
        }
    }

    override fun getItemCount() = list.count()

    override fun getItemViewType(position: Int): Int {
        if (position == 2 && !TextUtils.isEmpty(signalData)) {
            if (selectedSize == R.id.radio_size_banner) {
                return TYPE_SIGNAL_DATA_BANNER
            } else if (selectedSize == R.id.radio_size_medium) {
                return TYPE_SIGNAL_DATA_MRECT
            } else if (selectedSize == R.id.radio_size_leaderboard) {
                return TYPE_SIGNAL_DATA_LEADERBOARD
            }
        }

        return TYPE_TEXT
    }

    fun refreshWithSignalData(signalData: String, selectedSize: Int) {
        this.signalData = signalData
        this.selectedSize = selectedSize
        notifyDataSetChanged()
    }
}