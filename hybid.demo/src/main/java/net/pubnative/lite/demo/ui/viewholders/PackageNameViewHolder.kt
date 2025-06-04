// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.ui.listeners.PackageNameClickListener

class PackageNameViewHolder(itemView: View, private var listener: PackageNameClickListener?) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    private var packageName: String = ""

    init {
        itemView.setOnClickListener(this)
    }

    fun bind(packageName: String) {
        val textView = itemView as TextView
        textView.text = packageName
        this.packageName = packageName
    }

    override fun onClick(v: View?) {
        listener?.onPackageNameClicked(packageName)
    }
}