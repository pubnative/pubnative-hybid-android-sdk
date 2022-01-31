package net.pubnative.lite.demo.ui.fragments.signaldata

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.adapters.SignalDataAdapter
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd
import net.pubnative.lite.sdk.rewarded.HyBidRewardedAd
import net.pubnative.lite.sdk.utils.Logger

class SignalDataFragment : Fragment(R.layout.fragment_signal_data) {

    private val TAG = SignalDataFragment::class.java.simpleName

    private lateinit var signalDataInput: EditText
    private lateinit var adSizeGroup: RadioGroup
    private lateinit var signalDataList: RecyclerView
    private val adapter = SignalDataAdapter()

    private var selectedSize: Int = R.id.radio_size_banner

    private var interstitial: HyBidInterstitialAd? = null

    private var rewarded: HyBidRewardedAd? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signalDataInput = view.findViewById(R.id.input_signal_data)
        adSizeGroup = view.findViewById(R.id.group_ad_size)
        signalDataList = view.findViewById(R.id.list_signal_data)

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
        rewarded?.destroy()
        super.onDestroy()
    }

    private fun updateListVisibility() {
        if (selectedSize == R.id.radio_size_banner
            || selectedSize == R.id.radio_size_medium
            || selectedSize == R.id.radio_size_leaderboard
        ) {
            signalDataList.visibility = View.VISIBLE
        } else {
            signalDataList.visibility = View.GONE
        }
    }

    private fun pasteFromClipboard() {
        val clipboardText = ClipboardUtils.copyFromClipboard(requireActivity())
        if (!TextUtils.isEmpty(clipboardText)) {
            signalDataInput.setText(clipboardText)
        }
    }

    private fun loadSignalData() {
        var signalData = signalDataInput.text.toString()
        if (TextUtils.isEmpty(signalData)) {
            Toast.makeText(activity, "Please input some signal data", Toast.LENGTH_SHORT).show()
        } else {
            if (selectedSize == R.id.radio_size_interstitial) {
                loadInterstitial(signalData)
            } else if (selectedSize == R.id.radio_size_rewarded) {
                loadRewarded(signalData)
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
                Toast.makeText(requireContext(), error?.message, Toast.LENGTH_LONG).show()
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

    private fun loadRewarded(signalData: String) {
        rewarded?.destroy()

        val rewardedListener = object : HyBidRewardedAd.Listener {
            override fun onRewardedLoaded() {
                Logger.d(TAG, "onRewardedLoaded")
                rewarded?.show()
            }

            override fun onRewardedLoadFailed(error: Throwable?) {
                Logger.d(TAG, "onRewardedLoadFailed")
                Toast.makeText(requireContext(), error?.message, Toast.LENGTH_LONG).show()
            }

            override fun onRewardedOpened() {
                Logger.d(TAG, "onRewardedOpened")
            }

            override fun onRewardedClosed() {
                Logger.d(TAG, "onRewardedClosed")
            }

            override fun onRewardedClick() {
                Logger.d(TAG, "onRewardedClick")
            }

            override fun onReward() {
                Logger.d(TAG, "onReward")
            }
        }

        rewarded = HyBidRewardedAd(requireActivity(), rewardedListener)
        rewarded?.prepareAd(signalData)
    }
}