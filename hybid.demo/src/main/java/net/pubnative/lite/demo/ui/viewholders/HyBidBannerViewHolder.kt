package net.pubnative.lite.demo.ui.viewholders

import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.listeners.InFeedAdListener
import net.pubnative.lite.sdk.models.AdSize
import net.pubnative.lite.sdk.views.HyBidAdView
import net.pubnative.lite.sdk.views.PNAdView

class HyBidBannerViewHolder(itemView: View, val adListener: InFeedAdListener) :
    RecyclerView.ViewHolder(itemView), PNAdView.Listener {
    private val TAG = HyBidBannerViewHolder::class.java.simpleName

    private lateinit var adView: HyBidAdView

    fun bind(
        zoneId: String,
        adSize: AdSize,
        shouldLoad: Boolean,
        autoRefresh: Boolean,
        selectedApi: Int
    ) {
        if (!TextUtils.isEmpty(zoneId) && shouldLoad) {
            val container = itemView.findViewById<FrameLayout>(R.id.banner_view)
            container.removeAllViews()

            adView = HyBidAdView(itemView.context, adSize)

            val layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.gravity = Gravity.CENTER

            container.layoutParams = layoutParams
            container.visibility = View.VISIBLE
            container.addView(adView)

            autoRefreshCheck(autoRefresh)
            if (selectedApi == R.id.radio_api_ortb) {
                adView.loadExchangeAd(zoneId, this)
            } else {
                adView.load(zoneId, this)
            }
        }
    }

    private fun autoRefreshCheck(autoRefresh: Boolean) {
        if (autoRefresh) {
            adView.setAutoRefreshTimeInSeconds(30)
        } else {
            adView.stopAutoRefresh()
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