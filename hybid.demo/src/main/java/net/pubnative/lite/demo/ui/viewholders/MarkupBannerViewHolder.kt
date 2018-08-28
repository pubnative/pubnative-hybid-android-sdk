package net.pubnative.lite.demo.ui.viewholders

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.widget.FrameLayout
import net.pubnative.lite.demo.R

class MarkupBannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(markup: String) {
        if (TextUtils.isEmpty(markup)) {
            val container = itemView.findViewById<FrameLayout>(R.id.banner_container)
        }
    }
}