// The MIT License (MIT)
//
// Copyright (c) 2018 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package net.pubnative.lite.demo.ui.fragments.hybid

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.demo.viewmodel.InterstitialViewModel

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class HyBidInterstitialFragment : Fragment(R.layout.fragment_hybid_interstitial) {

    private var isLoadingAd: Boolean = false
    val TAG = HyBidInterstitialFragment::class.java.simpleName

    private val interstitialViewModel: InterstitialViewModel by viewModels()

    private var zoneId: String? = null

    private lateinit var loadButton: Button
    private lateinit var prepareButton: Button
    private lateinit var showButton: Button
    private lateinit var cachingCheckbox: CheckBox
    private lateinit var errorCodeView: TextView
    private lateinit var errorView: TextView
    private lateinit var creativeIdView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)
        initViews()
        initListeners()
        initObservers()
    }

    private fun initViews() {
        errorView = requireView().findViewById(R.id.view_error)
        errorCodeView = requireView().findViewById(R.id.view_error_code)
        creativeIdView = requireView().findViewById(R.id.view_creative_id)
        loadButton = requireView().findViewById(R.id.button_load)
        prepareButton = requireView().findViewById(R.id.button_prepare)
        cachingCheckbox = requireView().findViewById(R.id.check_caching)
        showButton = requireView().findViewById(R.id.button_show)
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
    }

    private fun fireLoadClicked() {
        isLoadingAd = true
        cleanLogs()
        val activity = activity as TabActivity
        activity.notifyAdCleaned()
        interstitialViewModel.loadAd(activity, zoneId)
    }

    private fun initObservers() {

        interstitialViewModel.interstitialLoadLiveData.observe(viewLifecycleOwner) { isLoaded ->
            if (isLoaded) {
                Log.i("testing", interstitialViewModel.cachingEnabled.toString())
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