// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class KeywordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(keyword: String) {
        val textView = itemView as TextView
        textView.text = keyword
    }
}