package net.pubnative.lite.demo.ui.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import net.pubnative.lite.demo.ui.listeners.ZoneIdClickListener

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class ZoneIdViewHolder(itemView: View, private var listener: ZoneIdClickListener?) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    private var zoneId: String = ""

    init {
        itemView.setOnClickListener(this)
    }

    fun bind(zoneId: String) {
        val textView = itemView as TextView
        textView.text = zoneId
        this.zoneId = zoneId
    }

    override fun onClick(v: View?) {
        listener?.onZoneIdClicked(zoneId)
    }
}