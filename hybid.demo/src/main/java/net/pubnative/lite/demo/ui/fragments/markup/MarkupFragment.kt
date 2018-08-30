package net.pubnative.lite.demo.ui.fragments.markup

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.adapters.MarkupAdapter
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.mraid.*
import net.pubnative.lite.sdk.utils.Logger
import net.pubnative.lite.sdk.utils.UrlHandler
import net.pubnative.lite.sdk.utils.text.StringEscapeUtils

class MarkupFragment : Fragment() {
    private val TAG = MarkupFragment::class.java.simpleName

    private lateinit var markupInput: EditText
    private lateinit var adSizeGroup: RadioGroup
    private lateinit var markupList: RecyclerView
    private val adapter = MarkupAdapter()
    private val urlHandler = UrlHandler(activity)

    private var selectedSize: Int = R.id.radio_size_banner

    private var interstitial: MRAIDInterstitial? = null

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

        adSizeGroup.setOnCheckedChangeListener { group, checkedId ->
            selectedSize = checkedId
            updateListVisibility()
        }

        markupList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        markupList.itemAnimator = DefaultItemAnimator()
        markupList.adapter = adapter
    }

    override fun onDestroy() {
        interstitial?.destroy()
        super.onDestroy()
    }

    private fun updateListVisibility() {
        if (selectedSize == R.id.radio_size_banner || selectedSize == R.id.radio_size_medium) {
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
                loadInterstitial(StringEscapeUtils.unescapeJava(markup))
            } else {
                adapter.refreshWithMarkup(StringEscapeUtils.unescapeJava(markup), selectedSize)
            }
        }
    }

    private fun loadInterstitial(markup: String) {
        interstitial?.destroy()

        val supportedFeatures = arrayOf(
                MRAIDNativeFeature.INLINE_VIDEO,
                MRAIDNativeFeature.CALENDAR,
                MRAIDNativeFeature.SMS,
                MRAIDNativeFeature.STORE_PICTURE,
                MRAIDNativeFeature.TEL
        )

        val mraidViewListener = object : MRAIDViewListener {
            override fun mraidViewLoaded(mraidView: MRAIDView?) {
                Logger.d(TAG, "mraidViewLoaded")
                interstitial?.show(activity)
            }

            override fun mraidViewResize(mraidView: MRAIDView?, width: Int, height: Int, offsetX: Int, offsetY: Int): Boolean {
                Logger.d(TAG, "mraidViewResize")
                return true
            }

            override fun mraidViewExpand(mraidView: MRAIDView?) {
                Logger.d(TAG, "mraidViewExpand")
            }

            override fun mraidViewClose(mraidView: MRAIDView?) {
                Logger.d(TAG, "mraidViewClose")
            }
        }

        val mraidNativeListener = object : MRAIDNativeFeatureListener {
            override fun mraidNativeFeatureCallTel(url: String?) {
                Logger.d(TAG, "mraidNativeFeatureCallTel")
            }

            override fun mraidNativeFeatureCreateCalendarEvent(eventJSON: String?) {
                Logger.d(TAG, "mraidNativeFeatureCreateCalendarEvent")
            }

            override fun mraidNativeFeatureOpenBrowser(url: String?) {
                Logger.d(TAG, "mraidNativeFeatureOpenBrowser")
                urlHandler.handleUrl(url)
            }

            override fun mraidNativeFeaturePlayVideo(url: String?) {
                Logger.d(TAG, "mraidNativeFeaturePlayVideo")
            }

            override fun mraidNativeFeatureSendSms(url: String?) {
                Logger.d(TAG, "mraidNativeFeatureSendSms")
            }

            override fun mraidNativeFeatureStorePicture(url: String?) {
                Logger.d(TAG, "mraidNativeFeatureStorePicture")
            }
        }

        val emptyContentInfo = FrameLayout(activity)

        interstitial = MRAIDInterstitial(activity,
                "",
                markup,
                supportedFeatures,
                mraidViewListener,
                mraidNativeListener,
                emptyContentInfo)
    }
}