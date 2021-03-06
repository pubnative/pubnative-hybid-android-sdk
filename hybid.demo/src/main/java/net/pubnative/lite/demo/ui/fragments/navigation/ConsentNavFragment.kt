package net.pubnative.lite.demo.ui.fragments.navigation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.consent.*

class ConsentNavFragment : Fragment(R.layout.fragment_nav_consent) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.button_ogury_cmp).setOnClickListener {
            val intent = Intent(activity, OguryCMPActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<TextView>(R.id.button_google_cmp).setOnClickListener {
            val intent = Intent(activity, GoogleCMPActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<TextView>(R.id.button_consent_strings).setOnClickListener {
            val intent = Intent(activity, ConsentStringsActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<TextView>(R.id.button_vgi_id).setOnClickListener {
            val intent = Intent(activity, VgiIdActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<TextView>(R.id.button_mopub_cmp).setOnClickListener {
            val intent = Intent(activity, MoPubCMPActivity::class.java)
            startActivity(intent)
        }
    }
}