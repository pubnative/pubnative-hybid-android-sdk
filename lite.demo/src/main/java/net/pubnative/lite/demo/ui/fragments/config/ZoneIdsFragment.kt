package net.pubnative.lite.demo.ui.fragments.config

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
import net.pubnative.lite.demo.ui.adapters.ZoneIdAdapter

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class ZoneIdsFragment : Fragment() {
    private lateinit var zoneIdInput: EditText
    private lateinit var zoneIdList: RecyclerView
    private lateinit var settingManager: SettingsManager
    private lateinit var adapter: ZoneIdAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_zone_ids, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        zoneIdInput = view.findViewById(R.id.input_zone_id)
        zoneIdList = view.findViewById(R.id.list_zone_ids)
        settingManager = SettingsManager.getInstance(context!!)

        adapter = ZoneIdAdapter(null)
        val layoutManager = GridLayoutManager(activity, 6, GridLayoutManager.VERTICAL, false)
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