// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.viewholders

import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.adapters.OnLogDisplayListener
import net.pubnative.lite.sdk.models.Ad
import net.pubnative.lite.sdk.models.NativeAd
import net.pubnative.lite.sdk.utils.Logger

class LegacyApiNativeViewHolder(itemView: View, val mListener: OnLogDisplayListener) :
    RecyclerView.ViewHolder(itemView), NativeAd.Listener {
    private val TAG = LegacyApiNativeViewHolder::class.java.simpleName

    private var nativeAd: NativeAd? = null

    fun bind(ad: Ad?) {
        ad?.let {
            destroy()
            try {
                nativeAd = NativeAd(ad)
                renderAd(nativeAd)
            } catch (e: Exception) {
                mListener.displayLogs()
            }
        }
    }

    fun destroy() {
        nativeAd?.stopTracking()
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