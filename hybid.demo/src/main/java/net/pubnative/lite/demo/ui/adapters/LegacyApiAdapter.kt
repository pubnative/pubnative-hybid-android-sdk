// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.models.Quote
import net.pubnative.lite.demo.ui.fragments.apitester.LegacyApiTesterSize
import net.pubnative.lite.demo.ui.viewholders.*
import net.pubnative.lite.demo.util.SampleQuotes
import net.pubnative.lite.sdk.models.Ad

class LegacyApiAdapter(private var mListener: OnLogDisplayListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), OnLogDisplayListener {

    companion object {
        private const val TYPE_TEXT = 1
        private const val TYPE_API_BANNER = 2
        private const val TYPE_API_MRECT = 3
        private const val TYPE_API_NATIVE = 5
        private const val TYPE_API_LEADERBOARD = 4
    }

    private var selectedSize: LegacyApiTesterSize = LegacyApiTesterSize.BANNER
    private var ad: Ad? = null

    private val list: List<Quote> = SampleQuotes.list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_API_BANNER -> LegacyApiBannerViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_markup_banner, parent, false), mListener
            )

            TYPE_API_MRECT -> LegacyApiMRectViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_markup_mrect, parent, false), mListener
            )

            TYPE_API_LEADERBOARD -> LegacyApiLeaderboardViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_markup_leaderboard, parent, false), mListener
            )

            TYPE_API_NATIVE -> LegacyApiNativeViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_markup_native, parent, false), mListener
            )

            else -> SampleTextViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_sample_text, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LegacyApiBannerViewHolder -> holder.bind(ad)
            is LegacyApiMRectViewHolder -> holder.bind(ad)
            is LegacyApiLeaderboardViewHolder -> holder.bind(ad)
            is LegacyApiNativeViewHolder -> holder.bind(ad)
            else -> {
                holder as SampleTextViewHolder
                holder.bind(list[position])
            }
        }
    }

    override fun getItemCount() = list.count()

    override fun getItemViewType(position: Int): Int {
        if (position == 7 && ad != null) {
            when (selectedSize) {
                LegacyApiTesterSize.BANNER -> {
                    return TYPE_API_BANNER
                }

                LegacyApiTesterSize.MEDIUM -> {
                    return TYPE_API_MRECT
                }

                LegacyApiTesterSize.LEADERBOARD -> {
                    return TYPE_API_LEADERBOARD
                }

                LegacyApiTesterSize.NATIVE -> {
                    return TYPE_API_NATIVE
                }

                else -> {
                    return TYPE_TEXT
                }
            }
        }

        return TYPE_TEXT
    }

    fun refreshWithAd(ad: Ad?, selectedSize: LegacyApiTesterSize) {
        this.ad = ad
        this.selectedSize = selectedSize
        notifyDataSetChanged()
    }

    override fun displayLogs() {
        mListener.displayLogs()
    }
}