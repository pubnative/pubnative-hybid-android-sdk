package net.pubnative.lite.demo.ui.fragments.signaldata

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.adapters.SignalDataAdapter
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd
import net.pubnative.lite.sdk.utils.Logger

class SignalDataFragment : Fragment() {
    private val TAG = SignalDataFragment::class.java.simpleName

    private lateinit var signalDataInput: EditText
    private lateinit var adSizeGroup: RadioGroup
    private lateinit var signalDataList: RecyclerView
    private val adapter = SignalDataAdapter()

    private var selectedSize: Int = R.id.radio_size_banner

    private var interstitial: HyBidInterstitialAd? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_signal_data, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signalDataInput = view.findViewById(R.id.input_signal_data)
        adSizeGroup = view.findViewById(R.id.group_ad_size)
        signalDataList = view.findViewById(R.id.list_signal_data)

        signalDataInput.setText("{\"status\":\"ok\",\"ads\":[{\"assetgroupid\":10,\"assets\":[{\"type\":\"htmlbanner\",\"data\":{ \"w\":320,\"h\":50,\"html\":\"<script class=\\\"pn-ad-tag\\\" type=\\\"text/javascript\\\">(function(beacons,trackerURL,options){var delay=1,passed=0,fired=false,readyBinded=false,viewableBinded=false,deepcut=false;deepcut=Math.random()<.001;function fire(url){(new Image).src=url;return true}function track(msg){if(!trackerURL)return;if(!deepcut)return;fire(trackerURL+encodeURIComponent(msg))}function fireAll(){if(fired)return;fired=true;for(var i=0;i<beacons.length;i++)fire(beacons[i]);track(\\\"imp P\\\"+boolToChar(options.isHTML5)+boolToChar(options.isPlMRAID)+boolToChar(options.isMRAID)+boolToChar(typeof mraid===\\r\\n    \\\"object\\\")+boolToChar(window.top===window))}function boolToChar(val){if(typeof val===\\\"undefined\\\")return\\\"N\\\";return val?\\\"1\\\":\\\"0\\\"}track(\\\"inf P\\\"+boolToChar(options.isHTML5)+boolToChar(options.isPlMRAID)+boolToChar(options.isMRAID)+boolToChar(typeof mraid===\\\"object\\\")+boolToChar(window.top===window));window.addEventListener(\\\"error\\\",function(event){track(\\\"er2 WIND \\\"+event.message);trackDbg()});fireAll()})([],\\\"https://got.pubnative.net/imp/error?t=qFJ8wCcv-SGRD3P4KI-7jSrrn1EJoqDUKz8IOE1kCRBSN21rBxkjPvcJHTUGgsBqbQmRVEOAhn5rNAj5RXzDt2BOI7MPxJnyYUUjvNpRLtHd2dwvjBsqSPAU-rPJvHD322Am94TRwYtU8ag&msg=\\\", {\\\"pubAppID\\\":1773388,\\\"dspID\\\":64,\\\"impID\\\":\\\"3530a316-64f9-4bfa-9568-8ab7e7d8eb5d\\\",\\\"isMRAID\\\":false,\\\"isHTML5\\\":false});</script>\\r\\n<a target=\\\"_blank\\\" href=\\\"https://itunes.apple.com/us/app/id1382171002\\\"><img src=\\\"https://cdn.pubnative.net/widget/v3/assets/easyforecast_320x50.jpg\\\" width=\\\"320\\\" height=\\\"50\\\" border=\\\"0\\\" alt=\\\"Advertisement\\\" /></a>\"}}],\"meta\":null,\"beacons\":null}]}")

        view.findViewById<ImageButton>(R.id.button_paste_clipboard).setOnClickListener {
            pasteFromClipboard()
        }

        view.findViewById<Button>(R.id.button_load).setOnClickListener {
            loadSignalData()
        }

        adSizeGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedSize = checkedId
            updateListVisibility()
        }

        signalDataList.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        signalDataList.itemAnimator = DefaultItemAnimator()
        signalDataList.adapter = adapter
    }

    override fun onDestroy() {
        interstitial?.destroy()
        super.onDestroy()
    }

    private fun updateListVisibility() {
        if (selectedSize == R.id.radio_size_banner
                || selectedSize == R.id.radio_size_medium
                || selectedSize == R.id.radio_size_leaderboard) {
            signalDataList.visibility = View.VISIBLE
        } else {
            signalDataList.visibility = View.GONE
        }
    }

    private fun pasteFromClipboard() {
        val clipboardText = ClipboardUtils.copyFromClipboard(activity!!)
        if (!TextUtils.isEmpty(clipboardText)) {
            signalDataInput.setText(clipboardText)
        }
    }

    private fun loadSignalData() {
        val signalData = signalDataInput.text.toString()
        if (TextUtils.isEmpty(signalData)) {
            Toast.makeText(activity, "Please input some signal data", Toast.LENGTH_SHORT).show()
        } else {
            if (selectedSize == R.id.radio_size_interstitial) {
                loadInterstitial(signalData)
            } else {
                adapter.refreshWithSignalData(signalData, selectedSize)
            }
        }
    }

    private fun loadInterstitial(signalData: String) {
        interstitial?.destroy()

        val interstitialListener = object : HyBidInterstitialAd.Listener {
            override fun onInterstitialLoaded() {
                Logger.d(TAG, "onInterstitialLoaded")
                interstitial?.show()
            }

            override fun onInterstitialLoadFailed(error: Throwable?) {
                Logger.e(TAG, "onInterstitialLoadFailed", error)
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
        interstitial?.prepareAd(signalData)
    }
}