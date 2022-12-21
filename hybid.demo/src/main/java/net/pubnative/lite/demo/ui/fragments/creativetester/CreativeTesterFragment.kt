package net.pubnative.lite.demo.ui.fragments.creativetester

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd
import net.pubnative.lite.sdk.models.AdSize
import net.pubnative.lite.sdk.network.PNHttpClient
import net.pubnative.lite.sdk.utils.AdRequestRegistry
import net.pubnative.lite.sdk.utils.Logger
import net.pubnative.lite.sdk.views.HyBidAdView
import net.pubnative.lite.sdk.views.HyBidBannerAdView
import net.pubnative.lite.sdk.views.HyBidLeaderboardAdView
import net.pubnative.lite.sdk.views.HyBidMRectAdView


class CreativeTesterFragment : Fragment(R.layout.fragment_creative_tester), HyBidAdView.Listener {
    private val TAG = CreativeTesterFragment::class.java.simpleName

    private lateinit var creativeIdInput: EditText
    private lateinit var adSizeGroup: RadioGroup
    private lateinit var serverGroup: RadioGroup
    private lateinit var bannerAdView: HyBidBannerAdView
    private lateinit var mrectAdView: HyBidMRectAdView
    private lateinit var leaderboardAdView: HyBidLeaderboardAdView

    private var selectedSize: Int = R.id.radio_size_banner
    private var selectedServer: Int = R.id.radio_server_p161

    private var interstitial: HyBidInterstitialAd? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        creativeIdInput = view.findViewById(R.id.input_creative_id)
        adSizeGroup = view.findViewById(R.id.group_ad_size)
        serverGroup = view.findViewById(R.id.group_server)
        bannerAdView = view.findViewById(R.id.banner_adview)
        mrectAdView = view.findViewById(R.id.mrect_adview)
        leaderboardAdView = view.findViewById(R.id.leaderboard_adview)

        bannerAdView.setAdSize(AdSize.SIZE_320x50)
        bannerAdView.isAutoShowOnLoad = true
        mrectAdView.setAdSize(AdSize.SIZE_300x250)
        mrectAdView.isAutoShowOnLoad = true
        leaderboardAdView.setAdSize(AdSize.SIZE_728x90)
        leaderboardAdView.isAutoShowOnLoad = true

        view.findViewById<ImageButton>(R.id.button_paste_clipboard).setOnClickListener {
            pasteFromClipboard()
        }

        view.findViewById<Button>(R.id.button_load).setOnClickListener {
            hideKeyboard(context, view)
            val activity = activity as TabActivity
            activity.notifyAdCleaned()
            activity.clearEventList()
            loadCreative()
        }

        adSizeGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedSize = checkedId
            updateListVisibility()
        }

        serverGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedServer = checkedId
        }
    }

    override fun onDestroy() {
        interstitial?.destroy()
        super.onDestroy()
    }

    private fun updateListVisibility() {
        when (selectedSize) {
            R.id.radio_size_banner -> {
                bannerAdView.visibility = View.VISIBLE
                mrectAdView.visibility = View.GONE
                leaderboardAdView.visibility = View.GONE
            }
            R.id.radio_size_medium -> {
                bannerAdView.visibility = View.GONE
                mrectAdView.visibility = View.VISIBLE
                leaderboardAdView.visibility = View.GONE
            }
            R.id.radio_size_leaderboard -> {
                bannerAdView.visibility = View.GONE
                mrectAdView.visibility = View.GONE
                leaderboardAdView.visibility = View.VISIBLE
            }
        }
    }

    private fun pasteFromClipboard() {
        val clipboardText = ClipboardUtils.copyFromClipboard(requireActivity())
        if (!TextUtils.isEmpty(clipboardText)) {
            creativeIdInput.setText(clipboardText)
        }
    }

    private fun loadCreative() {
        if (!TextUtils.isEmpty(creativeIdInput.text.toString())) {
            val creativeId = creativeIdInput.text.toString().trim()
            if (selectedServer == R.id.radio_server_p161) {
                loadP161Creative(creativeId)
            } else {
                loadFoundryCreative(creativeId)
            }
        } else {
            Toast.makeText(activity, "Please input some creative ID", Toast.LENGTH_SHORT).show()
            return
        }
    }

    private fun loadP161Creative(creativeId: String) {
        val url = "https://docker.creative-serving.com/preview?cr=$creativeId&type=adi"
        Toast.makeText(activity, "Making request to: $url", Toast.LENGTH_SHORT).show()
        val initTime = System.currentTimeMillis()
        PNHttpClient.makeRequest(context, url, null, null, true,
            object : PNHttpClient.Listener {
                override fun onSuccess(
                    response: String?,
                    headers: MutableMap<String, MutableList<String>>?
                ) {
                    Log.d(TAG, response ?: "")
                    val responseTime = System.currentTimeMillis() - initTime
                    AdRequestRegistry.getInstance().setLastAdRequest(url, response, responseTime)
                    loadMarkup(response ?: "")
                }

                override fun onFailure(error: Throwable) {
                    Log.d("onFailure", error.toString())
                    Toast.makeText(activity, "Creative request failed", Toast.LENGTH_SHORT).show()
                    val responseTime = System.currentTimeMillis() - initTime
                    AdRequestRegistry.getInstance().setLastAdRequest(url, error.message, responseTime)
                }
            })
    }

    private fun loadFoundryCreative(creativeId: String) {
        val url =
            "https://adcel-gcp.vrvm.com/banner?b=qa&p=iphn&adunit=mma&size=320x50&nwk=54&flt=1&ctg=$creativeId"
        Toast.makeText(activity, "Making request to: $url", Toast.LENGTH_SHORT).show()

        val headers: MutableMap<String, String> = HashMap()

        headers["user-agent"] =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36"
        val initTime = System.currentTimeMillis()

        PNHttpClient.makeRequest(context, url, headers, null, true,
            object : PNHttpClient.Listener {
                override fun onSuccess(
                    response: String?,
                    headers: MutableMap<String, MutableList<String>>?
                ) {
                    if (!response.isNullOrEmpty()) {
                        Log.d("onSuccess", response)
                        val result: String = response.substring(
                            response.indexOf("<![CDATA[") + 9,
                            response.indexOf("]]>")
                        )
                        Log.d("result", result)
                        val responseTime = System.currentTimeMillis() - initTime
                        AdRequestRegistry.getInstance().setLastAdRequest(url, response, responseTime)
                        loadMarkup(result)
                    } else {
                        Log.d("onSuccess", "Request succeeded with an empty response")
                        Toast.makeText(
                            activity,
                            "Request succeeded with an empty response",
                            Toast.LENGTH_SHORT
                        ).show()
                        val responseTime = System.currentTimeMillis() - initTime
                        AdRequestRegistry.getInstance().setLastAdRequest(url, "Request succeeded with an empty response", responseTime)
                    }
                }

                override fun onFailure(error: Throwable) {
                    Log.d("onFailure", error.toString())
                    Toast.makeText(activity, "Creative request failed", Toast.LENGTH_SHORT).show()
                    val responseTime = System.currentTimeMillis() - initTime
                    AdRequestRegistry.getInstance().setLastAdRequest(url, error.message, responseTime)
                }
            })
    }

    private fun loadMarkup(markup: String) {
        if (TextUtils.isEmpty(markup)) {
            Toast.makeText(activity, "The returned markup is empty or null", Toast.LENGTH_SHORT)
                .show()
        } else {
            when (selectedSize) {
                R.id.radio_size_banner -> bannerAdView.renderCustomMarkup(markup, this)
                R.id.radio_size_medium -> mrectAdView.renderCustomMarkup(markup, this)
                R.id.radio_size_leaderboard -> leaderboardAdView.renderCustomMarkup(markup, this)
                R.id.radio_size_interstitial -> loadInterstitial(markup)
            }
        }
    }

    private fun loadInterstitial(markup: String) {
        interstitial?.destroy()

        val interstitialListener = object : HyBidInterstitialAd.Listener {
            override fun onInterstitialLoaded() {
                Logger.d(TAG, "onInterstitialLoaded")
                interstitial?.show()
                displayLogs()
            }

            override fun onInterstitialLoadFailed(error: Throwable?) {
                Logger.e(TAG, "onInterstitialLoadFailed", error)
                displayLogs()
            }

            override fun onInterstitialImpression() {
                Logger.d(TAG, "onInterstitialImpression")
            }

            override fun onInterstitialClick() {
                Logger.d(TAG, "onInterstitialClick")
            }

            override fun onInterstitialDismissed() {
                Logger.d(TAG, "onInterstitialDismissed")
            }
        }

        interstitial = HyBidInterstitialAd(requireActivity(), interstitialListener)
        interstitial?.prepareCustomMarkup(markup)
    }

    private fun hideKeyboard(context: Context?, view: View) {
        val imm: InputMethodManager =
            context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    // AdView listeners
    override fun onAdLoaded() {
        Logger.d(TAG, "onAdLoaded")
        displayLogs()
    }

    override fun onAdLoadFailed(error: Throwable?) {
        Logger.d(TAG, "onAdLoadFailed")
        displayLogs()
    }

    override fun onAdImpression() {
        Logger.d(TAG, "onAdImpression")
    }

    override fun onAdClick() {
        Logger.d(TAG, "onAdClick")
    }

    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }
}