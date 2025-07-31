// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.rewarded.activity;


public class MraidRewardedActivity extends HyBidRewardedActivity {

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