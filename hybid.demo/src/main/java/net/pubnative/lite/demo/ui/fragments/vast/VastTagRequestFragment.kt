package net.pubnative.lite.demo.ui.fragments.vast

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.network.PNHttpClient
import net.pubnative.lite.sdk.utils.Logger
import net.pubnative.lite.sdk.views.HyBidMRectAdView
import net.pubnative.lite.sdk.views.PNAdView

class VastTagRequestFragment : Fragment(), PNAdView.Listener {
    private val TAG = VastTagRequestFragment::class.java.simpleName

    private lateinit var vastTagInput: EditText
    private lateinit var adSizeGroup: RadioGroup

    private lateinit var mResponse: String
    private lateinit var mRectContainer : RelativeLayout

    private var selectedSize: Int = R.id.radio_vast_size_medium

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_vast_tag, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mRectContainer = view.findViewById(R.id.mrect_vast_adview)
        adSizeGroup = view.findViewById(R.id.group_vast_ad_size)
        vastTagInput = view.findViewById(R.id.input_vast_tag)

        view.findViewById<ImageButton>(R.id.button_vast_paste_clipboard).setOnClickListener {
            pasteFromClipboard()
        }

        view.findViewById<Button>(R.id.button_vast_load).setOnClickListener {
            loadVastTag()
        }

        adSizeGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedSize = checkedId
            updateListVisibility()
        }
    }

    private fun pasteFromClipboard() {
        val clipboardText = ClipboardUtils.copyFromClipboard(activity!!)
        if (!TextUtils.isEmpty(clipboardText)) {
            vastTagInput.setText(clipboardText)
        }
    }

    private fun updateListVisibility() {
        if (selectedSize == R.id.radio_vast_size_medium) {
            mRectContainer.visibility = View.VISIBLE
        } else {
            mRectContainer.visibility = View.GONE
        }
    }

    private fun loadVastTag(){
        val vastUrl = vastTagInput.text.toString()

        if (TextUtils.isEmpty(vastUrl)){
            Toast.makeText(activity, "Please input some vast adserver URL", Toast.LENGTH_SHORT).show()
        } else {
            requestVastTag(vastUrl)
        }
    }

    private fun requestVastTag(url : String){
        PNHttpClient.makeRequest(activity, url, null, null, object : PNHttpClient.Listener{

            override fun onSuccess(response: String) {
                if (TextUtils.isEmpty(response)){
                    Toast.makeText(activity, "AdServer response is empty or null", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, response, Toast.LENGTH_LONG).show()
                    //renderVast(response)
                }
            }

            override fun onFailure(error: Throwable?) {
                Logger.e(TAG, "Request failed: " + error.toString());
                Toast.makeText(activity, "AdServer response is empty or null", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun renderVast(vastXmlResponse : String){
        if (selectedSize == R.id.radio_size_interstitial) {
            loadInterstitialVast(vastXmlResponse)
        } else {
            loadMrectVast(vastXmlResponse)
        }
    }

    private fun loadInterstitialVast(vastXmlResponse: String){

    }

    private fun loadMrectVast(vastXmlResponse: String){
        //mRectContainer.addView(mRectAdView)
        val adView = HyBidMRectAdView(context)
        mRectContainer.addView(adView)
        adView.renderAd(vastXmlResponse, this)
    }

    override fun onAdLoaded() {
        Logger.d(TAG, "onAdLoaded")
    }

    override fun onAdLoadFailed(error: Throwable?) {
        Logger.e(TAG, "onAdLoadFailed", error)
    }

    override fun onAdImpression() {
        Logger.d(TAG, "onAdImpression")
    }

    override fun onAdClick() {
        Logger.d(TAG, "onAdClick")
    }

}