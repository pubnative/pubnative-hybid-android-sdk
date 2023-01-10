package net.pubnative.lite.demo.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.viewholders.ReportingEventViewHolder
import net.pubnative.lite.demo.ui.viewholders.ReportingTrackerViewHolder
import net.pubnative.lite.sdk.analytics.ReportingEvent
import net.pubnative.lite.sdk.analytics.tracker.ReportingTracker

class ReportingTrackerAdapter() : RecyclerView.Adapter<ReportingTrackerViewHolder>() {

    private var trackers: List<ReportingTracker> = arrayListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun submitData(events: List<ReportingTracker>){
        this.trackers = events
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ReportingTrackerViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_reporting_tracker, parent, false)
    )

    override fun onBindViewHolder(holder: ReportingTrackerViewHolder, position: Int) {
        holder.bind(trackers[position])
    }

    override fun getItemCount() = trackers.size
}