package net.pubnative.lite.demo.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.viewholders.ZoneIdViewHolder

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class ZoneIdAdapter : RecyclerView.Adapter<ZoneIdViewHolder>() {
    private val list: List<String> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ZoneIdViewHolder
            = ZoneIdViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_zone_id, parent, false))

    override fun onBindViewHolder(holder: ZoneIdViewHolder?, position: Int) {
        holder?.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}