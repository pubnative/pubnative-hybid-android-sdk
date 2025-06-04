// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.fragments.markup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.AdCustomizationActivity
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.adapters.MarkupAdapter
import net.pubnative.lite.demo.ui.adapters.OnAdRefreshListener
import net.pubnative.lite.demo.ui.adapters.OnLogDisplayListener
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialAd
import net.pubnative.lite.sdk.rewarded.HyBidRewardedAd
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
    private lateinit var urTitleView: TextView
    private lateinit var urTemplateRadioGroup: RadioGroup
    private lateinit var loadButton: MaterialButton
    private lateinit var showButton: MaterialButton
    private lateinit var adCustomization: MaterialButton

    private var adapter: MarkupAdapter? = null

    private var interstitial: HyBidInterstitialAd? = null
    private var rewarded: HyBidRewardedAd? = null

    private var loadButtonClicked = false

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
        urTitleView = view.findViewById(R.id.universal_rendering_templates_title)
        urTemplateRadioGroup = view.findViewById(R.id.group_rendering_templates)
        markupList = view.findViewById(R.id.list_markup)
        markupList.isNestedScrollingEnabled = false
        loadButton = view.findViewById(R.id.button_load)
        showButton = view.findViewById(R.id.button_show)
        adCustomization = view.findViewById(R.id.customize_button)

        markupViewModel.clipboard.observe(viewLifecycleOwner) {
            markupInput.setText(it)
        }

        markupViewModel.listVisibility.observe(viewLifecycleOwner) {
            if (it) markupList.visibility = View.VISIBLE
            else markupList.visibility = View.GONE
        }

        markupViewModel.showButtonVisibility.observe(viewLifecycleOwner) {
            if (it) showButton.visibility = View.VISIBLE
            else showButton.visibility = View.GONE
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
            if (loadButtonClicked) {
                loadInterstitial(it)
                loadButtonClicked = false
            }
        }

        markupViewModel.loadAdInterstitial.observe(viewLifecycleOwner) {
            interstitial?.prepareAd(it)
        }

        markupViewModel.loadRewarded.observe(viewLifecycleOwner) {
            if (loadButtonClicked) {
                loadRewarded(it)
                loadButtonClicked = false
            }
        }

        markupViewModel.loadAdRewarded.observe(viewLifecycleOwner) {
            rewarded?.prepareAd(it)
        }

        markupViewModel.loadAdBanner.observe(viewLifecycleOwner) {
            adapter?.refreshWithAd(it, markupViewModel.getMarkupSize())
        }

        markupViewModel.adapterUpdate.observe(viewLifecycleOwner) {
            val configs = markupViewModel.getRemoteConfigParams()
            if (configs.isNotEmpty()) {
                markupViewModel.loadBannerRemoteConfig(it)
            } else {
                adapter?.refreshWithMarkup(
                    it,
                    markupViewModel.getMarkupSize()
                )
            }
        }

        view.findViewById<ImageButton>(R.id.button_paste_clipboard).setOnClickListener {
            markupViewModel.pasteFromClipboard()
        }

        loadButton.setOnClickListener {
            loadButtonClicked = true
            cleanLogs()
            loadMarkup()
        }

        showButton.setOnClickListener {
            when (markupViewModel.getMarkupSize()) {
                MarkupSize.INTERSTITIAL -> {
                    interstitial?.show()
                }

                MarkupSize.REWARDED -> {
                    rewarded?.show()
                }

                else -> {}
            }
        }

        adCustomization.setOnClickListener {
            val intent = Intent(context, AdCustomizationActivity::class.java)
            startActivity(intent)
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

                R.id.radio_size_rewarded -> {
                    markupViewModel.setMarkupSize(MarkupSize.REWARDED)
                }
            }
            showButton.isEnabled = false
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
            if (isChecked) {
                urTitleView.visibility = View.VISIBLE
                urTemplateRadioGroup.visibility = View.VISIBLE
            } else {
                urTitleView.visibility = View.GONE
                urTemplateRadioGroup.visibility = View.GONE
            }
        }

        urTemplateRadioGroup.setOnCheckedChangeListener { _, checkedId ->
           fetchCustomURTemplate(checkedId)
        }

        fetchURTemplate()

        adapter = MarkupAdapter(this, this)
        markupList.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        markupList.adapter = adapter
    }

    private fun loadInterstitial(markup: String) {
        showButton.isEnabled = false
        interstitial?.destroy()

        val interstitialListener = object : HyBidInterstitialAd.Listener {
            override fun onInterstitialLoaded() {
                Logger.d(TAG, "onInterstitialLoaded")
                val mainHandler = Handler(Looper.getMainLooper())

                val runnable = Runnable {
                    displayLogs()
                    showButton.isEnabled = true
                }

                mainHandler.post(runnable)
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

        val configs = markupViewModel.getRemoteConfigParams()
        if (configs.isNotEmpty()) {
            markupViewModel.loadInterstitialRemoteConfig(markup)
        } else {
            interstitial?.prepareCustomMarkup(markup)
        }
    }

    private fun loadRewarded(markup: String) {
        showButton.isEnabled = false
        rewarded?.destroy()

        val rewardedListener = object : HyBidRewardedAd.Listener {
            override fun onRewardedLoaded() {
                Logger.d(TAG, "onRewardedLoaded")
                val mainHandler = Handler(Looper.getMainLooper())

                val runnable = Runnable {
                    displayLogs()
                    showButton.isEnabled = true
                }

                mainHandler.post(runnable)
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

        rewarded = HyBidRewardedAd(requireActivity(), rewardedListener)
        val configs = markupViewModel.getRemoteConfigParams()
        if (configs.isNotEmpty()) {
            markupViewModel.loadRewardedRemoteConfig(markup)
        } else {
            rewarded?.prepareCustomMarkup(markup)
        }
    }

    private fun loadMarkup() {
        markupViewModel.loadMarkup(markupText = markupInput.text.toString())
    }

    override fun onDestroy() {
        interstitial?.destroy()
        rewarded?.destroy()
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
            activity.clearRequestUrlString()
            activity.notifyAdCleaned()
        }
    }

    override fun onAdRefresh() {

    }

    private fun fetchURTemplate() {
        val urTemplate = requireContext().assets.open("ur.html").bufferedReader().use {
            it.readText()
        }
        markupViewModel.setURTemplate(urTemplate)
    }

    private fun fetchCustomURTemplate(checkedId: Int) {
        val templateFileName = when (checkedId) {
            R.id.radio_template_default -> "ur.html"
            R.id.radio_template_final -> "ur_template_final.html"
            R.id.radio_template_one -> "ur_template_one.html"
            R.id.radio_template_two -> "ur_template_two.html"
            R.id.radio_template_three -> "ur_template_three.html"
            R.id.radio_template_four -> "ur_template_four.html"
            R.id.radio_template_five -> "ur_template_five.html"
            R.id.radio_template_six -> "ur_template_six.html"
            R.id.radio_template_seven -> "ur_template_seven.html"
            R.id.radio_template_new -> "ur_template_new.html"
            R.id.radio_template_four_without_container -> "ur_template_four_without_container.html"
            R.id.radio_template_one_pre_rendering -> "ur_template_one_pre_rendering.html"
            R.id.radio_pnadtag -> "pn-ad-tag.html"

            else -> "ur.html"
        }

        val urlTemplate =
            requireContext().assets.open(templateFileName).bufferedReader().use {
                it.readText()
            }

        markupViewModel.setURTemplate(urlTemplate)
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
                    uri.buildUpon().appendQueryParameter(creativeIdQueryParam, creativeId).build()
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

    override fun onResume() {
        super.onResume()
        markupViewModel.refetchAdCustomisationParams()
    }
}