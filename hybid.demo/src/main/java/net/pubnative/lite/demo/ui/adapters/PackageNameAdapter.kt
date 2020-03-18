// The MIT License (MIT)
//
// Copyright (c) 2020 PubNative GmbH
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