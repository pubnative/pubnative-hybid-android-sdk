package net.pubnative.lite.demo.ui.viewholders

import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.sdk.utils.Logger
import net.pubnative.lite.sdk.views.HyBidBannerAdView
import net.pubnative.lite.sdk.views.PNAdView

class SignalDataBannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), PNAdView.Listener {
    private val TAG = SignalDataBannerViewHolder::class.java.simpleName

    fun bind(signalData: String) {
        if (!TextUtils.isEmpty(signalData)) {
            val container = itemView.findViewById<FrameLayout>(R.id.banner_container)
            container.removeAllViews()

            val banner = HyBidBannerAdView(itemView.context)

            val adLayoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            adLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
            container.addView(banner, adLayoutParams)
            container.setBackgroundColor(Color.BLACK)

            banner.renderAd(signalData, this)
        }
    }

    override fun onAdLoaded() {
        Logger.d(TAG, "onAdLoaded")
    }

    override fun onAdLoadFailed(error: Throwable?) {
        Logger.e(TAG, "onAdLoadFailed", error)
    }

    override fun onAdImpression() {
        Logger.d(TAG, "onAdImpression")
    }

    override fun onAdClick() {
        Logger.d(TAG, "onAdClick")
    }
}