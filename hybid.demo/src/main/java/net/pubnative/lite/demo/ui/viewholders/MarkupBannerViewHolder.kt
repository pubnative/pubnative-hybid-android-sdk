package net.pubnative.lite.demo.ui.viewholders

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import net.pubnative.lite.demo.R
import net.pubnative.lite.sdk.mraid.*
import net.pubnative.lite.sdk.utils.Logger
import net.pubnative.lite.sdk.utils.UrlHandler

class MarkupBannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), MRAIDViewListener, MRAIDNativeFeatureListener {
    private val TAG = MarkupBannerViewHolder::class.java.simpleName

    private val urlHandler: UrlHandler = UrlHandler(itemView.context)

    fun bind(markup: String) {
        if (!TextUtils.isEmpty(markup)) {
            val container = itemView.findViewById<FrameLayout>(R.id.banner_container)
            container.removeAllViews()

            val supportedFeatures = arrayOf(
                    MRAIDNativeFeature.INLINE_VIDEO,
                    MRAIDNativeFeature.CALENDAR,
                    MRAIDNativeFeature.SMS,
                    MRAIDNativeFeature.STORE_PICTURE,
                    MRAIDNativeFeature.TEL
            )

            val emptyContentInfo = FrameLayout(itemView.context)

            val banner = MRAIDBanner(itemView.context,
                    "",
                    markup,
                    supportedFeatures,
                    this,
                    this,
                    emptyContentInfo)

            val adLayoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            adLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
            container.addView(banner, adLayoutParams)
            container.setBackgroundColor(Color.BLACK)
        }
    }

    override fun mraidViewLoaded(mraidView: MRAIDView?) {
        Logger.d(TAG, "mraidViewLoaded")
    }

    override fun mraidViewExpand(mraidView: MRAIDView?) {
        Logger.d(TAG, "mraidViewExpand")
    }

    override fun mraidViewResize(mraidView: MRAIDView?, width: Int, height: Int, offsetX: Int, offsetY: Int): Boolean {
        Logger.d(TAG, "mraidViewResize")
        return true
    }

    override fun mraidViewClose(mraidView: MRAIDView?) {
        Logger.d(TAG, "mraidViewClose")
    }

    override fun mraidNativeFeatureCallTel(url: String?) {
        Logger.d(TAG, "mraidNativeFeatureCallTel")
    }

    override fun mraidNativeFeatureCreateCalendarEvent(eventJSON: String?) {
        Logger.d(TAG, "mraidNativeFeatureCreateCalendarEvent")
    }

    override fun mraidNativeFeatureOpenBrowser(url: String?) {
        Logger.d(TAG, "mraidNativeFeatureOpenBrowser")
        urlHandler.handleUrl(url)
    }

    override fun mraidNativeFeaturePlayVideo(url: String?) {
        Logger.d(TAG, "mraidNativeFeaturePlayVideo")
    }

    override fun mraidNativeFeatureSendSms(url: String?) {
        Logger.d(TAG, "mraidNativeFeatureSendSms")
    }

    override fun mraidNativeFeatureStorePicture(url: String?) {
        Logger.d(TAG, "mraidNativeFeatureStorePicture")
    }
}