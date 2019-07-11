package net.pubnative.lite.demo.ui.viewholders

import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.listeners.InFeedAdListener
import net.pubnative.lite.sdk.views.HyBidMRectAdView
import net.pubnative.lite.sdk.views.PNAdView

class HyBidMRectViewHolder(itemView: View, val adListener: InFeedAdListener) : RecyclerView.ViewHolder(itemView), PNAdView.Listener {
    private val TAG = HyBidMRectViewHolder::class.java.simpleName

    private val adView: HyBidMRectAdView = itemView.findViewById(R.id.mrect_view)

    fun bind(zoneId: String, shouldLoad: Boolean) {
        if (!TextUtils.isEmpty(zoneId) && shouldLoad) {
            adView.visibility = View.VISIBLE
            adView.load(zoneId, this)
        }
    }

    override fun onAdLoaded() {
        Log.d(TAG, "onAdLoaded")

        adListener.onInFeedAdLoaded()
        adListener.onInFeedAdImpressionId(adView.impressionId)
    }

    override fun onAdLoadFailed(error: Throwable?) {
        Log.e(TAG, "onAdLoadFailed", error)

        adListener.onInFeedAdLoadError(error)
    }

    override fun onAdImpression() {
        Log.d(TAG, "onAdImpression")
    }

    override fun onAdClick() {
        Log.d(TAG, "onAdClick")
    }
}