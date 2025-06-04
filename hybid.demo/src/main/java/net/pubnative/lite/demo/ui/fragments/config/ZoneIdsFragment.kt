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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.managers.SettingsManager
import net.pubnative.lite.demo.ui.adapters.ZoneIdAdapter

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class ZoneIdsFragment : Fragment(R.layout.fragment_zone_ids) {
    private lateinit var zoneIdInput: EditText
    private lateinit var zoneIdList: RecyclerView
    private lateinit var settingManager: SettingsManager
    private lateinit var adapter: ZoneIdAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        zoneIdInput = view.findViewById(R.id.input_zone_id)
        zoneIdList = view.findViewById(R.id.list_zone_ids)
        settingManager = SettingsManager.getInstance(requireContext())

        adapter = ZoneIdAdapter(null)

        val gridLayoutManager =
            object : GridLayoutManager(activity, 6, RecyclerView.VERTICAL, false) {
                override fun canScrollVertically() = false
            }

        zoneIdList.layoutManager = gridLayoutManager
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

            val inputMethodManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(zoneIdInput.windowToken, 0)

            Toast.makeText(activity, "PN Zone Ids saved successfully.", Toast.LENGTH_SHORT).show()
            activity?.finish()
        }

        fillSavedValues()
    }

    private fun fillSavedValues() {
        val settings = settingManager.getSettings().hybidSettings

        settings?.zoneIds?.let { adapter.addZoneIds(it) }
    }
}