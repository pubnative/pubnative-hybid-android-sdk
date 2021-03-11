package net.pubnative.lite.demo.ui.fragments.mopub

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mopub.nativeads.*
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity
import java.util.*

class
MoPubMediationNativeFragment : Fragment(), MoPubNative.MoPubNativeNetworkListener, NativeAd.MoPubNativeEventListener {
    val TAG = MoPubMediationNativeFragment::class.java.simpleName

    private lateinit var mopubNativeContainer: FrameLayout
    private lateinit var loadButton: Button
    private lateinit var errorView: TextView

    private var mopubNative: MoPubNative? = null
    private var adapterHelper: AdapterHelper? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_mopub_native, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Integer values work in any range. This is intended for the list adapter but
        // it's required even for standalone native ads
        adapterHelper = AdapterHelper(requireContext(), 0, 3)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        mopubNativeContainer = view.findViewById(R.id.ad_container)

        val adUnitId = SettingsManager.getInstance(requireActivity()).getSettings().mopubMediationNativeAdUnitId

        view.findViewById<Button>(R.id.button_load).setOnClickListener {
            errorView.text = ""
            mopubNative?.destroy()

            val desiredAssets = EnumSet.of(RequestParameters.NativeAdAsset.TITLE,
                    RequestParameters.NativeAdAsset.TEXT,
                    RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT,
                    RequestParameters.NativeAdAsset.MAIN_IMAGE,
                    RequestParameters.NativeAdAsset.ICON_IMAGE,
                    RequestParameters.NativeAdAsset.STAR_RATING)

            mopubNative = MoPubNative(requireContext(), adUnitId, this)
            mopubNative?.registerAdRenderer(MoPubStaticNativeAdRenderer(ViewBinder.Builder(R.layout.layout_native_ad)
                    .mainImageId(R.id.ad_banner)
                    .iconImageId(R.id.ad_icon)
                    .titleId(R.id.ad_title)
                    .textId(R.id.ad_description)
                    .privacyInformationIconImageId(R.id.ad_choices)
                    .callToActionId(R.id.ad_call_to_action)
                    .build()))

            val requestParameters = RequestParameters.Builder().desiredAssets(desiredAssets).build()

            mopubNative?.makeRequest(requestParameters)
        }
    }

    override fun onDestroy() {
        mopubNative?.destroy()
        super.onDestroy()
    }

    // ---------------- MoPub Native Listener ---------------------
    override fun onNativeLoad(nativeAd: NativeAd?) {
        val view = adapterHelper?.getAdView(null, null, nativeAd)
        nativeAd?.setMoPubNativeEventListener(this)
        mopubNativeContainer.addView(view)
        Log.d(TAG, "onNativeLoad")
        displayLogs()
    }

    override fun onNativeFail(errorCode: NativeErrorCode?) {
        Log.d(TAG, "onNativeFail")
        errorView.text = errorCode.toString()
        displayLogs()
    }

    // ---------------- Native Event Listener ---------------------
    override fun onImpression(view: View?) {
        Log.d(TAG, "Native: onImpression")
    }

    override fun onClick(view: View?) {
        Log.d(TAG, "Native: onClick")
    }

    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }
}