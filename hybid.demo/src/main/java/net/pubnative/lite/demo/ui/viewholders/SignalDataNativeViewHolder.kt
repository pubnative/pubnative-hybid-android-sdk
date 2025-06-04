// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.viewholders

import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import net.pubnative.lite.demo.R
import net.pubnative.lite.sdk.models.NativeAd
import net.pubnative.lite.sdk.request.HyBidNativeAdRequest
import net.pubnative.lite.sdk.utils.Logger

class SignalDataNativeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    HyBidNativeAdRequest.RequestListener, NativeAd.Listener {
    private val TAG = SignalDataNativeViewHolder::class.java.simpleName

    private var nativeAd: NativeAd? = null

    fun bind(signalData: String) {
        if (!TextUtils.isEmpty(signalData)) {
            nativeAd?.stopTracking()
            nativeAd = null

            val nativeRequest = HyBidNativeAdRequest()
            nativeRequest.setPreLoadMediaAssets(true)
            nativeRequest.prepareAd(signalData, this)
        }
    }

    fun destroy() {
        nativeAd?.stopTracking()
    }

    override fun onRequestSuccess(ad: NativeAd?) {
        Logger.d(TAG, "onRequestSuccess")
        nativeAd = ad
        renderAd(ad)
    }

    override fun onRequestFail(throwable: Throwable?) {
        Logger.e(TAG, "onRequestFail", throwable)
        Toast.makeText(itemView.context, throwable?.message, Toast.LENGTH_LONG).show()
    }

    override fun onAdImpression(ad: NativeAd?, view: View?) {
        Logger.d(TAG, "onAdImpression")
    }

    override fun onAdClick(ad: NativeAd?, view: View?) {
        Logger.d(TAG, "onAdClick")
    }

    private fun renderAd(nativeAd: NativeAd?) {
        nativeAd?.let { ad ->
            val container = itemView.findViewById<RelativeLayout>(R.id.native_container)
            val adIcon = itemView.findViewById<ImageView>(R.id.ad_icon)
            val adBanner = itemView.findViewById<ImageView>(R.id.ad_banner)
            val adTitle = itemView.findViewById<TextView>(R.id.ad_title)
            val adDescription = itemView.findViewById<TextView>(R.id.ad_description)
            val adChoices = itemView.findViewById<FrameLayout>(R.id.ad_choices)
            val adCallToAction = itemView.findViewById<Button>(R.id.ad_call_to_action)
            val adRating = itemView.findViewById<RatingBar>(R.id.ad_rating)

            adTitle.text = ad.title
            adDescription.text = ad.description
            adCallToAction.text = ad.callToActionText
            itemView.context?.let {
                adChoices.addView(ad.getContentInfo(itemView.context))
            }
            val rating = ad.rating.toFloat()
            adRating.rating = rating

            if (ad.bannerBitmap != null) {
                adBanner.setImageBitmap(ad.bannerBitmap)
            } else {
                Picasso.get().load(ad.bannerUrl).into(adBanner)
            }

            if (ad.iconBitmap != null) {
                adIcon.setImageBitmap(ad.iconBitmap)
            } else {
                Picasso.get().load(ad.iconUrl).into(adIcon)
            }

            ad.startTracking(container, this)
        }
    }
}