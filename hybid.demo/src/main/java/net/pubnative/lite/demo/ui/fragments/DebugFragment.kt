package net.pubnative.lite.demo.ui.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.demo.util.JsonUtils
import net.pubnative.lite.sdk.utils.AdRequestRegistry

class DebugFragment: Fragment() {

    private lateinit var requestView: TextView
    private lateinit var latencyView: TextView
    private lateinit var responseView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(R.layout.fragment_debug, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestView = view.findViewById(R.id.view_request_url)
        latencyView = view.findViewById(R.id.view_latency)
        responseView = view.findViewById(R.id.view_response)

        requestView.setOnClickListener { ClipboardUtils.copyToClipboard(activity!!, requestView.text.toString()) }
        latencyView.setOnClickListener { ClipboardUtils.copyToClipboard(activity!!, latencyView.text.toString()) }
        responseView.setOnClickListener { ClipboardUtils.copyToClipboard(activity!!, responseView.text.toString()) }
    }

    fun cleanLogs() {
        requestView.text = ""
        latencyView.text = ""
        responseView.text = ""
    }

    fun updateLogs() {
        val registryItem = AdRequestRegistry.getInstance().lastAdRequest
        if (registryItem != null) {
            requestView.text = registryItem.url
            latencyView.text = registryItem.latency.toString()

            if (!TextUtils.isEmpty(registryItem.response)) {
                responseView.text = JsonUtils.toFormattedJson(registryItem.response)
            }
        }
        AdRequestRegistry.getInstance().setLastAdRequest("", "", 0)
    }
}