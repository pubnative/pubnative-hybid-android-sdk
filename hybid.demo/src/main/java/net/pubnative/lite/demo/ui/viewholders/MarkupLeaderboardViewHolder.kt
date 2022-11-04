package net.pubnative.lite.demo.ui.viewholders

import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.adapters.OnLogDisplayListener
import net.pubnative.lite.sdk.utils.Logger
import net.pubnative.lite.sdk.views.HyBidLeaderboardAdView
import net.pubnative.lite.sdk.views.PNAdView

class MarkupLeaderboardViewHolder(itemView: View, var mListener: OnLogDisplayListener) :
    RecyclerView.ViewHolder(itemView), PNAdView.Listener {
    private val TAG = MarkupLeaderboardViewHolder::class.java.simpleName

    fun bind(markup: String) {
        if (!TextUtils.isEmpty(markup)) {
            val container = itemView.findViewById<FrameLayout>(R.id.leaderboard_container)
            container.removeAllViews()

            val leaderboard = HyBidLeaderboardAdView(itemView.context)

            val adLayoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            adLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
            container.addView(leaderboard, adLayoutParams)
            container.setBackgroundColor(Color.BLACK)

            leaderboard.renderCustomMarkup(markup, this)
        }
    }

    override fun onAdLoaded() {
        Logger.d(TAG, "onAdLoaded")
        mListener.displayLogs()
    }

    override fun onAdLoadFailed(error: Throwable?) {
        Logger.e(TAG, "onAdLoadFailed", error)
        mListener.displayLogs()
    }

    override fun onAdImpression() {
        Logger.d(TAG, "onAdImpression")
    }

    override fun onAdClick() {
        Logger.d(TAG, "onAdClick")
    }
}