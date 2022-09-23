package net.pubnative.lite.demo.ui.fragments

import android.os.Bundle
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
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.adapters.ReportingEventAdapter
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.analytics.ReportingEvent

class DebugFragment : Fragment(R.layout.fragment_debug) {

    private var requestView: TextView? = null
    private var latencyView: TextView? = null
    private var responseView: TextView? = null
    private var eventReportButton: Button? = null

    private var eventReportDialog: AlertDialog? = null
    private var eventReportAdapter: ReportingEventAdapter? = null

    private var eventList: ArrayList<ReportingEvent>? = ArrayList()
    private var isOpenedEventReport: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initObservers()
        setOnClickListeners()
        handleSaveInstanse(savedInstanceState)
    }

    private fun initViews(){
        initEventReportDialog()
        requestView = requireView().findViewById(R.id.view_request_url)
        latencyView = requireView().findViewById(R.id.view_latency)
        responseView = requireView().findViewById(R.id.view_response)
        eventReportButton = requireView().findViewById(R.id.button_event_report)
    }

    private fun setOnClickListeners(){

        requestView?.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                requireActivity(),
                requestView?.text.toString()
            )
        }
        latencyView?.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                requireActivity(),
                latencyView?.text.toString()
            )
        }
        responseView?.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                requireActivity(),
                responseView?.text.toString()
            )
        }
        eventReportButton?.setOnClickListener {
            displayEventReport()
        }
    }

    private fun initObservers(){
        // Observe request debug info
        (requireActivity() as? TabActivity)?.debugViewModel?.requestDebugInfo?.observe(viewLifecycleOwner) {
            requestView?.text = it.requestUrl
            latencyView?.text = it.latencyMls.toString()
            responseView?.text = it.response
        }
    }

    private fun handleSaveInstanse(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            this.isOpenedEventReport = it.getBoolean("isOpenedEventReport", false)
            eventList = HyBid.getReportingController().adEventList as ArrayList<ReportingEvent>?
            if(this.isOpenedEventReport){
                displayEventReport()
            }
        }
    }

    private fun initEventReportDialog() {

        val builder = AlertDialog.Builder(requireActivity())
            .setOnDismissListener {
                isOpenedEventReport = false
            }
        eventReportAdapter = ReportingEventAdapter()
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
            adapter = eventReportAdapter
        }
        builder.setTitle(R.string.sdk_event_report)
        builder.setView(view)
        builder.setPositiveButton(R.string.action_dismiss) { dialog, _ ->
            dialog?.dismiss()
        }
        this.eventReportDialog = builder.create()
    }

    private fun displayEventReport(){
        (requireActivity() as? TabActivity)?.debugViewModel?.getEventList()?.observe(viewLifecycleOwner){
            eventReportAdapter?.submitData(it)
            eventReportDialog?.show()
            isOpenedEventReport = true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isOpenedEventReport", isOpenedEventReport)
        HyBid.getReportingController().cacheAdEventList(eventList)
    }
}