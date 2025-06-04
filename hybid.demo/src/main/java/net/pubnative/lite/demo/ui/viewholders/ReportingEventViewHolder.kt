// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.viewholders

import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.ui.activities.ReportingEventDetailsActivity
import net.pubnative.lite.sdk.analytics.ReportingEvent

class ReportingEventViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
    private var event: ReportingEvent? = null

    init {
        itemView.setOnClickListener(this)
    }

    fun bind(event: ReportingEvent) {
        this.event = event
        if (!TextUtils.isEmpty(event.eventType)) {
            (itemView as TextView).text = event.eventType
        }
    }

    override fun onClick(v: View?) {
        event?.let {
            val intent = Intent(itemView.context, ReportingEventDetailsActivity::class.java)
            intent.putExtra("event_data", it.eventData)
            itemView.context.startActivity(intent)
        }
    }
}