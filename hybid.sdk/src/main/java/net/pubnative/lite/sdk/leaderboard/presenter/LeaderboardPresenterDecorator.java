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
package net.pubnative.lite.sdk.leaderboard.presenter;

import android.view.View;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.utils.Logger;

public class LeaderboardPresenterDecorator implements LeaderboardPresenter, LeaderboardPresenter.Listener {
    private static final String TAG = LeaderboardPresenterDecorator.class.getSimpleName();
    private final LeaderboardPresenter mLeaderboardPresenter;
    private final AdTracker mAdTrackingDelegate;
    private final LeaderboardPresenter.Listener mListener;
    private boolean mIsDestroyed;

    public LeaderboardPresenterDecorator(LeaderboardPresenter leaderboardPresenter,
                                    AdTracker adTrackingDelegate,
                                    LeaderboardPresenter.Listener listener) {
        mLeaderboardPresenter = leaderboardPresenter;
        mAdTrackingDelegate = adTrackingDelegate;
        mListener = listener;
    }

    @Override
    public void setListener(Listener listener) {
        // We set the listener in the constructor instead
    }

    @Override
    public Ad getAd() {
        return mLeaderboardPresenter.getAd();
    }

    @Override
    public void load() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "LeaderboardPresenterDecorator is destroyed")) {
            return;
        }

        mLeaderboardPresenter.load();
    }

    @Override
    public void destroy() {
        mLeaderboardPresenter.destroy();
        mIsDestroyed = true;
    }

    @Override
    public void startTracking() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "LeaderboardPresenterDecorator is destroyed")) {
            return;
        }

        mLeaderboardPresenter.startTracking();
    }

    @Override
    public void stopTracking() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "LeaderboardPresenterDecorator is destroyed")) {
            return;
        }

        mLeaderboardPresenter.stopTracking();
    }

    @Override
    public void onLeaderboardLoaded(LeaderboardPresenter leaderboardPresenter, View leaderboard) {
        if (mIsDestroyed) {
            return;
        }

        mAdTrackingDelegate.trackImpression();
        mListener.onLeaderboardLoaded(leaderboardPresenter, leaderboard);
    }

    @Override
    public void onLeaderboardClicked(LeaderboardPresenter leaderboardPresenter) {
        if (mIsDestroyed) {
            return;
        }

        mAdTrackingDelegate.trackClick();
        mListener.onLeaderboardClicked(leaderboardPresenter);
    }

    @Override
    public void onLeaderboardError(LeaderboardPresenter leaderboardPresenter) {
        if (mIsDestroyed) {
            return;
        }

        String errorMessage = "Leaderboard error for zone id: ";
        Logger.d(TAG, errorMessage);
        mListener.onLeaderboardError(leaderboardPresenter);
    }
}
