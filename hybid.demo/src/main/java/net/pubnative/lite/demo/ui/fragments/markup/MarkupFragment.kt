package net.pubnative.lite.demo.ui.fragments.markup

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.adapters.MarkupAdapter
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd
import net.pubnative.lite.sdk.utils.Logger

class MarkupFragment : Fragment(R.layout.fragment_markup) {

    private lateinit var markupViewModel: MarkupViewModel

    private lateinit var markupInput: EditText
    private lateinit var adSizeGroup: RadioGroup
    private lateinit var groupMarkupType: RadioGroup
    private lateinit var markupList: RecyclerView

    private val adapter = MarkupAdapter()

    private var interstitial: HyBidInterstitialAd? = null

    private val TAG = MarkupFragment::class.java.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        markupViewModel = ViewModelProvider(this)[MarkupViewModel::class.java]

        markupInput = view.findViewById(R.id.input_markup)
        adSizeGroup = view.findViewById(R.id.group_ad_size)
        groupMarkupType = view.findViewById(R.id.group_markup_type)
        markupList = view.findViewById(R.id.list_markup)
        markupList.isNestedScrollingEnabled = false

        markupViewModel.clipboard.observe(viewLifecycleOwner) {
            markupInput.setText(it)
        }

        markupViewModel.listVisibillity.observe(viewLifecycleOwner) {
            if (it) markupList.visibility = View.VISIBLE
            else markupList.visibility = View.GONE
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

        markupList.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        markupList.itemAnimator = DefaultItemAnimator()
        markupList.adapter = adapter
    }


    private fun loadInterstitial(markup: String) {
        interstitial?.destroy()

        val interstitialListener = object : HyBidInterstitialAd.Listener {
            override fun onInterstitialLoaded() {
                Logger.d(TAG, "onInterstitialLoaded")
                interstitial?.show()
            }

            override fun onInterstitialLoadFailed(error: Throwable?) {
                Logger.e(TAG, "onInterstitialLoadFailed", error)
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
}