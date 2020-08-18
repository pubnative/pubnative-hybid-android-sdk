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
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.views.HyBidBannerAdView
import net.pubnative.lite.sdk.views.PNAdView

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class HyBidBannerFragment : Fragment(), PNAdView.Listener {
    val TAG = HyBidBannerFragment::class.java.simpleName

    private var zoneId: String? = null

    private lateinit var hybidBanner: HyBidBannerAdView
    private lateinit var loadButton: Button
    private lateinit var errorView: TextView
    private lateinit var creativeIdView: TextView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_hybid_banner, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        creativeIdView = view.findViewById(R.id.view_creative_id)
        loadButton = view.findViewById(R.id.button_load)
        hybidBanner = view.findViewById(R.id.hybid_banner)

        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)

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
        hybidBanner.destroy()
        super.onDestroy()
    }

    fun loadPNAd() {
        //hybidBanner.load(zoneId, this)

        val htmlString = "{\"status\":\"ok\",\"ads\":[{\"assetgroupid\":10,\"assets\":[{\"type\":\"htmlbanner\",\"data\":{\"w\":320,\"h\":50,\"html\":\"<script class=\\\"pn-ad-tag\\\" type=\\\"text\\/javascript\\\">(function(beacons,trackerURL,options){var delay=1,passed=0,fired=false,readyBinded=false,viewableBinded=false,deepcut=false;deepcut=Math.random()<.001;function fire(url){(new Image).src=url;return true}function track(msg){if(!trackerURL)return;if(!deepcut)return;fire(trackerURL+encodeURIComponent(msg))}function fireAll(){if(fired)return;fired=true;for(var i=0;i<beacons.length;i++)fire(beacons[i]);track(\\\"imp P\\\"+boolToChar(options.isHTML5)+boolToChar(options.isPlMRAID)+boolToChar(options.isMRAID)+boolToChar(typeof mraid===\\r\\n    \\\"object\\\")+boolToChar(window.top===window))}function boolToChar(val){if(typeof val===\\\"undefined\\\")return\\\"N\\\";return val?\\\"1\\\":\\\"0\\\"}track(\\\"inf P\\\"+boolToChar(options.isHTML5)+boolToChar(options.isPlMRAID)+boolToChar(options.isMRAID)+boolToChar(typeof mraid===\\\"object\\\")+boolToChar(window.top===window));window.addEventListener(\\\"error\\\",function(event){track(\\\"er2 WIND \\\"+event.message);trackDbg()});fireAll()})([],\\\"https:\\/\\/got.pubnative.net\\/imp\\/error?t=SztqG1bLVqjtoBjtKaluxZUgWi3D4PNJOTE-QOJ9X2HMBw4ky6t_dwAPOEhUNMkCpMWdBo9RZz0IDAVQKQRUbn_P1ggpT0OuYpUcOiq4wmJf8WkQUheodm1LRepExz9SsQ5E1X_sYPMh-Ow&msg=\\\", {\\\"pubAppID\\\":1036637,\\\"dspID\\\":64,\\\"impID\\\":\\\"79cdbe26-a560-4b82-b5a1-ff7fe221c5ad\\\",\\\"isMRAID\\\":false,\\\"isHTML5\\\":false});<\\/script>\\r\\n<a target=\\\"_blank\\\" href=\\\"https:\\/\\/play.google.com\\/store\\/apps\\/details?id=net.pubnative.easysound\\\"><img src=\\\"https:\\/\\/cdn.pubnative.net\\/widget\\/v3\\/assets\\/easysound_320x50.jpg\\\" width=\\\"320\\\" height=\\\"50\\\" border=\\\"0\\\" alt=\\\"Advertisement\\\" \\/><\\/a>\"}}],\"meta\":[{\"type\":\"points\",\"data\":{\"number\":23}},{\"type\":\"revenuemodel\",\"data\":{\"text\":\"cpm\"}},{\"type\":\"contentinfo\",\"data\":{\"link\":\"https:\\/\\/pubnative.net\\/content-info\",\"icon\":\"https:\\/\\/cdn.pubnative.net\\/static\\/adserver\\/contentinfo.png\",\"text\":\"Learn about this ad\"}},{\"type\":\"creativeid\",\"data\":{\"text\":\"test_creative\"}}],\"beacons\":[{\"type\":\"impression\",\"data\":{\"url\":\"https:\\/\\/backend.europe-west4gcp0.pubnative.net\\/mockdsp\\/v1\\/tracker\\/nurl?app_id=1036637&p=0.033222222\"}},{\"type\":\"impression\",\"data\":{\"url\":\"https:\\/\\/got.pubnative.net\\/impression?aid=1036637&t=bmiF5j-y2RZhXCZABagvj-xk6fT6YmQoaEMgmMu-wKGabQc44Sv1lJ3VnSrBZ3Sou5FewJy7t4M0TQyY8YaTlmiMfJ-A1tuvXD-oX2B7Pyyl72Ls1h73LEY1uD7wTEB-v_5D9RSgp3-CLB-yAUMRYmnXGIjUs5bh1ZFpHLeCI2YGLbPpqKDn25CBTrbJ_aNGPb_qw9_9Di0nWHaIqea7swR1oW1ar-swlOlGzBgObLa67p5hIuLbj8_biRu8fGcmzKyCVrNcK5KxaRzpOnlvgAiVXt3DMzN5RBmnRlY84bhMeDhU2mjGxXmMgENdQe-R_FyJYLNzxCeulxL0zGYIfsYrmQihq2WC4Aof5KfJnEsJM1_KxOAwPi9rfehh46qKgoIOiNn5O_nqQEa_b0ONPsBy2pZl3s_TGmc7mM26F-lI3w7MOPBuxfUpVssZE6AoFiwJlVPT2DcEb9lq0aJgmIPSpMMtspJ3-SwoOtDzRsud6y9D4OfJATjflWhXWbGojqYiTdOH_Px27xYQe_V8kxVhbcYurO_jKCOPfLe2nS9Ge7vnbfT-i1Nif8PNoXl7LaSigOGHehPMhLrfFPu4P0bCyRHTXAp5pX6lyzzUz_jORtQ-Nl_EXmS6ePBZbYvWtxLp-O2svgYtB7eI0zWPMsuBc8naKYRF6V99YWxoDHKhiMSAQBbcbhYrnrFfWJi5XNV-v4YVgplrhDGGHQ-9UGyLE52dgY_md0udVTUKGP9AbVzLCIPlTaRbVbUJukQGRAqzoATKFCovcG4GVLB_mpMNF2Gg-cPaNZm25f_SJxNqlOfkcX-DaI2zBo_BZaBfVS7zeQAassnBbQwM7WXT7k8G9P-0CZ5dIZhWUnHuMLhuQtypAOzdJJOdKDWWU8r1RVvy81j7ZPJ7dcMn6kivFegnDlKA8U5M5lKv42_Me5j9cjgelP9OBLU\"}},{\"type\":\"click\",\"data\":{\"url\":\"https:\\/\\/got.pubnative.net\\/click\\/rtb?aid=1036637&t=gEwv-mkxc0bPd5gAIetXiCFTw3wMrsC2mBVWAToQctSLlerCqkoL0_xA8xFizCM1-brxNiAFVSdh10JMN9DKdNeF1y_3gzv9OiCmGiTpClyFgubDiQ62-0hpx8uxl0o165NFtztzN7blkfJJdMingKayJratVbjXf3Xgtdks00R6IbGGGTafAiSJyvYVjBhO0_nK46ElAP1_ppxfBu8mKCATY1XmwbCT8r31SpPSAXDU23XZ5FcGZw4agUHGtwe_J6kHQHtGwNTHnwmF3aEF-G4-Moux-L0S1yaruLQwmBSR1SoJc7S1RsDyZkY1xPaHc7QyG712cmIIFTGJl3OO4QNQNer_gzbURCIlqRC32r2fALmRZ4J1xoOB8LikF66R7DW6QhOt7qm_6IFdNZ32w9Ee0kkHLZOwxCiURI1GNB0sG2SK7OKWT8PLQcyyWcJVzeAc8NWpkcs0qPPYKvb8OxzjvFftr3tNiB-QB7jxecdcy1sPEqW65otLiRwLFOftSCRMYvixVEh8gM9Ke37u2x-U_YW42OGWzpa0ebyZFNFgdlOv4nEl-BV3nmFiML26Ha_R9VT3F2GOvXQQlr6jpU8ijVvaew4DHyu54bUIwg2Olxt1mYHDxgfcKPykTH9u7ij2i4wW7hkaVhVBjOA4FGZsKscJkvQcN8Kj4eSM-SmOBpWCOlMvvDlvBNrlfiU\"}}]}]}"

        hybidBanner.renderAd(htmlString)
        onAdLoaded()
    }

    // --------------- PNAdView Listener --------------------
    override fun onAdLoaded() {
        Log.d(TAG, "onAdLoaded")
        displayLogs()
        if (!TextUtils.isEmpty(hybidBanner.creativeId)) {
            creativeIdView.text = hybidBanner.creativeId
        }
    }

    override fun onAdLoadFailed(error: Throwable?) {
        Log.e(TAG, "onAdLoadFailed", error)
        errorView.text = error?.message
        displayLogs()
    }

    override fun onAdImpression() {
        Log.d(TAG, "onAdImpression")
    }

    override fun onAdClick() {
        Log.d(TAG, "onAdClick")
    }

    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }
}