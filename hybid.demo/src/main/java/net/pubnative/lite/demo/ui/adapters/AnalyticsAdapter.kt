package net.pubnative.lite.demo.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.viewholders.AnalyticsViewHolder
import net.pubnative.lite.sdk.analytics.ReportingEvent

class AnalyticsAdapter(val values: MutableList<ReportingEvent>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AnalyticsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_analytics_text, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AnalyticsViewHolder -> holder.bind(values[position])
        }
    }

    override fun getItemCount(): Int {
        return values.size
    }
}