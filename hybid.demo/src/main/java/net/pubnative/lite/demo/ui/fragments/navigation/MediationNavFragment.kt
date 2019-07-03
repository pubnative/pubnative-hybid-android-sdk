package net.pubnative.lite.demo.ui.fragments.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.mopub.*

class MediationNavFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_nav_mediation, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_mopub_banner).setOnClickListener {
            val intent = Intent(activity, MoPubMediationBannerActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_mopub_medium).setOnClickListener {
            val intent = Intent(activity, MoPubMediationMRectActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_mopub_leaderboard).setOnClickListener {
            val intent = Intent(activity, MoPubMediationLeaderboardActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_mopub_interstitial).setOnClickListener {
            val intent = Intent(activity, MoPubMediationInterstitialActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_mopub_native).setOnClickListener {
            val intent = Intent(activity, MoPubMediationNativeActivity::class.java)
            startActivity(intent)
        }
    }
}