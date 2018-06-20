// The MIT License (MIT)
//
// Copyright (c) 2018 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
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