// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.demo.ui.activities.hybid

import android.os.Bundle
import android.util.Log
import net.pubnative.lite.demo.R
import net.pubnative.lite.demo.ui.activities.TabActivity
import net.pubnative.lite.demo.ui.fragments.hybid.HyBidBannerFragment

/**
 * Created by erosgarciaponte on 30.01.18.
 */
class HyBidBannerActivity : TabActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("HyBidBannerActivity","created")
    }

    override fun getAdFragment() = HyBidBannerFragment()

    override fun getActivityTitle() = getString(R.string.hybid_banner)
}