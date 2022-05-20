package net.pubnative.lite.demo.ui.viewholders

import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.sdk.models.AdSize
import net.pubnative.lite.sdk.utils.Logger
import net.pubnative.lite.sdk.views.HyBidAdView
import net.pubnative.lite.sdk.views.PNAdView

class MarkupMRectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), PNAdView.Listener {
    private val TAG = MarkupBannerViewHolder::class.java.simpleName

    fun bind(markup: String) {
        if (!TextUtils.isEmpty(markup)) {
            val container = itemView.findViewById<FrameLayout>(R.id.mrect_container)
            container.removeAllViews()

            val mRect = HyBidAdView(itemView.context, AdSize.SIZE_300x250)

            val adLayoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            adLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
            container.addView(mRect, adLayoutParams)
            container.setBackgroundColor(Color.BLACK)

            mRect.renderCustomMarkup(markup, this)
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