// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
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
        val settings = settingManager.getSettings().hybidSettings

        settings?.browserPriorities?.let { adapter.addPackageNames(it) }
    }
}