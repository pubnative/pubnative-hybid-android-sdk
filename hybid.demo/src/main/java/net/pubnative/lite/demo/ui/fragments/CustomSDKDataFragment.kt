package net.pubnative.lite.demo.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.models.SDKDataItem
import net.pubnative.lite.demo.ui.adapters.CustomSDKDataAdapter
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.models.IntegrationType

class CustomSDKDataFragment : Fragment(R.layout.fragment_custom_sdk_data) {
    private lateinit var itemList: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemList = view.findViewById(R.id.list_custom_data)

        val adapter = CustomSDKDataAdapter(getItems())

        itemList.itemAnimator = DefaultItemAnimator()
        itemList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        itemList.adapter = adapter
    }

    private fun getItems(): List<SDKDataItem> {
        val list = mutableListOf<SDKDataItem>()
        list.add(SDKDataItem("SDK version info", HyBid.getSDKVersionInfo()))
        list.add(SDKDataItem("Custom request signal data", HyBid.getCustomRequestSignalData("m")))
        return list
    }
}