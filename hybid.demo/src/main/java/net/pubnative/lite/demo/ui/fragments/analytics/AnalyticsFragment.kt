package net.pubnative.lite.demo.ui.fragments.analytics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.AnalyticsSubscriber
import net.pubnative.lite.demo.ui.adapters.AnalyticsAdapter
import net.pubnative.lite.sdk.analytics.ReportingEvent

class AnalyticsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AnalyticsAdapter
    private lateinit var analyticsList: MutableList<ReportingEvent>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater.inflate(R.layout.fragment_analytics, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.analytics_list)

        analyticsList = getAnalytics()

        adapter = AnalyticsAdapter(analyticsList)

        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
    }


    private fun getAnalytics() : MutableList<ReportingEvent> {
        return AnalyticsSubscriber.eventList
    }
}