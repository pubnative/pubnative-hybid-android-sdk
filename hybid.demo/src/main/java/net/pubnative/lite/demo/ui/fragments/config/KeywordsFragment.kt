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
package net.pubnative.lite.demo.ui.fragments.config

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.adapters.KeywordAdapter
import net.pubnative.lite.sdk.PNLite

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class KeywordsFragment: Fragment() {
    private lateinit var keywordInput: EditText
    private lateinit var keywordList: RecyclerView
    private lateinit var settingManager: SettingsManager
    private lateinit var adapter: KeywordAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_keywords, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        keywordInput = view.findViewById(R.id.input_keyword)
        keywordList = view.findViewById(R.id.list_keywords)
        settingManager = SettingsManager.getInstance(context!!)

        adapter = KeywordAdapter()
        val layoutManager = GridLayoutManager(activity, 3, GridLayoutManager.VERTICAL, false)
        keywordList.layoutManager = layoutManager
        keywordList.adapter = adapter

        view.findViewById<TextView>(R.id.button_add_keyword).setOnClickListener {
            if (keywordInput.text.isNotEmpty()) {
                val keyword = keywordInput.text.toString()
                adapter.addKeyword(keyword)
                keywordInput.setText("")
            }
        }

        view.findViewById<TextView>(R.id.button_clear_keywords).setOnClickListener {
            adapter.clear()
        }

        view.findViewById<Button>(R.id.button_save_pn_keywords).setOnClickListener {
            val keywords = adapter.getKeywords()
            settingManager.setKeywords(keywords)

            val keywordsBuilder = StringBuilder()
            val separator = ","
            for (keyword in keywords) {
                keywordsBuilder.append(keyword)
                keywordsBuilder.append(separator)
            }
            var keywordString = keywordsBuilder.toString()

            if (!TextUtils.isEmpty(keywordString)) {
                keywordString = keywordString.substring(0, keywordString.length - separator.length)
            }

            PNLite.setKeywords(keywordString)

            val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(keywordInput.windowToken, 0)

            Toast.makeText(activity, "PN Keywords saved successfully.", Toast.LENGTH_SHORT).show()
            activity?.finish()
        }

        fillSavedValues()
    }

    private fun fillSavedValues() {
        val settings = settingManager.getSettings()

        adapter.addKeywords(settings.keywords)
    }
}