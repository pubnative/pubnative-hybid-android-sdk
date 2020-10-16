// The MIT License (MIT)
//
// Copyright (c) 2020 PubNative GmbH
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
