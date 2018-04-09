package net.pubnative.lite.demo.ui.fragments

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
import net.pubnative.lite.sdk.api.InterstitialRequestManager
import net.pubnative.lite.sdk.api.RequestManager
import net.pubnative.lite.sdk.interstitial.presenter.InterstitialPresenter
import net.pubnative.lite.sdk.interstitial.presenter.InterstitialPresenterFactory
import net.pubnative.lite.sdk.models.Ad

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class PNLiteInterstitialFragment : Fragment(), RequestManager.RequestListener, InterstitialPresenter.Listener {
    val TAG = PNLiteInterstitialFragment::class.java.simpleName

    private lateinit var requestManager: RequestManager
    private var zoneId: String? = null

    private var presenter: InterstitialPresenter? = null
    private lateinit var loadButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_pnlite_interstitial, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadButton = view.findViewById(R.id.button_load)

        requestManager = InterstitialRequestManager()

        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)

        loadButton.setOnClickListener {
            loadPNAd()
        }
    }

    override fun onDestroy() {
        presenter?.destroy()
        super.onDestroy()
    }

    fun loadPNAd() {
        requestManager.setZoneId(zoneId)
        requestManager.setRequestListener(this)
        requestManager.requestAd()
    }

    // --------------- PNLite Request Listener --------------------
    override fun onRequestSuccess(ad: Ad?) {
        presenter?.destroy()
        presenter = InterstitialPresenterFactory(activity).createInterstitialPresenter(ad, this)
        presenter?.load()
        Log.d(TAG, "onRequestSuccess")
    }

    override fun onRequestFail(throwable: Throwable?) {
        Log.d(TAG, "onRequestFail: ", throwable)
        Toast.makeText(activity, throwable?.message, Toast.LENGTH_SHORT).show()
    }

    // --------------- PNLite Interstitial Presenter Listener --------------------
    override fun onInterstitialLoaded(interstitialPresenter: InterstitialPresenter?) {
        presenter?.show()
        Log.d(TAG, "onInterstitialLoaded")
    }

    override fun onInterstitialError(interstitialPresenter: InterstitialPresenter?) {
        Log.d(TAG, "onInterstitialError")
    }

    override fun onInterstitialShown(interstitialPresenter: InterstitialPresenter?) {
        Log.d(TAG, "onInterstitialShown")
    }

    override fun onInterstitialClicked(interstitialPresenter: InterstitialPresenter?) {
        Log.d(TAG, "onInterstitialClicked")
    }

    override fun onInterstitialDismissed(interstitialPresenter: InterstitialPresenter?) {
        Log.d(TAG, "onInterstitialDismissed")
    }
}