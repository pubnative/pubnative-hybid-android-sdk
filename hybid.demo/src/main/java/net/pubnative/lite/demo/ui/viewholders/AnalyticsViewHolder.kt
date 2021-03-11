package net.pubnative.lite.demo.ui.viewholders

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.sdk.analytics.ReportingEvent

class AnalyticsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    private val TAG = AnalyticsViewHolder::class.java.simpleName

    private val textView : TextView = itemView.findViewById(R.id.view_analytics)

    fun bind(value: ReportingEvent){
        if (!TextUtils.isEmpty(value.adFormat)){
            textView.text = value.adFormat
        }
    }
}