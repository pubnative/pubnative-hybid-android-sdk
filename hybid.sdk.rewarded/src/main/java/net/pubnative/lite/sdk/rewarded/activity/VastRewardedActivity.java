// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.rewarded.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import net.pubnative.lite.sdk.rewarded.viewModel.VastRewardedViewModel;
import net.pubnative.lite.sdk.vpaid.HyBidActivityInteractor;

public class VastRewardedActivity extends HyBidRewardedActivity {

    private HyBidActivityInteractor mInteractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((VastRewardedViewModel) mViewModel).renderVastAd();
    }

    @Override
    protected void onPause() {
        if (!mIsFinishing) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                fetchActivityInteractor();
                mInteractor.activityPaused();
                mViewModel.pauseAd();
            }, 100);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            fetchActivityInteractor();
            mInteractor.activityResumed();
            mViewModel.resumeAd();
        }, 100);
    }

    @Override
    protected void onDestroy() {
        fetchActivityInteractor();
        mInteractor.activityDestroyed();
        mViewModel.destroyAd();
        super.onDestroy();
    }

    private synchronized void fetchActivityInteractor() {
        mInteractor = HyBidActivityInteractor.getInstance();
    }
}