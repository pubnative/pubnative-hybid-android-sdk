package net.pubnative.lite.demo.ui.fragments.chartboost

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.chartboost.heliumsdk.HeliumSdk
import com.chartboost.heliumsdk.ad.ChartboostMediationAdLoadRequest
import com.chartboost.heliumsdk.ad.ChartboostMediationFullscreenAd
import com.chartboost.heliumsdk.ad.ChartboostMediationFullscreenAdListener
import com.chartboost.heliumsdk.ad.ChartboostMediationFullscreenAdLoadResult
import com.chartboost.heliumsdk.domain.ChartboostMediationAdException
import com.chartboost.heliumsdk.domain.Keywords
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager

class ChartboostMediationInterstitialVideoFragment : Fragment(R.layout.fragment_chartboost_interstitial_video),
    ChartboostMediationFullscreenAdListener {

    companion object {
        private val TAG = ChartboostMediationInterstitialVideoFragment::class.java.simpleName
    }

    private var adRequest : ChartboostMediationAdLoadRequest? = null
    private var heliumPlacementName : String? = null
    private lateinit var loadButton: Button
    private lateinit var showButton: Button
    private lateinit var errorView: TextView
    private lateinit var loadResult: ChartboostMediationFullscreenAdLoadResult

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        showButton = view.findViewById(R.id.button_show)
        showButton.isEnabled = false

        heliumPlacementName = SettingsManager.getInstance(requireActivity()).getSettings()
            .chartboostSettings?.mediationInterstitialVideoAdUnitId

        adRequest = heliumPlacementName?.let {
            ChartboostMediationAdLoadRequest(
                it,
                Keywords()
            )
        }

        loadButton.setOnClickListener {
            loadAd()
        }
        showButton.setOnClickListener { showAd() }
        showButton.isEnabled = false

    }

    private fun loadAd() {
        if (adRequest != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                loadResult = HeliumSdk.loadFullscreenAd(requireContext(), adRequest!!, this@ChartboostMediationInterstitialVideoFragment)
                if (loadResult.ad != null) {
                    withContext(Dispatchers.Main) {
                        showButton.isEnabled = true
                    }
                }
            }
            loadButton.isEnabled = false
        }
    }

    private fun showAd() {
        lifecycleScope.launch {
            loadResult.ad?.show(requireContext())
        }
    }


    // Chartboost Listeners
    override fun onAdClicked(ad: ChartboostMediationFullscreenAd) {
        Log.d(TAG, "onAdClicked")
    }

    override fun onAdClosed(
        ad: ChartboostMediationFullscreenAd,
        error: ChartboostMediationAdException?
    ) {
        if (error != null) {
            val errorMessage = error.message
            Log.e(TAG, "onAdLoadFailed: $errorMessage")
        } else {
            Log.d(TAG, "onAdClosed")
        }
        loadButton.isEnabled = true
    }

    override fun onAdExpired(ad: ChartboostMediationFullscreenAd) {
        Log.d(TAG, "onAdExpired")
        loadButton.isEnabled = true
    }

    override fun onAdImpressionRecorded(ad: ChartboostMediationFullscreenAd) {
        Log.d(TAG, "onAdImpressionRecorded")
    }

    override fun onAdRewarded(ad: ChartboostMediationFullscreenAd) {
        Log.d(TAG, "onAdRewarded")
    }
}