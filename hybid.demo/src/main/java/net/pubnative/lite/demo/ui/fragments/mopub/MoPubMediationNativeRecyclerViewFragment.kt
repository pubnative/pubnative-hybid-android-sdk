package net.pubnative.lite.demo.ui.fragments.mopub

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mopub.nativeads.*
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.adapters.QuoteAdapter


class MoPubMediationNativeRecyclerViewFragment : Fragment(), MoPubNativeAdLoadedListener{
    val TAG = MoPubMediationNativeRecyclerViewFragment::class.java.simpleName

    private var zoneId: String? = null
    private lateinit var loadButton: Button
    private lateinit var errorView: TextView

    private lateinit var recyclerView: RecyclerView
    private lateinit var quoteAdapter: QuoteAdapter
    private lateinit var mopubRecyclerAdapter: MoPubRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_mopub_native_recycler_view, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorView = view.findViewById(R.id.view_error)
        loadButton = view.findViewById(R.id.button_load)
        recyclerView = view.findViewById(R.id.list)

        val adUnitId = SettingsManager.getInstance(requireActivity()).getSettings().mopubMediationNativeAdUnitId

        val viewBinder = ViewBinder.Builder(R.layout.layout_native_ad)
                .mainImageId(R.id.ad_banner)
                .iconImageId(R.id.ad_icon)
                .titleId(R.id.ad_title)
                .textId(R.id.ad_description)
                .privacyInformationIconImageId(R.id.ad_choices)
                .callToActionId(R.id.ad_call_to_action)
                .build()

        val adRenderer = MoPubStaticNativeAdRenderer(viewBinder)

        val adPositioning = MoPubNativeAdPositioning.clientPositioning()
        adPositioning.addFixedPosition(4)

        quoteAdapter = QuoteAdapter()
        mopubRecyclerAdapter = MoPubRecyclerAdapter(requireActivity(), quoteAdapter, adPositioning)

        mopubRecyclerAdapter.registerAdRenderer(adRenderer)

        recyclerView.layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = mopubRecyclerAdapter

        mopubRecyclerAdapter.setAdLoadedListener(this)

        loadButton.setOnClickListener {
            mopubRecyclerAdapter.loadAds(adUnitId)
        }
    }

    override fun onDestroy() {
        mopubRecyclerAdapter.destroy()
        super.onDestroy()
    }

    override fun onAdLoaded(position: Int) {
        displayLogs()
        Log.d(TAG, "onAdLoaded")
    }

    override fun onAdRemoved(position: Int) {

    }

    private fun displayLogs() {
        if (activity != null) {
            val activity = activity as TabActivity
            activity.notifyAdUpdated()
        }
    }
}