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
package net.pubnative.lite.demo.ui.fragments.config

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.adapters.PackageNameAdapter
import net.pubnative.lite.sdk.HyBid

class BrowserPriorityFragment : Fragment(R.layout.fragment_browser_priorities) {
    private lateinit var packageNameInput: EditText
    private lateinit var packageNameList: RecyclerView
    private lateinit var settingManager: SettingsManager
    private lateinit var adapter: PackageNameAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        packageNameInput = view.findViewById(R.id.input_package_name)
        packageNameList = view.findViewById(R.id.list_package_names)
        settingManager = SettingsManager.getInstance(requireContext())

        adapter = PackageNameAdapter(null)
        val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        packageNameList.layoutManager = layoutManager
        packageNameList.adapter = adapter

        view.findViewById<TextView>(R.id.button_add_package_name).setOnClickListener {
            if (packageNameInput.text.isNotEmpty()) {
                val packageName = packageNameInput.text.toString()
                adapter.addPackageName(packageName)
                packageNameInput.setText("")
            }
        }

        view.findViewById<TextView>(R.id.button_clear_package_names).setOnClickListener {
            adapter.clear()
        }

        view.findViewById<Button>(R.id.button_save_package_names).setOnClickListener {
            val packageNames = adapter.getPackageNames()
            settingManager.setBrowserPriorities(packageNames)

            HyBid.getBrowserManager().cleanPriorities()
            packageNames.forEach { HyBid.getBrowserManager().addBrowser(it) }

            val inputMethodManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(packageNameInput.windowToken, 0)

            Toast.makeText(activity, "Package names saved successfully.", Toast.LENGTH_SHORT).show()
            activity?.finish()
        }

        fillSavedValues()
    }

    private fun fillSavedValues() {
        val settings = settingManager.getSettings()

        adapter.addPackageNames(settings.browserPriorities)
    }
}