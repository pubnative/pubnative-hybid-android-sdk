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
package net.pubnative.lite.demo.ui.fragments.dfp

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
import net.pubnative.lite.demo.ui.activities.dfp.DFPBannerActivity
import net.pubnative.lite.demo.ui.activities.dfp.DFPInterstitialActivity
import net.pubnative.lite.demo.ui.activities.dfp.DFPMRectActivity

class DFPFragment : Fragment() {
    private var zoneId: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_dfp, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        zoneId = activity?.intent?.getStringExtra(Constants.IntentParams.ZONE_ID)

        view.findViewById<TextView>(R.id.view_chosen_zone_id).text = zoneId

        view.findViewById<Button>(R.id.button_banner).setOnClickListener {
            val intent = Intent(activity, DFPBannerActivity::class.java)
            intent.putExtra(Constants.IntentParams.ZONE_ID, zoneId)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_medium).setOnClickListener {
            val intent = Intent(activity, DFPMRectActivity::class.java)
            intent.putExtra(Constants.IntentParams.ZONE_ID, zoneId)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.button_interstitial).setOnClickListener {
            val intent = Intent(activity, DFPInterstitialActivity::class.java)
            intent.putExtra(Constants.IntentParams.ZONE_ID, zoneId)
            startActivity(intent)
        }
    }
}