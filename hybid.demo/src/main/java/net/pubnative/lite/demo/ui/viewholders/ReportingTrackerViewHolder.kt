package net.pubnative.lite.demo.ui.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.util.ClipboardUtils
import net.pubnative.lite.sdk.analytics.tracker.ReportingTracker

class ReportingTrackerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private var tracker: ReportingTracker? = null

    fun bind(tracker: ReportingTracker) {
        this.tracker = tracker
        itemView.findViewById<TextView>(R.id.trackerUrl).text = tracker.url
        itemView.findViewById<TextView>(R.id.trackerType).text = tracker.type
        itemView.findViewById<TextView>(R.id.responseCode).text = tracker.responseCode.toString()
        initOnClicks()
    }

    private fun initOnClicks(){
        itemView.findViewById<TextView>(R.id.trackerUrl).setOnClickListener {
            ClipboardUtils.copyToClipboard(
                itemView.context,
                itemView.findViewById<TextView>(R.id.trackerUrl).text.toString()
            )
        }
    }
}