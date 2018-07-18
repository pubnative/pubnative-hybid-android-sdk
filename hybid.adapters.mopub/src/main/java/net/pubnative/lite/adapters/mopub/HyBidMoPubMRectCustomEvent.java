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
package net.pubnative.lite.adapters.mopub;

import android.content.Context;
import android.view.View;

import com.mopub.mobileads.CustomEventBanner;
import com.mopub.mobileads.MoPubErrorCode;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.mrect.presenter.MRectPresenter;
import net.pubnative.lite.sdk.mrect.presenter.MRectPresenterFactory;
import net.pubnative.lite.sdk.utils.Logger;

import java.util.Map;

public class HyBidMoPubMRectCustomEvent extends CustomEventBanner implements MRectPresenter.Listener {
    private static final String TAG = HyBidMoPubMRectCustomEvent.class.getSimpleName();

    private static final String ZONE_ID_KEY = "pn_zone_id";

    private CustomEventBannerListener mBannerListener;

    private MRectPresenter mMRectPresenter;

    @Override
    protected void loadBanner(Context context,
                              CustomEventBannerListener customEventBannerListener,
                              Map<String, Object> localExtras,
                              Map<String, String> serverExtras) {

        if (customEventBannerListener == null) {
            Logger.e(TAG, "customEventBannerListener is null");
            return;
        }
        mBannerListener = customEventBannerListener;

        String zoneIdKey;
        if (localExtras.containsKey(ZONE_ID_KEY)) {
            zoneIdKey = (String) localExtras.get(ZONE_ID_KEY);
        } else if (serverExtras.containsKey(ZONE_ID_KEY)) {
            zoneIdKey = serverExtras.get(ZONE_ID_KEY);
        } else {
            Logger.e(TAG, "Could not find zone id value in CustomEventBanner localExtras or serverExtras");
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        final Ad ad = HyBid.getAdCache().remove(zoneIdKey);
        if (ad == null) {
            Logger.e(TAG, "Could not find an ad in the cache for zone id with key: " + zoneIdKey);
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        mMRectPresenter = new MRectPresenterFactory(context).createMRectPresenter(ad, this);
        if (mMRectPresenter == null) {
            Logger.e(TAG, "Could not create valid MRect presenter");
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        mMRectPresenter.load();
    }

    @Override
    protected void onInvalidate() {
        if (mMRectPresenter != null) {
            mMRectPresenter.stopTracking();
            mMRectPresenter.destroy();
            mMRectPresenter = null;
        }
    }

    @Override
    public void onMRectLoaded(MRectPresenter mRectPresenter, View mRect) {
        if (mBannerListener != null) {
            mBannerListener.onBannerLoaded(mRect);
            mMRectPresenter.startTracking();
        }
    }

    @Override
    public void onMRectClicked(MRectPresenter mRectPresenter) {
        if (mBannerListener != null) {
            mBannerListener.onBannerClicked();
        }
    }

    @Override
    public void onMRectError(MRectPresenter mRectPresenter) {
        if (mBannerListener != null) {
            mBannerListener.onBannerFailed(MoPubErrorCode.INTERNAL_ERROR);
        }
    }
}
