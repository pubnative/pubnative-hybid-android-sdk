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
import android.text.TextUtils;
import android.util.AttributeSet;

import net.pubnative.lite.sdk.api.MRectRequestManager;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.models.APIAsset;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.mrect.presenter.MRectPresenterFactory;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.utils.MarkupUtils;
import net.pubnative.lite.sdk.vpaid.VideoAdCache;

public class HyBidMRectAdView extends PNAdView {

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
    protected String getLogTag() {
        return HyBidMRectAdView.class.getSimpleName();
    }

    @Override
    RequestManager getRequestManager() {
        return new MRectRequestManager();
    }

    @Override
    protected AdPresenter createPresenter() {
        return new MRectPresenterFactory(getContext())
                .createPresenter(mAd, this);
    }

    @Override
    public void renderAd(String adValue, Listener listener) {
        cleanup();
        mListener = listener;

        if (!TextUtils.isEmpty(adValue)) {
            int assetGroup;
            Ad.AdType type;
            if (MarkupUtils.isVastXml(adValue)) {
                assetGroup = 4;
                type = Ad.AdType.VIDEO;
            } else {
                assetGroup = 8;
                type = Ad.AdType.HTML;
            }

            mAd = new Ad(assetGroup, adValue, type);
            renderFromCustomAd();
        } else {
            invokeOnLoadFailed(new Exception("The server has returned an invalid ad asset"));
        }
    }
}
