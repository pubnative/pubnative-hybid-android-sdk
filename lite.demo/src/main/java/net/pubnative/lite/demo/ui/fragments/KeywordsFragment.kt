package net.pubnative.lite.demo.ui.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
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
            settingManager.setKeywords(adapter.getKeywords())

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