// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.interstitial.activity;

public class MraidInterstitialActivity extends HyBidInterstitialActivity {

    @Override
    protected void onPause() {
        super.onPause();
        mViewModel.pauseAd();
    }

    @Override
    protected void onResume() {
        mViewModel.resumeAd();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mViewModel.destroyAd();
        super.onDestroy();
    }
}