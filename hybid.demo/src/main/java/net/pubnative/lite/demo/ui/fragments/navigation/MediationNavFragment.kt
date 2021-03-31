package net.pubnative.lite.demo.ui.fragments.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.admob.*
import net.pubnative.lite.demo.ui.activities.dfp.*
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

        view.findViewById<Button>(R.id.button_mopub_medium_video).setOnClickListener {
            val intent = Intent(activity, MoPubMediationMRectVideoActivity::class.java)
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

        view.findViewById<Button>(R.id.button_mopub_interstitial_video).setOnClickListener {
            val intent = Intent(activity, MoPubMediationInterstitialVideoActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_mopub_rewarded).setOnClickListener {
            val intent = Intent(activity, MoPubMediationRewardedActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_mopub_native).setOnClickListener {
            val intent = Intent(activity, MoPubMediationNativeActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_admob_banner).setOnClickListener {
            val intent = Intent(activity, AdmobMediationBannerActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_admob_medium).setOnClickListener {
            val intent = Intent(activity, AdmobMediationMRectActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_admob_medium_video).setOnClickListener {
            val intent = Intent(activity, AdmobMediationMRectVideoActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_admob_leaderboard).setOnClickListener {
            val intent = Intent(activity, AdmobMediationLeaderboardActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_admob_interstitial).setOnClickListener {
            val intent = Intent(activity, AdmobMediationInterstitialActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_admob_interstitial_video).setOnClickListener {
            val intent = Intent(activity, AdmobMediationInterstitialVideoActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_admob_rewarded).setOnClickListener {
            val intent = Intent(activity, AdmobMediationRewardedActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_dfp_banner).setOnClickListener {
            val intent = Intent(activity, DFPMediationBannerActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_dfp_medium).setOnClickListener {
            val intent = Intent(activity, DFPMediationMRectActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_dfp_leaderboard).setOnClickListener {
            val intent = Intent(activity, DFPMediationLeaderboardActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_dfp_interstitial).setOnClickListener {
            val intent = Intent(activity, DFPMediationInterstitialActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_admob_native).setOnClickListener {
            val intent = Intent(activity, AdmobMediationNativeActivity::class.java)
            startActivity(intent)
        }
    }
}