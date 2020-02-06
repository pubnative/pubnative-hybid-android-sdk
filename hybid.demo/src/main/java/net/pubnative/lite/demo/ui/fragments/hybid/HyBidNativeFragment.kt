// The MIT License (MIT)
//
// Copyright (c) 2018 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package net.pubnative.lite.demo.ui.fragments.hybid

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.models.NativeAd
import net.pubnative.lite.sdk.nativead.HyBidNativeAdRequest

class HyBidNativeFragment : Fragment(), HyBidNativeAdRequest.RequestListener, NativeAd.Listener {
    val TAG = HyBidNativeFragment::class.java.simpleName

    private var zoneId: String? = null

    private lateinit var adContainer: ViewGroup
    private lateinit var adIcon: ImageView
    private lateinit var adBanner: ImageView
    private lateinit var adTitle: TextView
    private lateinit var adDescription: TextView
    private lateinit var adChoices: FrameLayout
    private lateinit var adCallToAction: Button
    private lateinit var adRating: RatingBar

    private lateinit var loadButton: Button
    private lateinit var errorView: TextView
    private lateinit var creativeIdView: TextView

    private var nativeAd: NativeAd? = null
    private var nativeAdRequest: HyBidNativeAdRequest? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_hybid_native, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        creativeIdView = view.findViewById(R.id.view_creative_id)

        loadButton = view.findViewById(R.id.button_load)

        adContainer = view.findViewById(R.id.ad_container)
        adIcon = view.findViewById(R.id.ad_icon)
        adBanner = view.findViewById(R.id.ad_banner)
        adTitle = view.findViewById(R.id.ad_title)
        adDescription = view.findViewById(R.id.ad_description)
        adChoices = view.findViewById(R.id.ad_choices)
        adCallToAction = view.findViewById(R.id.ad_call_to_action)
        adRating = view.findViewById(R.id.ad_rating)

        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)

        nativeAdRequest = HyBidNativeAdRequest()

        loadButton.setOnClickListener {
            errorView.text = ""
            val activity = activity as TabActivity
            activity.notifyAdCleaned()
            loadPNAd()
        }

        errorView.setOnClickListener { ClipboardUtils.copyToClipboard(activity!!, errorView.text.toString()) }
        creativeIdView.setOnClickListener { ClipboardUtils.copyToClipboard(activity!!, creativeIdView.text.toString()) }
    }

    override fun onDestroy() {
        nativeAd?.stopTracking()
        super.onDestroy()
    }

    fun loadPNAd() {
        nativeAdRequest?.load(zoneId, this)
    }

    fun renderAd(ad: NativeAd?) {
        nativeAd = ad
        adTitle.text = ad?.title
        adDescription.text = ad?.description
        adCallToAction.text = ad?.callToActionText
        adChoices.addView(ad?.getContentInfo(context))

        val rating = ad?.rating?.toFloat()
        adRating.rating = rating!!

        Picasso.get().load(ad.bannerUrl).into(adBanner)
        Picasso.get().load(ad.iconUrl).into(adIcon)

        ad.startTracking(adContainer, this)
    }

    override fun onRequestSuccess(ad: NativeAd?) {
        renderAd(ad)
        displayLogs()
        if (!TextUtils.isEmpty(ad?.creativeId)) {
            creativeIdView.text = ad?.creativeId
        }
    }

    override fun onRequestFail(throwable: Throwable?) {
        Log.e(TAG, "onAdLoadFailed", throwable)
        errorView.text = throwable?.message
        displayLogs()
    }

    override fun onAdImpression(PNAPIAdModel: NativeAd?, view: View?) {
        Log.d(TAG, "onAdImpression")
    }

    override fun onAdClick(PNAPIAdModel: NativeAd?, view: View?) {
        Log.d(TAG, "onAdClick")
    }

    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }
}