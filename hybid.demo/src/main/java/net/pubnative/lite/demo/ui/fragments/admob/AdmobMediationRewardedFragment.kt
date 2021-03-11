package net.pubnative.lite.demo.ui.fragments.admob

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.AdmobErrorParser
import net.pubnative.lite.demo.util.ClipboardUtils

class AdmobMediationRewardedFragment : Fragment() {
    val TAG = AdmobMediationRewardedFragment::class.java.simpleName

    private lateinit var admobRewarded: RewardedAd
    private lateinit var loadButton: Button
    private lateinit var showButton: Button
    private lateinit var errorView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_admob_rewarded, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        showButton = view.findViewById(R.id.button_show)

        val adUnitId = SettingsManager.getInstance(requireActivity()).getSettings().admobRewardedAdUnitId
        showButton.isEnabled = false

        admobRewarded = RewardedAd(requireActivity(), adUnitId)
        val adLoadCallback = object : RewardedAdLoadCallback(){
            override fun onRewardedAdLoaded(){
                Log.d(TAG, "onRewardedAdLoaded")
                displayLogs()
                Toast.makeText(context, "Rewarded Ad Loaded", Toast.LENGTH_SHORT).show()
                showButton.isEnabled = true
            }
            override fun onRewardedAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, "onRewardedAdFailedToLoad")
                displayLogs()
                Toast.makeText(context, "Rewarded Ad Failed to Load", Toast.LENGTH_SHORT).show()
                errorView.text = AdmobErrorParser.getErrorMessage(adError.code)
            }
        }

        val rewardedAdCallback = object: RewardedAdCallback(){
            override fun onRewardedAdOpened() {
                super.onRewardedAdOpened()
                Log.d(TAG, "onRewardedAdOpened")
            }
            override fun onRewardedAdClosed() {
                super.onRewardedAdClosed()
                Log.d(TAG, "onRewardedAdClosed")
            }
            override fun onUserEarnedReward(@NonNull reward: RewardItem) {
                Log.d(TAG, "onUserEarnedReward")
            }
            override fun onRewardedAdFailedToShow(adError: AdError) {
                super.onRewardedAdFailedToShow(adError)
                Log.d(TAG, "onRewardedAdFailedToShow")
                errorView.text = AdmobErrorParser.getErrorMessage(adError.code)
            }
        }

        loadButton.setOnClickListener {
            errorView.text = ""
            admobRewarded.loadAd(AdRequest.Builder()
                    .build(), adLoadCallback)
        }

        showButton.setOnClickListener{
            if (admobRewarded.isLoaded) {
                admobRewarded.show(requireActivity(), rewardedAdCallback)
            }
        }

        errorView.setOnClickListener { ClipboardUtils.copyToClipboard(requireActivity(), errorView.text.toString()) }
    }

    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }



}