package net.pubnative.lite.demo.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.activities.UrlInspectorActivity
import net.pubnative.lite.demo.ui.adapters.ReportingEventAdapter
import net.pubnative.lite.demo.ui.adapters.ReportingTrackerAdapter
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.analytics.ReportingEvent

class DebugFragment : Fragment(R.layout.fragment_debug) {

    private var requestView: TextView? = null
    private var latencyView: TextView? = null
    private var responseView: TextView? = null
    private var postBodyView: TextView? = null
    private var postBodyViewLabel: TextView? = null
    private var eventReportButton: Button? = null
    private var trackerReportButton: Button? = null
    private var urlInspectorButton: Button? = null

    private var eventReportDialog: AlertDialog? = null
    private var trackersReportDialog: AlertDialog? = null
    private var eventReportAdapter: ReportingEventAdapter? = null
    private var trackerReportAdapter: ReportingTrackerAdapter? = null

    private var eventList: ArrayList<ReportingEvent>? = ArrayList()
    private var isOpenedEventReport: Boolean = false
    private var isOpenedTrackerReport: Boolean = false
    private var isOpenedUrlInspector: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initObservers()
        setOnClickListeners()
        handleSaveInstance(savedInstanceState)
    }

    private fun initViews() {
        initEventReportDialog()
        initTrackersReportDialog()
        requestView = requireView().findViewById(R.id.view_request_url)
        latencyView = requireView().findViewById(R.id.view_latency)
        responseView = requireView().findViewById(R.id.view_response)
        postBodyView = requireView().findViewById(R.id.view_post_body)
        postBodyViewLabel = requireView().findViewById(R.id.view_post_body_label)
        eventReportButton = requireView().findViewById(R.id.button_event_report)
        trackerReportButton = requireView().findViewById(R.id.button_tracker_report)
        urlInspectorButton = requireView().findViewById(R.id.button_url_inspector)
    }

    private fun setOnClickListeners() {

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
        postBodyView?.setOnClickListener {
            ClipboardUtils.copyToClipboard(
                requireActivity(),
                postBodyView?.text.toString()
            )
        }
        eventReportButton?.setOnClickListener {
            displayEventReport()
        }
        trackerReportButton?.setOnClickListener {
            displayTrackerReport()
        }
        urlInspectorButton?.setOnClickListener {
            displayUrlInspector()
        }
    }

    private fun initObservers() {
        // Observe request debug info
        (requireActivity() as? TabActivity)?.debugViewModel?.requestDebugInfo?.observe(
            viewLifecycleOwner
        ) {
            requestView?.text = it.requestUrl
            latencyView?.text = it.latencyMls.toString()
            responseView?.text = it.response
            if (it.requestPostBody.isNullOrEmpty()) {
                postBodyView!!.visibility = View.GONE
                postBodyViewLabel!!.visibility = View.GONE
            } else {
                postBodyView!!.visibility = View.VISIBLE
                postBodyViewLabel!!.visibility = View.VISIBLE
                postBodyView!!.text = it.requestPostBody
            }
        }
    }

    private fun handleSaveInstance(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            this.isOpenedEventReport = it.getBoolean("isOpenedEventReport", false)
            eventList = HyBid.getReportingController().adEventList as ArrayList<ReportingEvent>?
            if (this.isOpenedEventReport) {
                displayEventReport()
            }
            this.isOpenedTrackerReport = it.getBoolean("isOpenedTrackerReport", false)
            if (this.isOpenedTrackerReport) {
                displayTrackerReport()
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

    private fun initTrackersReportDialog() {

        val builder = AlertDialog.Builder(requireActivity())
            .setOnDismissListener {
                isOpenedEventReport = false
            }
        trackerReportAdapter = ReportingTrackerAdapter()
        val view = LayoutInflater.from(requireActivity())
            .inflate(R.layout.dialog_fired_trackers, null, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.list_trackers)
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
            adapter = trackerReportAdapter
        }
        builder.setTitle(R.string.tracker_report)
        builder.setView(view)
        builder.setPositiveButton(R.string.action_dismiss) { dialog, _ ->
            dialog?.dismiss()
        }
        this.trackersReportDialog = builder.create()
    }

    private fun displayEventReport() {
        (requireActivity() as? TabActivity)?.debugViewModel?.getEventList()
            ?.observe(viewLifecycleOwner) {
                eventReportAdapter?.submitData(it)
                eventReportDialog?.show()
                isOpenedEventReport = true
            }
    }

    private fun displayTrackerReport() {
        (requireActivity() as? TabActivity)?.debugViewModel?.getFiredTrackersList()
            ?.observe(viewLifecycleOwner) {
                trackerReportAdapter?.submitData(it)
                trackersReportDialog?.show()
                isOpenedTrackerReport = true
            }
    }

    private fun displayUrlInspector() {
        (requireActivity() as? TabActivity)?.debugViewModel?.getRequestUri()
            ?.observe(viewLifecycleOwner) {
                val intent = Intent(context, UrlInspectorActivity::class.java)

                if (it != null) {
                    intent.putExtra("requestUrl", it)
                    addOrtbRequestBody(intent)
                    context?.startActivity(intent)
                    isOpenedUrlInspector = true
                } else {
                    Toast.makeText(context, "A request must be done first", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addOrtbRequestBody(intent: Intent) {
        if (!postBodyView!!.text.isNullOrEmpty()) {
            intent.putExtra("requestBody", postBodyView!!.text.toString())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isOpenedEventReport", isOpenedEventReport)
        outState.putBoolean("isOpenedTrackerReport", isOpenedTrackerReport)
        outState.putBoolean("isOpenedUrlInspector", isOpenedUrlInspector)
        HyBid.getReportingController().cacheAdEventList(eventList)
    }
}