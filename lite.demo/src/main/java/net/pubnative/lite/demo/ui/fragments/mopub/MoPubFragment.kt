package net.pubnative.lite.demo.ui.fragments.mopub

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import net.pubnative.lite.demo.Constants
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.mopub.MoPubBannerActivity
import net.pubnative.lite.demo.ui.activities.mopub.MoPubInterstitialActivity
import net.pubnative.lite.demo.ui.activities.mopub.MoPubMRectActivity
import net.pubnative.lite.demo.ui.activities.mopub.MoPubMediationActivity

class MoPubFragment : Fragment() {
    private var zoneId: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_mopub, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)

        view.findViewById<TextView>(R.id.view_chosen_zone_id).text = zoneId

        view.findViewById<Button>(R.id.button_banner).setOnClickListener {
            val intent = Intent(activity, MoPubBannerActivity::class.java)
            intent.putExtra(Constants.IntentParams.ZONE_ID, zoneId)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_medium).setOnClickListener {
            val intent = Intent(activity, MoPubMRectActivity::class.java)
            intent.putExtra(Constants.IntentParams.ZONE_ID, zoneId)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_interstitial).setOnClickListener {
            val intent = Intent(activity, MoPubInterstitialActivity::class.java)
            intent.putExtra(Constants.IntentParams.ZONE_ID, zoneId)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_mopub_mediation).setOnClickListener {
            val intent = Intent(activity, MoPubMediationActivity::class.java)
            intent.putExtra(Constants.IntentParams.ZONE_ID, zoneId)
            startActivity(intent)
        }
    }
}