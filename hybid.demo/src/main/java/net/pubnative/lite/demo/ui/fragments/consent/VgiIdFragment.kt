package net.pubnative.lite.demo.ui.fragments.consent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.util.JsonUtils
import net.pubnative.lite.sdk.HyBid
import net.pubnative.lite.sdk.VgiIdManager

class VgiIdFragment : Fragment(){
    private val TAG = VgiIdFragment::class.java.simpleName

    private lateinit var vgiIdView: TextView

    private val mVgiIdManager : VgiIdManager? = HyBid.getVgiIdManager()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_vgi_id, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vgiIdView = view.findViewById(R.id.view_vgi_id)

        initVgiId()

        showVgiId()
    }

    private fun initVgiId(){
        if (mVgiIdManager != null) {
            mVgiIdManager?.init()
            Toast.makeText(context, "VGI ID initialised", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "VGI ID not initialised", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showVgiId(){
        val vgiIdModel = mVgiIdManager?.vgiIdModel

        if (vgiIdModel != null) {
            val vgiIdModelString = vgiIdModel.toJson().toString()
            vgiIdView.text = JsonUtils.toFormattedJson(vgiIdModelString)
            Toast.makeText(context, "VGI ID retrieved", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "The VGI ID has not been initialised", Toast.LENGTH_SHORT).show()
        }
    }
}