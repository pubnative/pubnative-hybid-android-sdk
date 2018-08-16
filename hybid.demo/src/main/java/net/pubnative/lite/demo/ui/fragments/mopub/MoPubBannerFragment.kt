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
package net.pubnative.lite.demo.ui.fragments.mopub

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.mopub.mobileads.MoPubErrorCode
import com.mopub.mobileads.MoPubView
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.demo.util.JsonUtils
import net.pubnative.lite.sdk.api.BannerRequestManager
import net.pubnative.lite.sdk.api.RequestManager
import net.pubnative.lite.sdk.models.Ad
import net.pubnative.lite.sdk.utils.AdRequestRegistry
import net.pubnative.lite.sdk.utils.PrebidUtils

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class MoPubBannerFragment : Fragment(), RequestManager.RequestListener, MoPubView.BannerAdListener {
    val TAG = MoPubBannerFragment::class.java.simpleName

    private lateinit var requestManager: RequestManager
    private var zoneId: String? = null
    private var adUnitId: String? = null

    private lateinit var mopubBanner: MoPubView
    private lateinit var loadButton: Button
    private lateinit var errorView: TextView
    private lateinit var requestView: TextView
    private lateinit var latencyView: TextView
    private lateinit var responseView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_mopub_banner, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        requestView = view.findViewById(R.id.view_request_url)
        latencyView = view.findViewById(R.id.view_latency)
        responseView = view.findViewById(R.id.view_response)
        loadButton = view.findViewById(R.id.button_load)
        mopubBanner = view.findViewById(R.id.mopub_banner)
        mopubBanner.bannerAdListener = this
        mopubBanner.autorefreshEnabled = false

        requestManager = BannerRequestManager()

        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)
        adUnitId = SettingsManager.getInstance(activity!!).getSettings().mopubBannerAdUnitId

        loadButton.setOnClickListener {
            errorView.text = ""
            requestView.text = ""
            latencyView.text = ""
            responseView.text = ""
            loadPNAd()
        }

        requestView.setOnClickListener { ClipboardUtils.copyToClipboard(activity!!, requestView.text.toString()) }
        latencyView.setOnClickListener { ClipboardUtils.copyToClipboard(activity!!, latencyView.text.toString()) }
        responseView.setOnClickListener { ClipboardUtils.copyToClipboard(activity!!, responseView.text.toString()) }
        errorView.setOnClickListener { ClipboardUtils.copyToClipboard(activity!!, errorView.text.toString()) }
    }

    override fun onDestroy() {
        super.onDestroy()
        mopubBanner.destroy()
    }

    fun loadPNAd() {
        requestManager.setZoneId(zoneId)
        requestManager.setRequestListener(this)
        requestManager.requestAd()
    }

    // --------------- HyBid Request Listener --------------------
    override fun onRequestSuccess(ad: Ad?) {
        mopubBanner.adUnitId = adUnitId
        mopubBanner.keywords = PrebidUtils.getPrebidKeywords(ad, zoneId)
        mopubBanner.loadAd()

        Log.d(TAG, "onRequestSuccess")
        displayLogs()
    }

    override fun onRequestFail(throwable: Throwable?) {
        Log.d(TAG, "onRequestFail: ", throwable)
        errorView.text = throwable?.message
        displayLogs()
    }

    // ---------------- MoPub Banner Listener ---------------------
    override fun onBannerLoaded(banner: MoPubView?) {
        Log.d(TAG, "onBannerLoaded")
    }

    override fun onBannerFailed(banner: MoPubView?, errorCode: MoPubErrorCode?) {
        Log.d(TAG, "onBannerFailed")
    }

    override fun onBannerExpanded(banner: MoPubView?) {
        Log.d(TAG, "onBannerExpanded")
    }

    override fun onBannerCollapsed(banner: MoPubView?) {
        Log.d(TAG, "onBannerCollapsed")
    }

    override fun onBannerClicked(banner: MoPubView?) {
        Log.d(TAG, "onBannerClicked")
    }

    private fun displayLogs() {
        val registryItem = AdRequestRegistry.getInstance().lastAdRequest
        if (registryItem != null) {
            requestView.text = registryItem.url
            latencyView.text = registryItem.latency.toString()
            if (!TextUtils.isEmpty(registryItem.response)) {
                responseView.text = JsonUtils.toFormattedJson(registryItem.response)
            }
        }
    }
}