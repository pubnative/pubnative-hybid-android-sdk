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
package net.pubnative.lite.sdk.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import net.pubnative.lite.sdk.api.MRectRequestManager;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.mrect.presenter.MRectPresenter;
import net.pubnative.lite.sdk.mrect.presenter.MRectPresenterFactory;

public class HyBidMRectAdView extends PNAdView implements MRectPresenter.Listener {

    private MRectPresenter mPresenter;

    public HyBidMRectAdView(Context context) {
        super(context);
    }

    public HyBidMRectAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HyBidMRectAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public HyBidMRectAdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void cleanup() {
        super.cleanup();
        if (mPresenter != null) {
            mPresenter.destroy();
            mPresenter = null;
        }
    }

    @Override
    protected String getLogTag() {
        return HyBidMRectAdView.class.getSimpleName();
    }

    @Override
    RequestManager getRequestManager() {
        return new MRectRequestManager();
    }

    @Override
    protected void renderAd() {
        mPresenter = new MRectPresenterFactory(getContext())
                .createMRectPresenter(mAd, this);
        if (mPresenter != null) {
            mPresenter.load();
        } else {
            invokeOnLoadFailed(new Exception("The server has returned an unsupported ad asset"));
        }
    }

    @Override
    protected void startTracking() {
        if (mPresenter != null) {
            mPresenter.startTracking();
        }
    }

    @Override
    protected void stopTracking() {
        if (mPresenter != null) {
            mPresenter.stopTracking();
        }
    }

    //------------------------------ MRectPresenter Callbacks --------------------------------------
    @Override
    public void onMRectLoaded(MRectPresenter mRectPresenter, View mRect) {
        if (mRect == null) {
            invokeOnLoadFailed(new Exception("An error has occurred while rendering the ad"));
        } else {
            setupAdView(mRect);
        }
    }

    @Override
    public void onMRectError(MRectPresenter mRectPresenter) {
        invokeOnLoadFailed(new Exception("An error has occurred while rendering the ad"));
    }

    @Override
    public void onMRectClicked(MRectPresenter mRectPresenter) {
        invokeOnClick();
    }
}
