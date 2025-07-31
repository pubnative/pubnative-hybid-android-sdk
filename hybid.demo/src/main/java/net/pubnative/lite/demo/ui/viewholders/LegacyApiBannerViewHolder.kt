// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.viewholders

import android.view.View
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.adapters.OnLogDisplayListener
import net.pubnative.lite.sdk.views.HyBidAdView
import net.pubnative.lite.sdk.views.HyBidBannerAdView

class LegacyApiBannerViewHolder(itemView: View, listener: OnLogDisplayListener) :
    BaseLegacyApiViewHolder(itemView, listener, R.id.banner_container, "BannerViewHolder") {

    override fun createHyBidAdView(itemView: View): HyBidAdView {
        return HyBidBannerAdView(itemView.context)
    }
}