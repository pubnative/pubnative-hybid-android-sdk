// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.viewholders

import android.view.View
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.adapters.OnLogDisplayListener
import net.pubnative.lite.sdk.models.AdSize
import net.pubnative.lite.sdk.views.HyBidAdView

class LegacyApiMRectViewHolder(itemView: View, listener: OnLogDisplayListener) :
    BaseLegacyApiViewHolder(itemView, listener, R.id.mrect_container, "MRectViewHolder") {

    override fun createHyBidAdView(itemView: View): HyBidAdView {
        return HyBidAdView(itemView.context, AdSize.SIZE_300x250)
    }
}