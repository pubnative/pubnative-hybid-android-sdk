package net.pubnative.lite.sdk.rewarded.presenter;

import android.text.TextUtils;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.utils.Logger;

public class RewardedPresenterDecorator implements RewardedPresenter, RewardedPresenter.Listener {
    private static final String TAG = RewardedPresenterDecorator.class.getSimpleName();
    private final RewardedPresenter mRewardedPresenter;
    private final AdTracker mAdTrackingDelegate;
    private final RewardedPresenter.Listener mListener;
    private boolean mIsDestroyed;

    public RewardedPresenterDecorator(RewardedPresenter rewardedPresenter,
                                      AdTracker adTrackingDelegate,
                                      RewardedPresenter.Listener listener) {
        mRewardedPresenter = rewardedPresenter;
        mAdTrackingDelegate = adTrackingDelegate;
        mListener = listener;
    }

    @Override
    public void setListener(RewardedPresenter.Listener listener) {
        // We set the listener in the constructor instead
    }

    @Override
    public Ad getAd() {
        return mRewardedPresenter.getAd();
    }

    @Override
    public void load() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "RewardedPresenterDecorator is destroyed")) {
            return;
        }

        mRewardedPresenter.load();
    }

    @Override
    public boolean isReady() {
        return mRewardedPresenter.isReady();
    }

    @Override
    public void show() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "RewardedPresenterDecorator is destroyed")) {
            return;
        }

        mRewardedPresenter.show();
    }

    @Override
    public void destroy() {
        mRewardedPresenter.destroy();
        mIsDestroyed = true;
    }

    @Override
    public void onRewardedLoaded(RewardedPresenter rewardedPresenter) {
        if (mIsDestroyed) {
            return;
        }

        mListener.onRewardedLoaded(rewardedPresenter);
    }

    @Override
    public void onRewardedOpened(RewardedPresenter rewardedPresenter) {
        if (mIsDestroyed) {
            return;
        }

        mAdTrackingDelegate.trackImpression();
        mListener.onRewardedOpened(rewardedPresenter);
    }

    @Override
    public void onRewardedClicked(RewardedPresenter rewardedPresenter) {
        if (mIsDestroyed) {
            return;
        }

        mAdTrackingDelegate.trackClick();
        mListener.onRewardedClicked(rewardedPresenter);
    }

    @Override
    public void onRewardedClosed(RewardedPresenter rewardedPresenter) {
        if (mIsDestroyed) {
            return;
        }

        mListener.onRewardedClosed(rewardedPresenter);
    }

    @Override
    public void onRewardedFinished(RewardedPresenter rewardedPresenter) {
        if (mIsDestroyed) {
            return;
        }

        mListener.onRewardedFinished(rewardedPresenter);
    }

    @Override
    public void onRewardedError(RewardedPresenter rewardedPresenter) {
        if (mIsDestroyed) {
            return;
        }

        String zoneId = getAd().getZoneId();
        String errorMessage;
        if (TextUtils.isEmpty(zoneId)) {
            errorMessage = "Rewarded error";
        } else {
            errorMessage = "Rewarded error for zone id: " + zoneId;
        }

        Logger.d(TAG, errorMessage);
        mListener.onRewardedError(rewardedPresenter);
    }
}
