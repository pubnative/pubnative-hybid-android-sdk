// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
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
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.HyBidError
import net.pubnative.lite.sdk.models.AdSize
import net.pubnative.lite.sdk.mraid.utils.MraidCloseAdRepo
import net.pubnative.lite.sdk.views.HyBidAdView
import net.pubnative.lite.sdk.views.PNAdView

class HyBidStickyBannerFragment : Fragment(R.layout.fragment_sticky_top_bottom), PNAdView.Listener,
    RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

    private var adSize: AdSize = AdSize.SIZE_320x50
    private val TAG: String = HyBidStickyBannerFragment::class.java.simpleName

    private var zoneId: String? = null

    private var mPosition: HyBidAdView.Position = HyBidAdView.Position.TOP

    private var hybidBanner: HyBidAdView? = null
    private lateinit var loadButton: Button
    private lateinit var adSizeSpinner: Spinner
    private lateinit var apiRadioGroup: RadioGroup
    private lateinit var spinnerAdapter: ArrayAdapter<AdSize>
    private lateinit var errorView: TextView
    private lateinit var errorCodeView: TextView
    private lateinit var creativeIdView: TextView

    private lateinit var radioPosition: RadioGroup

    private val adSizes = arrayOf(
        AdSize.SIZE_320x50,
        AdSize.SIZE_160x600,
        AdSize.SIZE_250x250,
        AdSize.SIZE_300x50,
        AdSize.SIZE_300x250,
        AdSize.SIZE_300x600,
        AdSize.SIZE_320x100,
        AdSize.SIZE_320x480,
        AdSize.SIZE_480x320,
        AdSize.SIZE_728x90,
        AdSize.SIZE_768x1024,
        AdSize.SIZE_1024x768
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        errorCodeView = view.findViewById(R.id.view_error_code)
        creativeIdView = view.findViewById(R.id.view_creative_id)
        loadButton = view.findViewById(R.id.button_load)
        adSizeSpinner = view.findViewById(R.id.spinner_ad_size)
        radioPosition = view.findViewById(R.id.radioPosition)
        apiRadioGroup = view.findViewById(R.id.group_api_type)

        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)

        loadButton.setOnClickListener {
            errorView.text = ""
            val activity = activity as TabActivity
            activity.notifyAdCleaned()
            loadPNAd()
        }

        errorView.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                requireActivity(),
                errorView.text.toString()
            )
        }
        creativeIdView.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                requireActivity(),
                creativeIdView.text.toString()
            )
        }

        spinnerAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, adSizes)
        adSizeSpinner.adapter = spinnerAdapter
        adSizeSpinner.onItemSelectedListener = this

        radioPosition.setOnCheckedChangeListener(this)
    }

    private fun loadPNAd() {
        hybidBanner?.destroy()

        hybidBanner = HyBidAdView(activity)
        hybidBanner?.setAdSize(adSize)
        hybidBanner?.setIsAdSticky(true)
        if (apiRadioGroup.checkedRadioButtonId == R.id.radio_api_ortb) {
            hybidBanner?.loadExchangeAd(zoneId, mPosition, this)
        } else {
            hybidBanner?.load(zoneId, mPosition, this)
        }
    }


    // --------------- PNAdView Listener --------------------
    override fun onAdLoaded() {
        Log.d(TAG, "onAdLoaded")
        displayLogs()
    }

    override fun onAdLoadFailed(error: Throwable?) {
        if (error != null && error is HyBidError) {
            Log.e(TAG, error.message ?: " - ")
            errorCodeView.text = error.errorCode.code.toString()
            errorView.text = error.message ?: " - "
        } else {
            errorCodeView.text = " - "
            errorView.text = " - "
        }
    }

    override fun onAdImpression() {
        Log.d(TAG, "onAdImpression")
        if (!TextUtils.isEmpty(hybidBanner?.creativeId)) {
            creativeIdView.text = hybidBanner?.creativeId
        }
        if (HyBid.getDiagnosticsManager() != null) {
            HyBid.getDiagnosticsManager().printPlacementDiagnosticsLog(
                requireContext(),
                hybidBanner?.placementParams
            )
        }
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

    override fun onDestroy() {
        hybidBanner?.destroy()
        super.onDestroy()
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        if (checkedId == R.id.radioTop) {
            mPosition = HyBidAdView.Position.TOP
        } else if (checkedId == R.id.radioBottom) {
            mPosition = HyBidAdView.Position.BOTTOM
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        adSize = adSizes[pos]
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        adSize = AdSize.SIZE_320x50
    }
}