package net.pubnative.lite.demo.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.adapters.UrlInspectorAdapter
import java.util.TreeMap


class UrlInspectorFragment : Fragment(R.layout.fragment_url_inspector) {
    private lateinit var itemList: RecyclerView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemList = view.findViewById(R.id.list_url_data)

        val adapter = UrlInspectorAdapter(getParameterValuesFromUrl())

        itemList.itemAnimator = DefaultItemAnimator()
        itemList.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        itemList.adapter = adapter
    }

    private fun getParameterValuesFromUrl(): TreeMap<String, String> {
        val url = requireActivity().intent.getStringExtra("requestUrl")
        val uri = Uri.parse(url)
        val map = TreeMap<String, String>()
        for (paramName in uri.queryParameterNames) {
            if (paramName != null) {
                val paramValue = uri.getQueryParameter(paramName)
                if (paramValue != null) {
                    map[paramName] = paramValue
                }
            }
        }
        return map
    }
}