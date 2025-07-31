// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.viewholders

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.adapters.OnExpandedAdCloseListener
import net.pubnative.lite.demo.ui.adapters.OnLogDisplayListener
import net.pubnative.lite.sdk.models.Ad
import net.pubnative.lite.sdk.mraid.MRAIDView
import net.pubnative.lite.sdk.mraid.MRAIDViewListener
import net.pubnative.lite.sdk.utils.Logger
import net.pubnative.lite.sdk.views.HyBidBannerAdView
import net.pubnative.lite.sdk.views.PNAdView

class MarkupBannerViewHolder(
    itemView: View,
    private var mListener: OnLogDisplayListener,
    private var onExpandedAdCloseListener: OnExpandedAdCloseListener
) :
    RecyclerView.ViewHolder(itemView), PNAdView.Listener, MRAIDViewListener {
    private var banner: HyBidBannerAdView? = null
    private val TAG = MarkupBannerViewHolder::class.java.simpleName

    fun bind(markup: String) {
        if (markup.isNotEmpty()) {
            val container = itemView.findViewById<FrameLayout>(R.id.banner_container)
            container.removeAllViews()

            banner = HyBidBannerAdView(itemView.context)

            val adLayoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            adLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
            container.addView(banner, adLayoutParams)
            container.setBackgroundColor(Color.BLACK)
            banner?.setMraidListener(this)
            banner?.renderCustomMarkup(markup, this)
        }
    }

    fun bind(ad: Ad?) {
        if (ad != null) {
            val container = itemView.findViewById<FrameLayout>(R.id.banner_container)
            container.removeAllViews()

            banner = HyBidBannerAdView(itemView.context)

            val adLayoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            adLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
            container.addView(banner, adLayoutParams)
            container.setBackgroundColor(Color.BLACK)
            banner?.setMraidListener(this)
            banner?.renderAd(ad, this)
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

    override fun onExpandedAdClosed() {
        onExpandedAdCloseListener.onExpandedAdClosed()
    }

    override fun onReplayClicked() {

    }

    override fun mraidViewLoaded(mraidView: MRAIDView?) {
    }

    override fun mraidViewError(mraidView: MRAIDView?) {
    }

    override fun mraidViewExpand(mraidView: MRAIDView?) {
        Logger.d(TAG, "mraidViewExpand")
    }

    override fun mraidViewClose(mraidView: MRAIDView?) {
    }

    override fun mraidViewResize(
        mraidView: MRAIDView?,
        width: Int,
        height: Int,
        offsetX: Int,
        offsetY: Int
    ): Boolean {
        return false
    }

    override fun mraidShowCloseButton() {
    }
}