package net.pubnative.lite.demo.ui.viewholders

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.ui.adapters.OnLogDisplayListener
import net.pubnative.lite.demo.util.Destroyable
import net.pubnative.lite.sdk.models.Ad
import net.pubnative.lite.sdk.utils.Logger
import net.pubnative.lite.sdk.views.HyBidAdView
import net.pubnative.lite.sdk.views.PNAdView

abstract class BaseLegacyApiViewHolder(
    itemView: View,
    protected val listener: OnLogDisplayListener,
    private val containerId: Int,
    private val tagPrefix: String
) : RecyclerView.ViewHolder(itemView), PNAdView.Listener, Destroyable {

    private lateinit var hyBidAdView: HyBidAdView
    private val TAG: String
        get() = "$tagPrefix${this::class.java.simpleName}"

    protected abstract fun createHyBidAdView(itemView: View): HyBidAdView

    fun bind(ad: Ad?) {
        ad?.let {
            val container = itemView.findViewById<FrameLayout>(containerId)
            container.removeAllViews()

            destroy()

            hyBidAdView = createHyBidAdView(itemView)

            val adLayoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            adLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
            container.addView(hyBidAdView, adLayoutParams)
            container.setBackgroundColor(Color.BLACK)

            hyBidAdView.renderAd(ad, this)
        }
    }

    override fun destroy() {
        if (this::hyBidAdView.isInitialized) {
            hyBidAdView.destroy()
        }
    }

    override fun onAdLoaded() {
        Logger.d(TAG, "onAdLoaded")
        listener.displayLogs()
    }

    override fun onAdLoadFailed(error: Throwable?) {
        Logger.e(TAG, "onAdLoadFailed", error)
        listener.displayLogs()
    }

    override fun onAdImpression() {
        Logger.d(TAG, "onAdImpression")
    }

    override fun onAdClick() {
        Logger.d(TAG, "onAdClick")
    }
}