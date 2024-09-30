package net.pubnative.lite.demo.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.viewholders.BeaconListViewHolder
import net.pubnative.lite.demo.util.BeaconDescription

class BeaconListAdapter (beaconList : List<BeaconDescription>): RecyclerView.Adapter<BeaconListViewHolder>(){
    private var list: List<BeaconDescription> = beaconList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BeaconListViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_key_value, parent, false)
    )

    override fun onBindViewHolder(holder: BeaconListViewHolder, position: Int) {
        holder.bind(list.elementAt(position))
    }

    override fun getItemCount() = list.size
}