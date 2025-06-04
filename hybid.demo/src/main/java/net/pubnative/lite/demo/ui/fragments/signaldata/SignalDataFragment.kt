// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.fragments.signaldata

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
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
    private lateinit var loadButton: MaterialButton
    private lateinit var showButton: MaterialButton
    private val adapter = SignalDataAdapter()

    private var selectedSize: Int = R.id.radio_size_banner

    private var interstitial: HyBidInterstitialAd? = null

    private var rewarded: HyBidRewardedAd? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signalDataInput = view.findViewById(R.id.input_signal_data)
        adSizeGroup = view.findViewById(R.id.group_ad_size)
        signalDataList = view.findViewById(R.id.list_signal_data)
        loadButton = view.findViewById(R.id.button_load)
        showButton = view.findViewById(R.id.button_show)

        view.findViewById<ImageButton>(R.id.button_paste_clipboard).setOnClickListener {
            pasteFromClipboard()
        }

        loadButton.setOnClickListener {
            loadSignalData()
        }

        showButton.setOnClickListener {
            when (selectedSize) {
                R.id.radio_size_interstitial -> {
                    interstitial?.show()
                }

                R.id.radio_size_rewarded -> {
                    rewarded?.show()
                }
            }
        }

        adSizeGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedSize = checkedId
            updateVisibility()
        }

        val linearLayoutManager = object : LinearLayoutManager(activity, RecyclerView.VERTICAL, false) {
            override fun canScrollVertically() = false
        }

        signalDataList.layoutManager = linearLayoutManager
        signalDataList.itemAnimator = DefaultItemAnimator()
        signalDataList.adapter = adapter
    }

    override fun onDestroy() {
        interstitial?.destroy()
        rewarded?.destroy()
        super.onDestroy()
    }

    private fun updateVisibility() {
        if (selectedSize == R.id.radio_size_banner
            || selectedSize == R.id.radio_size_medium
            || selectedSize == R.id.radio_size_leaderboard
            || selectedSize == R.id.radio_size_native
        ) {
            signalDataList.visibility = View.VISIBLE
            showButton.visibility = View.GONE
            showButton.isEnabled = false
        } else {
            signalDataList.visibility = View.GONE
            showButton.visibility = View.VISIBLE
            showButton.isEnabled = false
        }
    }

    private fun pasteFromClipboard() {
        val clipboardText = ClipboardUtils.copyFromClipboard(requireActivity())
        if (!TextUtils.isEmpty(clipboardText)) {
            signalDataInput.setText(clipboardText)
        }
    }

    private fun loadSignalData() {
        showButton.isEnabled = false
        var signalData = signalDataInput.text.toString()
        if (TextUtils.isEmpty(signalData)) {
            Toast.makeText(activity, "Please input some signal data", Toast.LENGTH_SHORT).show()
        } else {
            when (selectedSize) {
                R.id.radio_size_interstitial -> {
                    loadInterstitial(signalData)
                }

                R.id.radio_size_rewarded -> {
                    loadRewarded(signalData)
                }

                else -> {
                    adapter.refreshWithSignalData(signalData, selectedSize)
                }
            }
        }
    }

    private fun loadInterstitial(signalData: String) {
        interstitial?.destroy()

        val interstitialListener = object : HyBidInterstitialAd.Listener {
            override fun onInterstitialLoaded() {
                Logger.d(TAG, "onInterstitialLoaded")
                showButton.isEnabled = true
            }

            override fun onInterstitialLoadFailed(error: Throwable?) {
                Logger.e(TAG, "onInterstitialLoadFailed", error)
                Toast.makeText(requireContext(), error?.message, Toast.LENGTH_LONG).show()
                showButton.isEnabled = false
            }

            override fun onInterstitialImpression() {
                Logger.d(TAG, "onInterstitialImpression")
            }

            override fun onInterstitialClick() {
                Logger.d(TAG, "onInterstitialClick")
            }

            override fun onInterstitialDismissed() {
                Logger.d(TAG, "onInterstitialDismissed")
                showButton.isEnabled = false
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
                showButton.isEnabled = true
            }

            override fun onRewardedLoadFailed(error: Throwable?) {
                Logger.d(TAG, "onRewardedLoadFailed")
                Toast.makeText(requireContext(), error?.message, Toast.LENGTH_LONG).show()
                showButton.isEnabled = false
            }

            override fun onRewardedOpened() {
                Logger.d(TAG, "onRewardedOpened")
            }

            override fun onRewardedClosed() {
                Logger.d(TAG, "onRewardedClosed")
                showButton.isEnabled = false
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