package net.pubnative.lite.demo.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.models.SDKDataItem
import net.pubnative.lite.demo.ui.adapters.CustomSDKDataAdapter
import net.pubnative.lite.sdk.HyBid

class ReportingEventDetailsFragment : Fragment(R.layout.fragment_reporting_event_details) {
    private lateinit var itemList: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemList = view.findViewById(R.id.list_event_data)

        val adapter = CustomSDKDataAdapter(
            getItems(
                requireActivity().intent.getBundleExtra("event_data") ?: bundleOf()
            )
        )

        itemList.itemAnimator = DefaultItemAnimator()
        itemList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        itemList.adapter = adapter


    }

    private fun getItems(bundle: Bundle): List<SDKDataItem> {
        val list = mutableListOf<SDKDataItem>()
        for (key in bundle.keySet()) {
            list.add(SDKDataItem(key, bundle.getString(key, "")))
        }
        return list
    }
}