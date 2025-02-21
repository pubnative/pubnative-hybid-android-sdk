package net.pubnative.lite.demo.ui.viewholders

import android.text.TextUtils
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
        var content = "";
        if (TextUtils.isEmpty(tracker.url)) {
            itemView.findViewById<TextView>(R.id.trackerContentLabel).text =
                itemView.context.getText(R.string.tracker_content)
            content = tracker.js
        } else {
            itemView.findViewById<TextView>(R.id.trackerContentLabel).text =
                itemView.context.getText(R.string.tracker_report_url)
            content = tracker.url
        }
        itemView.findViewById<TextView>(R.id.trackerUrl).text = content
        itemView.findViewById<TextView>(R.id.trackerType).text = tracker.type
        itemView.findViewById<TextView>(R.id.responseCode).text = tracker.responseCode.toString()
        initOnClicks()
    }

    private fun initOnClicks() {
        itemView.findViewById<TextView>(R.id.trackerUrl).setOnClickListener {
            ClipboardUtils.copyToClipboard(
                itemView.context,
                itemView.findViewById<TextView>(R.id.trackerUrl).text.toString()
            )
        }
    }
}