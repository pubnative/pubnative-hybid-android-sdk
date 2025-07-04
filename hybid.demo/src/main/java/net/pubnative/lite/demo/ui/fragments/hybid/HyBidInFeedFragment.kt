// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.fragments.hybid

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.adapters.InFeedAdapter
import net.pubnative.lite.demo.ui.listeners.InFeedAdListener
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.HyBidError
import net.pubnative.lite.sdk.models.AdSize

class HyBidInFeedFragment : Fragment(R.layout.fragment_hybid_infeed_banner), InFeedAdListener {
    private val hyBidTAG = HyBidInFeedFragment::class.java.simpleName

    private var zoneId: String? = null

    private lateinit var autoRefreshSwitch: SwitchCompat
    private lateinit var apiRadioGroup: RadioGroup
    private lateinit var loadButton: Button
    private lateinit var errorCodeView: TextView
    private lateinit var errorView: TextView
    private lateinit var adSizeSpinner: Spinner
    private lateinit var spinnerAdapter: ArrayAdapter<AdSize>
    private lateinit var creativeIdView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InFeedAdapter
    private lateinit var formatRadioGroup: RadioGroup
    private lateinit var adFormatLayout: LinearLayout

    private val adSizes = arrayOf(
        AdSize.SIZE_320x50,
        AdSize.SIZE_300x250,
        AdSize.SIZE_160x600,
        AdSize.SIZE_250x250,
        AdSize.SIZE_300x50,
        AdSize.SIZE_300x600,
        AdSize.SIZE_320x100,
        AdSize.SIZE_320x480,
        AdSize.SIZE_480x320,
        AdSize.SIZE_728x90,
        AdSize.SIZE_768x1024,
        AdSize.SIZE_1024x768
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        errorCodeView = view.findViewById(R.id.view_error_code)
        creativeIdView = view.findViewById(R.id.view_creative_id)
        loadButton = view.findViewById(R.id.button_load)
        recyclerView = view.findViewById(R.id.list)
        adSizeSpinner = view.findViewById(R.id.spinner_ad_size)
        autoRefreshSwitch = view.findViewById(R.id.check_auto_refresh)
        apiRadioGroup = view.findViewById(R.id.group_api_type)
        formatRadioGroup = view.findViewById(R.id.group_ad_format)
        adFormatLayout = view.findViewById(R.id.linear_ad_format)

        autoRefreshSwitch.isChecked = false

        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)

        adapter = InFeedAdapter(zoneId!!, this)

        val linearLayoutManager =
            object : LinearLayoutManager(activity, RecyclerView.VERTICAL, false) {
                override fun canScrollVertically() = false
            }

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter

        loadButton.setOnClickListener {
            val activity = activity as TabActivity
            activity.notifyAdCleaned()
            activity.clearEventList()
            activity.clearRequestUrlString()
            loadPNAd()
        }

        autoRefreshSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                adapter.setAutoRefresh(true)
            } else {
                adapter.setAutoRefresh(false)
            }
        }

        errorView.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                requireActivity(),
                errorView.text.toString()
            )
        }
        creativeIdView.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                requireActivity(),
                creativeIdView.text.toString()
            )
        }

        apiRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radio_api_ortb) {
                adFormatLayout.visibility = View.VISIBLE
            } else {
                adFormatLayout.visibility = View.GONE
            }
        }

        spinnerAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, adSizes)
        adSizeSpinner.adapter = spinnerAdapter
    }

    fun loadPNAd() {
        errorView.text = ""

        val adSize = adSizes[adSizeSpinner.selectedItemPosition]
        adapter.loadWithAd(adSize, apiRadioGroup.checkedRadioButtonId,
            formatRadioGroup.checkedRadioButtonId)
    }

    // --------------- InFeedAdListener Listener --------------------

    override fun onInFeedAdLoaded() {
        displayLogs()
    }

    override fun onInFeedAdLoadError(error: Throwable?) {
        if (error != null && error is HyBidError) {
            Log.e(hyBidTAG, error.message ?: " - ")
            errorCodeView.text = error.errorCode.code.toString()
            errorView.text = error.message ?: " - "
        } else {
            errorCodeView.text = " - "
            errorView.text = " - "
        }
        displayLogs()
        creativeIdView.text = ""
    }

    override fun onInFeedAdCreativeId(creativeId: String?) {
        if (!TextUtils.isEmpty(creativeId)) {
            creativeIdView.text = creativeId
        }
    }

    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }
}