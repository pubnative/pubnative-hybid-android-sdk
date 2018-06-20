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
package net.pubnative.lite.demo.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import net.pubnative.lite.demo.R
import net.pubnative.lite.sdk.PNLite
import net.pubnative.lite.sdk.consent.UserConsentActivity

class PNConsentFragment : Fragment() {

    private val REQUEST_CONSENT: Int = 290
    private lateinit var vendorListView: TextView
    private lateinit var privacyPolicyView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_pn_consent, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vendorListView = view.findViewById(R.id.view_vendor_list)
        privacyPolicyView = view.findViewById(R.id.view_privacy_policy)

        view.findViewById<Button>(R.id.button_pn_owned).setOnClickListener {
            /*
            This would be the normal implementation for a regular publisher.
            We remove this condition here for testing purposes

            if (PNLite.getUserDataManager().shouldAskConsent()) {

                val intent = PNLite.getUserDataManager().getConsentScreenIntent(activity)
                startActivityForResult(intent, REQUEST_CONSENT)
            } else {
                Toast.makeText(activity, "Consent has already been answered. If you want to try again please clear your app cache", Toast.LENGTH_LONG).show()
            } */
            val intent = PNLite.getUserDataManager().getConsentScreenIntent(activity)
            startActivityForResult(intent, REQUEST_CONSENT)
        }

        view.findViewById<Button>(R.id.button_publisher_owned).setOnClickListener {
            vendorListView.text = PNLite.getUserDataManager().vendorListLink
            privacyPolicyView.text = PNLite.getUserDataManager().privacyPolicyLink
        }

        view.findViewById<Button>(R.id.button_accept_consent).setOnClickListener {
            PNLite.getUserDataManager().grantConsent()
        }

        view.findViewById<Button>(R.id.button_deny_consent).setOnClickListener {
            PNLite.getUserDataManager().denyConsent()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CONSENT -> {
                if (resultCode == UserConsentActivity.RESULT_CONSENT_ACCEPTED) {
                    Toast.makeText(activity, "Consent given", Toast.LENGTH_SHORT).show()
                } else if (resultCode == UserConsentActivity.RESULT_CONSENT_REJECTED) {
                    Toast.makeText(activity, "Consent refused", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}