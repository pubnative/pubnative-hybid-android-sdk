package net.pubnative.lite.demo.ui.viewholders

import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.sdk.views.HyBidMRectAdView
import net.pubnative.lite.sdk.views.PNAdView

class HyBidMRectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), PNAdView.Listener {
    private val TAG = HyBidMRectViewHolder::class.java.simpleName

    private val adView: HyBidMRectAdView = itemView.findViewById(R.id.mrect_view)

    fun bind(zoneId: String) {
        if (!TextUtils.isEmpty(zoneId)) {
            adView.load(zoneId, this)
        }
    }

    override fun onAdLoaded() {
        adView.visibility = View.VISIBLE
        Log.d(TAG, "onAdLoaded")
    }

    override fun onAdLoadFailed(error: Throwable?) {
        Log.e(TAG, error?.message)
    }

    override fun onAdImpression() {
        Log.d(TAG, "onAdImpression")
    }

    override fun onAdClick() {
        Log.d(TAG, "onAdClick")
    }
}