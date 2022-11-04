package net.pubnative.lite.demo.ui.fragments.apitester

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.adapters.LegacyApiAdapter
import net.pubnative.lite.demo.ui.adapters.OnLogDisplayListener
import net.pubnative.lite.demo.viewmodel.ApiTesterViewModel
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd
import net.pubnative.lite.sdk.models.Ad
import net.pubnative.lite.sdk.utils.Logger

class LegacyApiTesterFragment : Fragment(R.layout.fragment_legacy_api_tester), OnLogDisplayListener {

    private lateinit var mViewModel: ApiTesterViewModel

    private lateinit var responseInput: EditText
    private lateinit var adSizeGroup: RadioGroup
    private lateinit var markupList: RecyclerView

    private val adapter = LegacyApiAdapter(this)

    private var interstitial: HyBidInterstitialAd? = null

    private val TAG = LegacyApiTesterFragment::class.java.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = ViewModelProvider(this)[ApiTesterViewModel::class.java]

        responseInput = view.findViewById(R.id.input_response)
        adSizeGroup = view.findViewById(R.id.group_ad_size)
        markupList = view.findViewById(R.id.list_markup)
        markupList.isNestedScrollingEnabled = false

        mViewModel.clipboard.observe(viewLifecycleOwner) {
            responseInput.setText(it)
        }

        mViewModel.listVisibillity.observe(viewLifecycleOwner) {
            if (it) markupList.visibility = View.VISIBLE
            else markupList.visibility = View.GONE
        }

        mViewModel.loadInterstitial.observe(viewLifecycleOwner) {
            loadInterstitial(it)
        }

        mViewModel.adapterUpdate.observe(viewLifecycleOwner) {
            adapter.refreshWithAd(it, mViewModel.getAdSize())
        }

        view.findViewById<ImageButton>(R.id.button_paste_clipboard).setOnClickListener {
            mViewModel.pasteFromClipboard()
        }

        view.findViewById<Button>(R.id.button_load).setOnClickListener {
            cleanLogs()
            loadAd()
        }

        adSizeGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_size_banner -> {
                    mViewModel.setAdSize(LegacyApiTesterSize.BANNER)
                }

                R.id.radio_size_medium -> {
                    mViewModel.setAdSize(LegacyApiTesterSize.MEDIUM)
                }

                R.id.radio_size_leaderboard -> {
                    mViewModel.setAdSize(LegacyApiTesterSize.LEADERBOARD)
                }

                R.id.radio_size_native -> {
                    mViewModel.setAdSize(LegacyApiTesterSize.NATIVE)
                }

                R.id.radio_size_interstitial -> {
                    mViewModel.setAdSize(LegacyApiTesterSize.INTERSTITIAL)
                }
            }
        }

        markupList.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        markupList.itemAnimator = DefaultItemAnimator()
        markupList.adapter = adapter
    }


    private fun loadInterstitial(ad: Ad?) {
        interstitial?.destroy()

        val interstitialListener = object : HyBidInterstitialAd.Listener {
            override fun onInterstitialLoaded() {
                Logger.d(TAG, "onInterstitialLoaded")
                interstitial?.show()
                displayLogs()
            }

            override fun onInterstitialLoadFailed(error: Throwable?) {
                Logger.e(TAG, "onInterstitialLoadFailed", error)
                displayLogs()
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
        interstitial?.prepareAd(ad)
    }

    private fun loadAd() {
        mViewModel.loadAdFromResponse(response = responseInput.text.toString())
    }

    override fun onDestroy() {
        interstitial?.destroy()
        super.onDestroy()
    }

    override fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }

    private fun cleanLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.clearEventList()
            activity.notifyAdCleaned()
        }
    }
}