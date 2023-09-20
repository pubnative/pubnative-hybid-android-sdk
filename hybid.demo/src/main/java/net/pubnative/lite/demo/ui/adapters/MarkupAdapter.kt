package net.pubnative.lite.demo.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.models.Quote
import net.pubnative.lite.demo.ui.fragments.markup.MarkupSize
import net.pubnative.lite.demo.ui.viewholders.MarkupBannerViewHolder
import net.pubnative.lite.demo.ui.viewholders.MarkupLeaderboardViewHolder
import net.pubnative.lite.demo.ui.viewholders.MarkupMRectViewHolder
import net.pubnative.lite.demo.ui.viewholders.SampleTextViewHolder
import net.pubnative.lite.demo.util.SampleQuotes


class MarkupAdapter(
        private var mListener: OnLogDisplayListener,
        private var mAdRefreshListener: OnAdRefreshListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), OnLogDisplayListener,
        OnExpandedAdCloseListener {

    companion object {
        private const val TYPE_TEXT = 1
        private const val TYPE_MARKUP_BANNER = 2
        private const val TYPE_MARKUP_MRECT = 3
        private const val TYPE_MARKUP_LEADERBOARD = 4
        private const val ATTACHED_AD_VIEW_POSITION = 2
    }

    private var isClosingExpandedAd: Boolean = false
    private var selectedSize: MarkupSize = MarkupSize.BANNER
    private var markup = ""

    private val list: List<Quote> = SampleQuotes.list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_MARKUP_BANNER -> MarkupBannerViewHolder(
                    LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_markup_banner, parent, false), mListener, this
            )

            TYPE_MARKUP_MRECT -> MarkupMRectViewHolder(
                    LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_markup_mrect, parent, false), mListener
            )

            TYPE_MARKUP_LEADERBOARD -> MarkupLeaderboardViewHolder(
                    LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_markup_leaderboard, parent, false), mListener
            )

            else -> SampleTextViewHolder(
                    LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_sample_text, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MarkupBannerViewHolder -> holder.bind(markup)
            is MarkupMRectViewHolder -> holder.bind(markup)
            is MarkupLeaderboardViewHolder -> holder.bind(markup)
            else -> {
                holder as SampleTextViewHolder
                holder.bind(list[position])
            }
        }
    }

    override fun getItemCount() = list.count()

    override fun getItemViewType(position: Int): Int {
        if (position == ATTACHED_AD_VIEW_POSITION && markup.isNotEmpty()) {
            when (selectedSize) {
                MarkupSize.BANNER -> {
                    return TYPE_MARKUP_BANNER
                }

                MarkupSize.MEDIUM -> {
                    return TYPE_MARKUP_MRECT
                }

                MarkupSize.LEADERBOARD -> {
                    return TYPE_MARKUP_LEADERBOARD
                }

                else -> {
                    return TYPE_TEXT
                }
            }
        }

        return TYPE_TEXT
    }

    fun refreshWithMarkup(markup: String, selectedSize: MarkupSize) {
        this.markup = markup
        this.selectedSize = selectedSize
        notifyDataSetChanged()
    }

    override fun displayLogs() {
        mListener.displayLogs()
    }

    override fun onExpandedAdClosed() {
        mAdRefreshListener.onAdRefresh()
        notifyItemChanged(ATTACHED_AD_VIEW_POSITION)
    }
}