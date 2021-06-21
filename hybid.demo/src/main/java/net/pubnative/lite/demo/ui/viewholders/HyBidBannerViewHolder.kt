package net.pubnative.lite.demo.ui.viewholders

import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.listeners.InFeedAdListener
import net.pubnative.lite.demo.util.convertDpToPx
import net.pubnative.lite.sdk.models.AdSize
import net.pubnative.lite.sdk.views.HyBidAdView
import net.pubnative.lite.sdk.views.PNAdView

class HyBidBannerViewHolder(itemView: View, val adListener: InFeedAdListener) : RecyclerView.ViewHolder(itemView), PNAdView.Listener {
    private val TAG = HyBidBannerViewHolder::class.java.simpleName

    private val adView: HyBidAdView = itemView.findViewById(R.id.banner_view)

    fun bind(zoneId: String, adSize: AdSize, shouldLoad: Boolean) {
        if (!TextUtils.isEmpty(zoneId) && shouldLoad) {
            adView.setAdSize(adSize)
            val layoutParams = FrameLayout.LayoutParams(
                    convertDpToPx(itemView.context, adSize.width.toFloat()),
                    convertDpToPx(itemView.context, adSize.height.toFloat()))
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL

            adView.layoutParams = layoutParams
            adView.visibility = View.VISIBLE
            adView.load(zoneId, this)
        }
    }

    override fun onAdLoaded() {
        adListener.onInFeedAdLoaded()
        adListener.onInFeedAdCreativeId(adView.creativeId)
    }

    override fun onAdLoadFailed(error: Throwable?) {
        adListener.onInFeedAdLoadError(error)
    }

    override fun onAdImpression() {
        Log.d(TAG, "onAdImpression")
    }

    override fun onAdClick() {
        Log.d(TAG, "onAdClick")
    }
}