// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.listeners.PackageNameClickListener
import net.pubnative.lite.demo.ui.viewholders.PackageNameViewHolder

class PackageNameAdapter(listener: PackageNameClickListener?) : RecyclerView.Adapter<PackageNameViewHolder>() {
    private val list: MutableList<String> = mutableListOf()
    private var listener: PackageNameClickListener? = listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageNameViewHolder = PackageNameViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_package_name, parent, false), listener)

    override fun onBindViewHolder(holder: PackageNameViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun addPackageNames(packageNames: List<String>) {
        packageNames.forEach {
            addPackageName(it)
        }
    }

    fun addPackageName(packageName: String) {
        list.add(packageName)
        notifyItemInserted(list.size - 1)
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    fun getPackageNames(): List<String> = list.toList()
}