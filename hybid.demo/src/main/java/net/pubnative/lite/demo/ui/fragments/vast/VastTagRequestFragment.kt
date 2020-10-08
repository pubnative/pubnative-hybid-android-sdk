package net.pubnative.lite.demo.ui.fragments.vast

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd
import net.pubnative.lite.sdk.utils.Logger

class VastTagRequestFragment : Fragment(), HyBidInterstitialAd.Listener{
    private val TAG = VastTagRequestFragment::class.java.simpleName

    private lateinit var vastTagInput: EditText
    private lateinit var adSizeGroup: RadioGroup

    private lateinit var mInterstitial : HyBidInterstitialAd

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_vast_tag, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adSizeGroup = view.findViewById(R.id.group_vast_ad_size)
        vastTagInput = view.findViewById(R.id.input_vast_tag)

        view.findViewById<ImageButton>(R.id.button_vast_paste_clipboard).setOnClickListener {
            pasteFromClipboard()
        }

        view.findViewById<Button>(R.id.button_vast_load).setOnClickListener {
            loadVastTag()
        }
    }

    private fun pasteFromClipboard() {
        val clipboardText = ClipboardUtils.copyFromClipboard(activity!!)
        if (!TextUtils.isEmpty(clipboardText)) {
            vastTagInput.setText(clipboardText)
        }
    }

    private fun loadVastTag(){
        val vastUrl = vastTagInput.text.toString()

        if (TextUtils.isEmpty(vastUrl)){
            Toast.makeText(activity, "Please input some vast adserver URL", Toast.LENGTH_SHORT).show()
        } else {
            loadVastTagDirectly(vastUrl)
        }
    }

    private fun loadVastTagDirectly(url: String){
        mInterstitial = HyBidInterstitialAd(activity, this)
        mInterstitial.prepareVideoTag(url)
    }

    // Interstitial listeners
    override fun onInterstitialLoaded() {
        Logger.d(TAG, "onInterstitialAdLoaded")
        mInterstitial.show()
    }

    override fun onInterstitialLoadFailed(error: Throwable?) {
        Logger.e(TAG, "onInterstitialAdLoadFailed", error)
    }

    override fun onInterstitialImpression() {
        Logger.d(TAG, "onInterstitialAdImpression")
    }

    override fun onInterstitialDismissed() {
        Logger.d(TAG, "onInterstitialAdDismissed")
    }

    override fun onInterstitialClick() {
        Logger.d(TAG, "onInterstitialAdClick")
    }
}