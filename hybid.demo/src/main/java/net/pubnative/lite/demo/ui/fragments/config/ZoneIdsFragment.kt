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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.adapters.ZoneIdAdapter

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class ZoneIdsFragment : Fragment() {
    private lateinit var zoneIdInput: EditText
    private lateinit var zoneIdList: RecyclerView
    private lateinit var settingManager: SettingsManager
    private lateinit var adapter: ZoneIdAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_zone_ids, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        zoneIdInput = view.findViewById(R.id.input_zone_id)
        zoneIdList = view.findViewById(R.id.list_zone_ids)
        settingManager = SettingsManager.getInstance(context!!)

        adapter = ZoneIdAdapter(null)
        val layoutManager = GridLayoutManager(activity, 6, RecyclerView.VERTICAL, false)
        zoneIdList.layoutManager = layoutManager
        zoneIdList.adapter = adapter

        view.findViewById<TextView>(R.id.button_add_zone_id).setOnClickListener {
            if (zoneIdInput.text.isNotEmpty()) {
                val zoneId = zoneIdInput.text.toString()
                adapter.addZoneId(zoneId)
                zoneIdInput.setText("")
            }
        }

        view.findViewById<TextView>(R.id.button_clear_zone_ids).setOnClickListener {
            adapter.clear()
        }

        view.findViewById<Button>(R.id.button_save_pn_zone_ids).setOnClickListener {
            settingManager.setZoneIds(adapter.getZoneIds())

            val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(zoneIdInput.windowToken, 0)

            Toast.makeText(activity, "PN Zone Ids saved successfully.", Toast.LENGTH_SHORT).show()
            activity?.finish()
        }

        fillSavedValues()
    }

    private fun fillSavedValues() {
        val settings = settingManager.getSettings()

        adapter.addZoneIds(settings.zoneIds)
    }
}