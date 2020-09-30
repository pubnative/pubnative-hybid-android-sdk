package net.pubnative.lite.demo.ui.fragments.markup

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.adapters.MarkupAdapter
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd
import net.pubnative.lite.sdk.utils.Logger

class MarkupFragment : Fragment() {
    private val TAG = MarkupFragment::class.java.simpleName

    private lateinit var markupInput: EditText
    private lateinit var adSizeGroup: RadioGroup
    private lateinit var markupList: RecyclerView
    private val adapter = MarkupAdapter()

    private var selectedSize: Int = R.id.radio_size_banner

    private var interstitial: HyBidInterstitialAd? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_markup, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        markupInput = view.findViewById(R.id.input_markup)
        adSizeGroup = view.findViewById(R.id.group_ad_size)
        markupList = view.findViewById(R.id.list_markup)

        view.findViewById<ImageButton>(R.id.button_paste_clipboard).setOnClickListener {
            pasteFromClipboard()
        }

        view.findViewById<Button>(R.id.button_load).setOnClickListener {
            loadMarkup()
        }

        adSizeGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedSize = checkedId
            updateListVisibility()
        }

        markupList.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        markupList.itemAnimator = DefaultItemAnimator()
        markupList.adapter = adapter
    }

    override fun onDestroy() {
        interstitial?.destroy()
        super.onDestroy()
    }

    private fun updateListVisibility() {
        if (selectedSize == R.id.radio_size_banner
                || selectedSize == R.id.radio_size_medium
                || selectedSize == R.id.radio_size_leaderboard) {
            markupList.visibility = View.VISIBLE
        } else {
            markupList.visibility = View.GONE
        }
    }

    private fun pasteFromClipboard() {
        val clipboardText = ClipboardUtils.copyFromClipboard(activity!!)
        if (!TextUtils.isEmpty(clipboardText)) {
            markupInput.setText(clipboardText)
        }
    }

    private fun loadMarkup() {
        val markup = markupInput.text.toString()
        if (TextUtils.isEmpty(markup)) {
            Toast.makeText(activity, "Please input some markup", Toast.LENGTH_SHORT).show()
        } else {
            if (selectedSize == R.id.radio_size_interstitial) {
                loadInterstitial(markup)
            } else {
                adapter.refreshWithMarkup(markup, selectedSize)
            }
        }
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
}