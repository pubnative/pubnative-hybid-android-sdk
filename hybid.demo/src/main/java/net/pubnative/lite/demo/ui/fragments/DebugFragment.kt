package net.pubnative.lite.demo.ui.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.adapters.ReportingEventAdapter
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.demo.util.JsonUtils
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.analytics.Reporting
import net.pubnative.lite.sdk.analytics.ReportingEvent
import net.pubnative.lite.sdk.analytics.ReportingEventCallback
import net.pubnative.lite.sdk.utils.AdRequestRegistry

class DebugFragment : Fragment(R.layout.fragment_debug), ReportingEventCallback {

    private var requestView: TextView? = null
    private lateinit var latencyView: TextView
    private lateinit var responseView: TextView

    private var eventList = mutableListOf<ReportingEvent>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestView = view.findViewById(R.id.view_request_url)
        latencyView = view.findViewById(R.id.view_latency)
        responseView = view.findViewById(R.id.view_response)

        requestView?.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                requireActivity(),
                requestView?.text.toString()
            )
        }
        latencyView.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                requireActivity(),
                latencyView.text.toString()
            )
        }
        responseView.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                requireActivity(),
                responseView.text.toString()
            )
        }

        view.findViewById<Button>(R.id.button_event_report).setOnClickListener {
            displayEventReport()
        }

        HyBid.getReportingController()?.apply {
            addCallback(this@DebugFragment)
        }

        savedInstanceState?.let {
            val requestViewText = it.getString("requestViewText", "")
            val latencyViewText = it.getString("latencyViewText", "")
            val responseViewText = it.getString("responseViewText", "")

            requestView?.text = requestViewText
            latencyView.text = latencyViewText
            responseView.text = responseViewText

            eventList = HyBid.getReportingController().adEventList
        }
    }

    override fun onDestroyView() {
        HyBid.getReportingController().removeCallback(this)
        super.onDestroyView()
    }

    override fun onEvent(event: ReportingEvent?) {
        if (event != null) {
            if (event.eventType != null && event.eventType.equals(Reporting.EventType.REQUEST))
                clearEventList()
            eventList.add(event)
        }
    }

    fun cleanLogs() {
        clearEventList()

        if (requestView != null) {
            requestView?.text = ""
        }
        if (isLatencyViewInitializedNotNull()) {
            latencyView.text = ""
        }
        if (isResponseViewInitializedNotNull()) {
            responseView.text = ""
        }
    }

    private fun clearEventList() {
        if (eventList.isNotEmpty()) {
            eventList.clear()
            HyBid.getReportingController().clearAdEventList()
        }
    }

    fun cacheEventList() {
        HyBid.getReportingController().cacheAdEventList(eventList)
    }

    fun updateLogs() {
        val registryItem = AdRequestRegistry.getInstance().lastAdRequest
        if (registryItem != null) {
            if (requestView != null) {
                requestView?.text = registryItem.url
            }
            if (isLatencyViewInitializedNotNull()) {
                latencyView.text = registryItem.latency.toString()
            }

            if (!TextUtils.isEmpty(registryItem.response)) {
                if (isResponseViewInitializedNotNull()) {
                    responseView.text = JsonUtils.toFormattedJson(registryItem.response)
                }
            }
        }
        AdRequestRegistry.getInstance().setLastAdRequest("", "", 0)
    }

    private fun displayEventReport() {
        val builder = AlertDialog.Builder(requireActivity())
        val view = LayoutInflater.from(requireActivity())
            .inflate(R.layout.dialog_placement_events, null, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.list_events)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
            addItemDecoration(
                DividerItemDecoration(
                    requireActivity(),
                    DividerItemDecoration.VERTICAL
                )
            )
            itemAnimator = DefaultItemAnimator()
            adapter = ReportingEventAdapter(eventList)

        }
        builder.setTitle(R.string.sdk_event_report)
        builder.setView(view)
        builder.setPositiveButton(R.string.action_dismiss) { dialog, _ -> dialog?.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }

    private fun isLatencyViewInitializedNotNull(): Boolean {
        return this::latencyView.isInitialized && latencyView != null
    }

    private fun isResponseViewInitializedNotNull(): Boolean {
        return this::responseView.isInitialized && responseView != null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("requestViewText", requestView?.text.toString())
        outState.putString("latencyViewText", latencyView?.text.toString())
        outState.putString("responseViewText", responseView?.text.toString())

        HyBid.getReportingController().cacheAdEventList(eventList)
    }
}