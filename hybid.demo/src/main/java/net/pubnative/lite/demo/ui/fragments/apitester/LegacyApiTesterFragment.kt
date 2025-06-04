// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.fragments.apitester

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.AdCustomizationActivity
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.adapters.LegacyApiAdapter
import net.pubnative.lite.demo.ui.adapters.OnLogDisplayListener
import net.pubnative.lite.demo.ui.fragments.apitester.LegacyApiTesterSize.*
import net.pubnative.lite.demo.ui.fragments.markup.MarkupType
import net.pubnative.lite.demo.viewmodel.AdCustomizationViewModel
import net.pubnative.lite.demo.viewmodel.ApiTesterViewModel
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.VideoListener
import net.pubnative.lite.sdk.analytics.Reporting
import net.pubnative.lite.sdk.analytics.ReportingEvent
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd
import net.pubnative.lite.sdk.models.Ad
import net.pubnative.lite.sdk.models.AdSize
import net.pubnative.lite.sdk.models.IntegrationType
import net.pubnative.lite.sdk.rewarded.HyBidRewardedAd
import net.pubnative.lite.sdk.utils.Logger
import java.util.Locale

class LegacyApiTesterFragment : Fragment(R.layout.fragment_legacy_api_tester),
    OnLogDisplayListener {

    private lateinit var viewModel: ApiTesterViewModel
    private lateinit var adCustomizationViewModel: AdCustomizationViewModel

    private lateinit var responseInput: EditText
    private lateinit var oRTBBodyInput: EditText
    private lateinit var adSizeGroup: RadioGroup
    private lateinit var responseSourceGroup: RadioGroup
    private lateinit var enableAdCustomisationCheckbox: CheckBox
    private lateinit var markupList: RecyclerView
    private lateinit var loadButton: MaterialButton
    private lateinit var showButton: MaterialButton
    private lateinit var customizeButton: MaterialButton
    private lateinit var oRTBLayout: RelativeLayout
    private lateinit var adCustomizationLayout: LinearLayout

    private val adapter = LegacyApiAdapter(this)

    private var interstitial: HyBidInterstitialAd? = null
    private var rewardedAd: HyBidRewardedAd? = null

    private val TAG = LegacyApiTesterFragment::class.java.simpleName

    private var adCustomisationEnabled: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModels()
        initViews(view)
        initObservers()
        setListeners()
    }


    private fun loadInterstitial(ad: Ad?) {
        interstitial?.destroy()

        val interstitialListener = object : HyBidInterstitialAd.Listener {
            override fun onInterstitialLoaded() {
                Logger.d(TAG, "onInterstitialLoaded")
                displayLogs()
                showButton.isEnabled = true
            }

            override fun onInterstitialLoadFailed(error: Throwable?) {
                Logger.e(TAG, "onInterstitialLoadFailed", error)
                displayLogs()
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
        interstitial?.prepareAd(ad)
    }

    private fun initViewModels() {
        viewModel = ViewModelProvider(this)[ApiTesterViewModel::class.java]
        adCustomizationViewModel = ViewModelProvider(this)[AdCustomizationViewModel::class.java]
    }

    private fun initViews(view: View) {

        responseInput = view.findViewById(R.id.input_response)
        oRTBBodyInput = view.findViewById(R.id.input_ortb_body)
        adSizeGroup = view.findViewById(R.id.group_ad_size)
        responseSourceGroup = view.findViewById(R.id.group_response_source)
        markupList = view.findViewById(R.id.list_markup)
        markupList.isNestedScrollingEnabled = false
        loadButton = view.findViewById(R.id.button_load)
        showButton = view.findViewById(R.id.button_show)
        oRTBLayout = view.findViewById(R.id.layout_ortb_body)
        adCustomizationLayout = view.findViewById(R.id.ad_customisation_layout)
        enableAdCustomisationCheckbox = view.findViewById(R.id.cb_enable_customization)
        customizeButton = view.findViewById(R.id.customize_button)

        markupList.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        markupList.itemAnimator = DefaultItemAnimator()
        markupList.adapter = adapter
    }

    private fun initObservers() {


        viewModel.clipboard.observe(viewLifecycleOwner) {
            responseInput.setText(it)
        }

        viewModel.clipboardBody.observe(viewLifecycleOwner) {
            oRTBBodyInput.setText(it)
        }

        viewModel.listVisibility.observe(viewLifecycleOwner) {
            if (it) markupList.visibility = View.VISIBLE
            else markupList.visibility = View.GONE
        }

        viewModel.showButtonVisibility.observe(viewLifecycleOwner) {
            if (it) showButton.visibility = View.VISIBLE
            else showButton.visibility = View.GONE
        }

        viewModel.loadInterstitial.observe(viewLifecycleOwner) {
            loadInterstitial(it)
        }

        viewModel.loadRewarded.observe(viewLifecycleOwner) {
            loadRewarded(it)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            Toast.makeText(
                context,
                it,
                Toast.LENGTH_SHORT
            ).show()
        }

        viewModel.adapterUpdate.observe(viewLifecycleOwner) {
            adapter.refreshWithAd(it, viewModel.getAdSize())
        }

        adCustomizationViewModel.onAdLoaded.observe(viewLifecycleOwner) { ad ->
            viewModel.handleAdResult(ad)
        }

        adCustomizationViewModel.onAdLoadFailed.observe(viewLifecycleOwner) {
            Toast.makeText(
                context,
                it,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setListeners() {

        view?.findViewById<ImageButton>(R.id.button_paste_clipboard)?.setOnClickListener {
            viewModel.pasteFromClipboard()
        }

        view?.findViewById<ImageButton>(R.id.button_paste_clipboard_body)?.setOnClickListener {
            viewModel.pasteFromClipboardBody()
        }

        loadButton.setOnClickListener {
            cleanLogs()
            if (adCustomisationEnabled && viewModel.getMarkupType() != MarkupType.ORTB_BODY && viewModel.getAdSize() != NATIVE) {
                loadCustomizedAd()
            } else {
                loadAd()
            }
        }

        showButton.setOnClickListener {
            when (viewModel.getAdSize()) {
                INTERSTITIAL -> {
                    interstitial?.show()
                }

                REWARDED -> {
                    rewardedAd?.show()
                }

                else -> {}
            }
        }

        adSizeGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_size_banner -> {
                    viewModel.setAdSize(BANNER)
                }

                R.id.radio_size_medium -> {
                    viewModel.setAdSize(MEDIUM)
                }

                R.id.radio_size_leaderboard -> {
                    viewModel.setAdSize(LEADERBOARD)
                }

                R.id.radio_size_native -> {
                    viewModel.setAdSize(NATIVE)
                }

                R.id.radio_size_interstitial -> {
                    viewModel.setAdSize(INTERSTITIAL)
                }

                R.id.radio_size_rewarded -> {
                    viewModel.setAdSize(REWARDED)
                }
            }
            showAdCustomisationLayoutIfAvalable()
            showButton.isEnabled = false
        }

        responseSourceGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_markup -> {
                    showORTBEditText(false)
                    viewModel.setMarkupType(MarkupType.CUSTOM_MARKUP)
                }

                R.id.radio_url -> {
                    showORTBEditText(false)
                    viewModel.setMarkupType(MarkupType.URL)
                }

                R.id.radio_ortb -> {
                    showORTBEditText(true)
                    viewModel.setMarkupType(MarkupType.ORTB_BODY)
                }
            }
            showAdCustomisationLayoutIfAvalable()
        }

        customizeButton.setOnClickListener {
            if (adCustomisationEnabled) {
                val intent = Intent(context, AdCustomizationActivity::class.java)
                startActivity(intent)
            }
        }

        enableAdCustomisationCheckbox.setOnCheckedChangeListener { compoundButton, isChecked ->
            adCustomisationEnabled = isChecked
            customizeButton.isEnabled = adCustomisationEnabled
        }
    }

    private fun loadRewarded(ad: Ad?) {
        rewardedAd?.destroy()

        val rewardelListener = object : HyBidRewardedAd.Listener {

            override fun onRewardedLoaded() {
                Logger.d(TAG, "onRewardedLoaded")
                displayLogs()
                showButton.isEnabled = true
            }

            override fun onRewardedLoadFailed(error: Throwable?) {
                Logger.e(TAG, "onRewardedLoadFailed", error)
                displayLogs()
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

        rewardedAd = HyBidRewardedAd(requireActivity(), rewardelListener)
        rewardedAd?.prepareAd(ad)
    }

    private fun loadAd() {
        showButton.isEnabled = false
        viewModel.loadApiAd(responseInput.text.toString(), oRTBBodyInput.text.toString())
    }

    private fun loadCustomizedAd() {

        showButton.isEnabled = false
        var isReworded = false;
        val adSize: AdSize?

        when (viewModel.getAdSize()) {
            BANNER -> {
                adSize = AdSize.SIZE_300x50
            }

            MEDIUM -> {
                adSize = AdSize.SIZE_300x250
            }

            LEADERBOARD -> {
                adSize = AdSize.SIZE_728x90
            }

            INTERSTITIAL -> {
                adSize = AdSize.SIZE_INTERSTITIAL
            }

            REWARDED -> {
                adSize = AdSize.SIZE_INTERSTITIAL; isReworded = true
            }

            NATIVE -> {
                adSize = null
            }
        }

        if (viewModel.getMarkupType() == MarkupType.CUSTOM_MARKUP) {
            adCustomizationViewModel.loadCustomizedAd(
                responseInput.text.toString(),
                adSize,
                isReworded,
                "",
                Constants.AdmType.API_V3
            )
        } else if (viewModel.getMarkupType() == MarkupType.URL) {
            adCustomizationViewModel.loadCustomizedAdFromUrl(
                responseInput.text.toString(),
                adSize,
                isReworded,
                "",
                Constants.AdmType.API_V3
            )
        }
    }

    override fun onDestroy() {
        interstitial?.destroy()
        rewardedAd?.destroy()
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
            activity.clearTrackerList()
            activity.clearRequestUrlString()
            activity.notifyAdCleaned()
        }
    }

    private fun showORTBEditText(rtbEditTextEnabled: Boolean) {
        if (rtbEditTextEnabled) {
            oRTBLayout.visibility = View.VISIBLE
        } else {
            oRTBLayout.visibility = View.GONE
        }
    }

    private fun showAdCustomisationLayoutIfAvalable() {
        if (viewModel.getMarkupType() != MarkupType.ORTB_BODY && viewModel.getAdSize() != NATIVE) {
            adCustomizationLayout.visibility = View.VISIBLE
        } else {
            adCustomizationLayout.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        adCustomizationViewModel.refetchAdCustomisationParams()
    }
}