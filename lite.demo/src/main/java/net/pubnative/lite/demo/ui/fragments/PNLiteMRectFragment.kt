package net.pubnative.lite.demo.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.doubleclick.PublisherAdView
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.sdk.api.MRectRequestManager
import net.pubnative.lite.sdk.api.RequestManager
import net.pubnative.lite.sdk.models.Ad
import net.pubnative.lite.sdk.utils.PrebidUtils

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class PNLiteMRectFragment : Fragment(), RequestManager.RequestListener {
    val TAG = PNLiteMRectFragment::class.java.simpleName

    private lateinit var requestManager: RequestManager
    private var zoneId: String? = null

    private lateinit var pnliteMRectContainer: FrameLayout
    private lateinit var loadButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_pnlite_mrect, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadButton = view.findViewById(R.id.button_load)
        pnliteMRectContainer = view.findViewById(R.id.pnlite_mrect_container)

        requestManager = MRectRequestManager()

        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)

        loadButton.setOnClickListener {
            loadPNAd()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun loadPNAd() {
        requestManager.setZoneId(zoneId)
        requestManager.setRequestListener(this)
        requestManager.requestAd()
    }

    // --------------- PNLite Request Listener --------------------
    override fun onRequestSuccess(ad: Ad?) {
        Log.d(TAG, "onRequestSuccess")
    }

    override fun onRequestFail(throwable: Throwable?) {
        Log.d(TAG, "onRequestFail: ", throwable)
        Toast.makeText(activity, throwable?.message, Toast.LENGTH_SHORT).show()
    }
}