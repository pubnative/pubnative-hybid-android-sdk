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
package net.pubnative.lite.demo.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.listeners.ZoneIdClickListener
import net.pubnative.lite.demo.ui.viewholders.ZoneIdViewHolder

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class ZoneIdAdapter(listener: ZoneIdClickListener?) : RecyclerView.Adapter<ZoneIdViewHolder>() {
    private val list: MutableList<String> = mutableListOf()
    private var listener: ZoneIdClickListener? = listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ZoneIdViewHolder = ZoneIdViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_zone_id, parent, false), listener)

    override fun onBindViewHolder(holder: ZoneIdViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun addZoneIds(zoneIds: List<String>) {
        zoneIds.forEach {
            addZoneId(it)
        }
    }

    fun addZoneId(zoneId: String) {
        list.add(zoneId)
        notifyItemInserted(list.size - 1)
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    fun getZoneIds(): List<String> = list.toList()
}