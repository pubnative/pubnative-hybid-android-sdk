package net.pubnative.lite.demo.ui.fragments.pnlite

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.sdk.views.PNAdView
import net.pubnative.lite.sdk.views.PNMRectAdView

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class PNLiteMRectFragment : Fragment(), PNAdView.Listener {
    val TAG = PNLiteMRectFragment::class.java.simpleName

    private var zoneId: String? = null

    private lateinit var pnliteMRect: PNMRectAdView
    private lateinit var loadButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_pnlite_mrect, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadButton = view.findViewById(R.id.button_load)
        pnliteMRect = view.findViewById(R.id.pnlite_mrect)

        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)

        loadButton.setOnClickListener {
            loadPNAd()
        }
    }

    override fun onDestroy() {
        pnliteMRect.destroy()
        super.onDestroy()
    }

    fun loadPNAd() {
        pnliteMRect.load(zoneId, this)
    }

    // --------------- PNAdView Listener --------------------
    override fun onAdLoaded() {
        Log.d(TAG, "onAdLoaded")
    }

    override fun onAdLoadFailed(error: Throwable?) {
        Log.e(TAG, "onAdLoadFailed", error)
        Toast.makeText(activity, error?.message, Toast.LENGTH_SHORT).show()
    }

    override fun onAdImpression() {
        Log.d(TAG, "onAdImpression")
    }

    override fun onAdClick() {
        Log.d(TAG, "onAdClick")
    }
}