package net.pubnative.lite.demo.ui.fragments.markup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.adapters.MarkupAdapter
import net.pubnative.lite.demo.ui.adapters.OnAdRefreshListener
import net.pubnative.lite.demo.ui.adapters.OnLogDisplayListener
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd
import net.pubnative.lite.sdk.utils.Logger

class MarkupFragment : Fragment(R.layout.fragment_markup), OnLogDisplayListener,
    OnAdRefreshListener {

    private lateinit var markupViewModel: MarkupViewModel

    private lateinit var markupInput: EditText
    private lateinit var adSizeGroup: RadioGroup
    private lateinit var groupMarkupType: RadioGroup
    private lateinit var creativeIdLabel: TextView
    private lateinit var creativeIdView: TextView
    private lateinit var markupList: RecyclerView
    private lateinit var urWrapCheckbox: CheckBox

    private val adapter = MarkupAdapter(this, this)

    private var interstitial: HyBidInterstitialAd? = null

    private val TAG = MarkupFragment::class.java.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        markupViewModel = ViewModelProvider(this)[MarkupViewModel::class.java]

        markupInput = view.findViewById(R.id.input_markup)
        adSizeGroup = view.findViewById(R.id.group_ad_size)
        groupMarkupType = view.findViewById(R.id.group_markup_type)
        creativeIdLabel = view.findViewById(R.id.label_creative_id)
        creativeIdView = view.findViewById(R.id.view_creative_id)
        urWrapCheckbox = view.findViewById(R.id.check_ur_wrap)
        markupList = view.findViewById(R.id.list_markup)
        markupList.isNestedScrollingEnabled = false

        markupViewModel.clipboard.observe(viewLifecycleOwner) {
            markupInput.setText(it)
        }

        markupViewModel.listVisibillity.observe(viewLifecycleOwner) {
            if (it) markupList.visibility = View.VISIBLE
            else markupList.visibility = View.GONE
        }

        markupViewModel.creativeIdVisibillity.observe(viewLifecycleOwner) {
            if (it) {
                creativeIdLabel.visibility = View.VISIBLE
                creativeIdView.visibility = View.VISIBLE
                creativeIdView.setOnClickListener {
                    openUrlInExternalBrowser()
                }
            } else {
                creativeIdLabel.visibility = View.GONE
                creativeIdView.visibility = View.GONE
                creativeIdView.setOnClickListener(null)
            }
        }

        markupViewModel.creativeId.observe(viewLifecycleOwner) {
            creativeIdView.text = it
        }

        markupViewModel.loadInterstitial.observe(viewLifecycleOwner) {
            loadInterstitial(it)
        }

        markupViewModel.adapterUpdate.observe(viewLifecycleOwner) {
            adapter.refreshWithMarkup(it, markupViewModel.getMarkupSize())
        }

        view.findViewById<ImageButton>(R.id.button_paste_clipboard).setOnClickListener {
            markupViewModel.pasteFromClipboard()
        }

        view.findViewById<Button>(R.id.button_load).setOnClickListener {
            cleanLogs()
            if (activity != null) {
                val activity = activity as TabActivity
                activity.registerReportingCallback()
            }
            loadMarkup()
        }

        adSizeGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_size_banner -> {
                    markupViewModel.setMarkupSize(MarkupSize.BANNER)
                }

                R.id.radio_size_medium -> {
                    markupViewModel.setMarkupSize(MarkupSize.MEDIUM)
                }

                R.id.radio_size_leaderboard -> {
                    markupViewModel.setMarkupSize(MarkupSize.LEADERBOARD)
                }

                R.id.radio_size_interstitial -> {
                    markupViewModel.setMarkupSize(MarkupSize.INTERSTITIAL)
                }
            }
        }

        groupMarkupType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_markup -> {
                    markupViewModel.setMarkupType(MarkupType.CUSTOM_MARKUP)
                }

                R.id.radio_url -> {
                    markupViewModel.setMarkupType(MarkupType.URL)
                }
            }
        }

        urWrapCheckbox.setOnCheckedChangeListener { _, isChecked ->
            markupViewModel.setURWrap(isChecked)
        }

        markupList.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        markupList.itemAnimator = DefaultItemAnimator()
        markupList.adapter = adapter

        fetchURTemplate()
    }


    private fun loadInterstitial(markup: String) {
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
        interstitial?.prepareCustomMarkup(markup)
    }

    private fun loadMarkup() {
        markupViewModel.loadMarkup(markupText = markupInput.text.toString())
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

    override fun onAdRefresh() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.removeReportingCallback()
        }
    }

    private fun fetchURTemplate() {
        val urTemplate = requireContext().assets.open("ur.html").bufferedReader().use {
            it.readText()
        }
        markupViewModel.setURTemplate(urTemplate)
    }

    private fun openUrlInExternalBrowser() {
        if (groupMarkupType.checkedRadioButtonId == R.id.radio_url) {
            val url = markupInput.text.toString()
            val creativeId = creativeIdView.text.toString()
            if (url.isNotEmpty() && creativeId.isNotEmpty()) {
                val creativeIdQueryParam = "crid"
                val uri = Uri.parse(url)
                val param = uri.getQueryParameter(creativeIdQueryParam)
                val finalUrl = if (param != null) {
                    uri
                } else {
                    uri.buildUpon().appendQueryParameter(creativeIdQueryParam, creativeId)
                        .build()
                }

                finalUrl?.let {
                    openWebPage(it)
                }
            }
        }
    }

    private fun openWebPage(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }
}