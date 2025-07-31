// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.viewholders

import android.view.View
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.adapters.OnLogDisplayListener
import net.pubnative.lite.sdk.views.HyBidAdView
import net.pubnative.lite.sdk.views.HyBidLeaderboardAdView

class LegacyApiLeaderboardViewHolder(itemView: View, listener: OnLogDisplayListener) :
    BaseLegacyApiViewHolder(itemView, listener, R.id.leaderboard_container, "LeaderboardViewHolder") {

    override fun createHyBidAdView(itemView: View): HyBidAdView {
        return HyBidLeaderboardAdView(itemView.context)
    }
}