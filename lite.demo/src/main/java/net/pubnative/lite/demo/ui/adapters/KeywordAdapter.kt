package net.pubnative.lite.demo.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.viewholders.KeywordViewHolder

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class KeywordAdapter : RecyclerView.Adapter<KeywordViewHolder>() {
    private val list: MutableList<String> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): KeywordViewHolder
            = KeywordViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_keyword, parent, false))

    override fun onBindViewHolder(holder: KeywordViewHolder?, position: Int) {
        holder?.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun addKeywords(keywords: List<String>) {
        keywords.forEach {
            addKeyword(it)
        }
    }

    fun addKeyword(keyword: String) {
        list.add(keyword)
        notifyItemInserted(list.size - 1)
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    fun getKeywords(): List<String> = list.toList()
}