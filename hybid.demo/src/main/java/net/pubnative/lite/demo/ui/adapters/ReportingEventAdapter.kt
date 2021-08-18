package net.pubnative.lite.demo.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.viewholders.ReportingEventViewHolder
import net.pubnative.lite.sdk.analytics.ReportingEvent

class ReportingEventAdapter(private val events: List<ReportingEvent>) :
    RecyclerView.Adapter<ReportingEventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ReportingEventViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_reporting_event, parent, false)
    )

    override fun onBindViewHolder(holder: ReportingEventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount() = events.size
}