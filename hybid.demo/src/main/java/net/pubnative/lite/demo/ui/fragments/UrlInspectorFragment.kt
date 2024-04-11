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
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
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
        getParametersFromRequestBody(map)

        return map
    }

    private fun getParametersFromRequestBody(map: TreeMap<String, String>) {
        val body = requireActivity().intent.getStringExtra("requestBody")
        val jsonBody = body?.let { JSONObject(it) }

        if (!body.isNullOrEmpty() && jsonBody != null) {
            parseJSONToTreeMap(jsonBody, map)
        }
    }

    private fun parseJSONToTreeMap(jsonObject: JSONObject, map: TreeMap<String, String>): TreeMap<String, String> {
        parseJSONObject(jsonObject, "", map)
        return map
    }

    @Throws(JSONException::class)
    private fun parseJSONObject(jsonObject: JSONObject, parentKey: String, treeMap: TreeMap<String, String>) {
        val keys = jsonObject.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = jsonObject[key]
            val fullKey = if (parentKey.isEmpty()) key else "$parentKey.$key"
            when (value) {
                is JSONObject -> parseJSONObject(value, fullKey, treeMap)
                is JSONArray -> parseJSONArray(value, fullKey, treeMap)
                else -> treeMap[fullKey] = value.toString()
            }
        }
    }

    @Throws(JSONException::class)
    private fun parseJSONArray(jsonArray: JSONArray, parentKey: String, treeMap: TreeMap<String, String>) {
        for (i in 0 until jsonArray.length()) {
            val value = jsonArray[i]
            val fullKey = "$parentKey[$i]"
            when (value) {
                is JSONObject -> parseJSONObject(value, fullKey, treeMap)
                is JSONArray -> parseJSONArray(value, fullKey, treeMap)
                else -> treeMap[fullKey] = value.toString()
            }
        }
    }
}