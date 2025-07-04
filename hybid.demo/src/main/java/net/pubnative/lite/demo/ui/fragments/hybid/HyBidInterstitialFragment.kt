// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.fragments.hybid

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.demo.viewmodel.InterstitialViewModel

class HyBidInterstitialFragment : Fragment(R.layout.fragment_hybid_interstitial) {

    private var isLoadingAd: Boolean = false
    val TAG = HyBidInterstitialFragment::class.java.simpleName

    private val interstitialViewModel: InterstitialViewModel by viewModels()

    private var zoneId: String? = null

    private lateinit var loadButton: Button
    private lateinit var prepareButton: Button
    private lateinit var showButton: Button
    private lateinit var cachingCheckbox: SwitchCompat
    private lateinit var apiRadioGroup: RadioGroup
    private lateinit var formatRadioGroup: RadioGroup
    private lateinit var errorCodeView: TextView
    private lateinit var errorView: TextView
    private lateinit var creativeIdView: TextView
    private lateinit var adFormatLayout: LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)
        initViews(view)
        initListeners()
        initObservers()
    }

    private fun initViews(view: View) {
        errorView = view.findViewById(R.id.view_error)
        errorCodeView = view.findViewById(R.id.view_error_code)
        creativeIdView = view.findViewById(R.id.view_creative_id)
        loadButton = view.findViewById(R.id.button_load)
        prepareButton = view.findViewById(R.id.button_prepare)
        cachingCheckbox = view.findViewById(R.id.check_caching)
        showButton = view.findViewById(R.id.button_show)
        apiRadioGroup = view.findViewById(R.id.group_api_type)
        formatRadioGroup = view.findViewById(R.id.group_ad_format)
        adFormatLayout = view.findViewById(R.id.linear_ad_format)
        prepareButton.isEnabled = false
        showButton.isEnabled = false
    }

    private fun initListeners() {

        loadButton.setOnClickListener {
            cleanLogs()
            prepareButton.isEnabled = false
            showButton.isEnabled = false
            if (!isLoadingAd) fireLoadClicked()
        }

        prepareButton.setOnClickListener {
            interstitialViewModel.prepareAd()
        }

        showButton.setOnClickListener {
            val activity = activity as TabActivity
            activity.cacheEventList()
            interstitialViewModel.showAd()
            showButton.isEnabled = false
        }

        cachingCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != interstitialViewModel.cachingEnabled) interstitialViewModel.reset()
            interstitialViewModel.cachingEnabled = isChecked
            val activity = activity as TabActivity
            activity.cacheEventList()
            prepareButton.visibility = if (isChecked) View.GONE else View.VISIBLE
        }

        errorView.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                requireActivity(), errorView.text.toString()
            )
        }

        creativeIdView.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                requireActivity(), creativeIdView.text.toString()
            )
        }

        apiRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radio_api_ortb) {
                adFormatLayout.visibility = View.VISIBLE
            } else {
                adFormatLayout.visibility = View.GONE
            }
        }
    }

    private fun fireLoadClicked() {
        isLoadingAd = true
        cleanLogs()
        val activity = activity as TabActivity
        activity.notifyAdCleaned()
        interstitialViewModel.loadAd(activity, zoneId, apiRadioGroup.checkedRadioButtonId,
            formatRadioGroup.checkedRadioButtonId)
    }

    private fun initObservers() {

        interstitialViewModel.interstitialLoadLiveData.observe(viewLifecycleOwner) { isLoaded ->
            if (isLoaded) {
                prepareButton.isEnabled = !interstitialViewModel.cachingEnabled
                showButton.isEnabled = interstitialViewModel.cachingEnabled
            } else {
                prepareButton.isEnabled = false
                showButton.isEnabled = false
            }
            if (isLoadingAd) {
                displayLogs()
                isLoadingAd = false
            }
        }

        interstitialViewModel.cacheLiveData.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                prepareButton.isEnabled = false
                showButton.isEnabled = true
            } else {
                prepareButton.isEnabled = false
                showButton.isEnabled = true
            }
        }

        interstitialViewModel.errorCodeLiveData.observe(viewLifecycleOwner) { errorCode ->
            errorCodeView.text = errorCode
        }

        interstitialViewModel.errorMessageLiveData.observe(viewLifecycleOwner) { errorMessage ->
            errorView.text = errorMessage
        }

        interstitialViewModel.creativeIdLiveData.observe(viewLifecycleOwner) { creativeId ->
            creativeIdView.text = creativeId
        }
    }

    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }

    private fun cleanLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdCleaned()
        }
    }
}