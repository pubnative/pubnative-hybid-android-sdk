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
package net.pubnative.lite.sdk.mrect.presenter;

import android.view.View;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.utils.Logger;

/**
 * Created by erosgarciaponte on 12.01.18.
 */

public class MRectPresenterDecorator implements MRectPresenter, MRectPresenter.Listener {
    private static final String TAG = MRectPresenterDecorator.class.getSimpleName();
    private final MRectPresenter mMRectPresenter;
    private final AdTracker mAdTrackingDelegate;
    private final MRectPresenter.Listener mListener;
    private boolean mIsDestroyed;

    public MRectPresenterDecorator(MRectPresenter mRectPresenter,
                                   AdTracker adTrackingDelegate,
                                   MRectPresenter.Listener listener) {
        mMRectPresenter = mRectPresenter;
        mAdTrackingDelegate = adTrackingDelegate;
        mListener = listener;
    }

    @Override
    public void setListener(Listener listener) {
        // We set the listener in the constructor instead
    }

    @Override
    public Ad getAd() {
        return mMRectPresenter.getAd();
    }

    @Override
    public void load() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "MRectPresenterDecorator is destroyed")) {
            return;
        }

        mMRectPresenter.load();
    }

    @Override
    public void destroy() {
        mMRectPresenter.destroy();
        mIsDestroyed = true;
    }

    @Override
    public void startTracking() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "MRectPresenterDecorator is destroyed")) {
            return;
        }

        mMRectPresenter.startTracking();
    }

    @Override
    public void stopTracking() {
        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "MRectPresenterDecorator is destroyed")) {
            return;
        }

        mMRectPresenter.stopTracking();
    }

    @Override
    public void onMRectLoaded(MRectPresenter mRectPresenter, View mRect) {
        if (mIsDestroyed) {
            return;
        }

        mAdTrackingDelegate.trackImpression();
        mListener.onMRectLoaded(mRectPresenter, mRect);
    }

    @Override
    public void onMRectClicked(MRectPresenter mRectPresenter) {
        if (mIsDestroyed) {
            return;
        }

        mAdTrackingDelegate.trackClick();
        mListener.onMRectClicked(mRectPresenter);
    }

    @Override
    public void onMRectError(MRectPresenter mRectPresenter) {
        if (mIsDestroyed) {
            return;
        }

        String errorMessage = "MRect error for zone id: ";
        Logger.d(TAG, errorMessage);
        mListener.onMRectError(mRectPresenter);
    }
}
