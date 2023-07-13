package net.pubnative.lite.demo.ui.viewholders

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.adapters.OnLogDisplayListener
import net.pubnative.lite.sdk.models.Ad
import net.pubnative.lite.sdk.utils.Logger
import net.pubnative.lite.sdk.views.HyBidAdView
import net.pubnative.lite.sdk.views.HyBidBannerAdView
import net.pubnative.lite.sdk.views.PNAdView

class LegacyApiBannerViewHolder(itemView: View, private var mListener: OnLogDisplayListener) :
    RecyclerView.ViewHolder(itemView), PNAdView.Listener {
    private val TAG = LegacyApiBannerViewHolder::class.java.simpleName
    private lateinit var banner: HyBidAdView

    fun bind(ad: Ad?) {
        ad?.let {
            val container = itemView.findViewById<FrameLayout>(R.id.banner_container)
            container.removeAllViews()

            banner = HyBidBannerAdView(itemView.context)

            val adLayoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            adLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
            container.addView(banner, adLayoutParams)
            container.setBackgroundColor(Color.BLACK)

            banner.renderAd(ad, this)
        }
    }

    override fun onAdLoaded() {
        mListener.displayLogs()
        Logger.d(TAG, "onAdLoaded")
    }

    override fun onAdLoadFailed(error: Throwable?) {
        mListener.displayLogs()
        Logger.e(TAG, "onAdLoadFailed", error)
    }

    override fun onAdImpression() {
        Logger.d(TAG, "onAdImpression")
    }

    override fun onAdClick() {
        Logger.d(TAG, "onAdClick")
    }
}