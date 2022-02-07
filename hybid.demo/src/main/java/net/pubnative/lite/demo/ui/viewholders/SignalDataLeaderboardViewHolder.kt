package net.pubnative.lite.demo.ui.viewholders

import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.sdk.models.AdSize
import net.pubnative.lite.sdk.utils.Logger
import net.pubnative.lite.sdk.views.HyBidAdView
import net.pubnative.lite.sdk.views.HyBidLeaderboardAdView
import net.pubnative.lite.sdk.views.PNAdView

class SignalDataLeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), PNAdView.Listener {
    private val TAG = SignalDataLeaderboardViewHolder::class.java.simpleName

    private var adView: HyBidAdView? = null

    fun bind(signalData: String) {
        if (!TextUtils.isEmpty(signalData)) {
            val container = itemView.findViewById<FrameLayout>(R.id.leaderboard_container)
            container.removeAllViews()

            adView = HyBidAdView(itemView.context, AdSize.SIZE_728x90)

            val adLayoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            adLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
            container.addView(adView, adLayoutParams)
            container.setBackgroundColor(Color.BLACK)

            adView?.renderAd(signalData, this)
        }
    }

    fun destroy() {
        adView?.destroy()
    }

    override fun onAdLoaded() {
        Logger.d(TAG, "onAdLoaded")
    }

    override fun onAdLoadFailed(error: Throwable?) {
        Logger.e(TAG, "onAdLoadFailed", error)
        Toast.makeText(itemView.context, error?.message, Toast.LENGTH_LONG).show()
    }

    override fun onAdImpression() {
        Logger.d(TAG, "onAdImpression")
    }

    override fun onAdClick() {
        Logger.d(TAG, "onAdClick")
    }
}