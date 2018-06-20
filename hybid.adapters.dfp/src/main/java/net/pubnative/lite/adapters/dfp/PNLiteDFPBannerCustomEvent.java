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
package net.pubnative.lite.adapters.dfp;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventBanner;
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener;

import net.pubnative.lite.sdk.PNLite;
import net.pubnative.lite.sdk.banner.presenter.BannerPresenter;
import net.pubnative.lite.sdk.banner.presenter.BannerPresenterFactory;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.Logger;

/**
 * Created by erosgarciaponte on 13.03.18.
 */

public class PNLiteDFPBannerCustomEvent implements CustomEventBanner, BannerPresenter.Listener {
    private static final String TAG = PNLiteDFPBannerCustomEvent.class.getSimpleName();

    private static final String ZONE_ID_KEY = "pn_zone_id";
    private CustomEventBannerListener mBannerListener;
    private BannerPresenter mPresenter;

    @Override
    public void requestBannerAd(Context context,
                                CustomEventBannerListener listener,
                                String serverParameter,
                                AdSize size,
                                MediationAdRequest mediationAdRequest,
                                Bundle customEventExtras) {
        if (listener == null) {
            Logger.e(TAG, "customEventBannerListener is null");
            return;
        }
        mBannerListener = listener;

        String zoneIdKey;
        if (!TextUtils.isEmpty(PNLiteDFPUtils.getZoneId(serverParameter))) {
            zoneIdKey = PNLiteDFPUtils.getZoneId(serverParameter);
        } else if (!TextUtils.isEmpty(PNLiteDFPUtils.getZoneId(customEventExtras))) {
            zoneIdKey = PNLiteDFPUtils.getZoneId(customEventExtras);
        } else {
            Logger.e(TAG, "Could not find zone id value in CustomEventBanner serverParameter or customEventExtras");
            mBannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST);
            return;
        }

        final Ad ad = PNLite.getAdCache().remove(zoneIdKey);
        if (ad == null) {
            Logger.e(TAG, "Could not find an ad in the cache for zone id with key: " + zoneIdKey);
            mBannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NETWORK_ERROR);
            return;
        }

        mPresenter = new BannerPresenterFactory(context).createBannerPresenter(ad, this);
        if (mPresenter == null) {
            Logger.e(TAG, "Could not create valid banner presenter");
            mBannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_NETWORK_ERROR);
            return;
        }

        mPresenter.load();
    }

    @Override
    public void onDestroy() {
        if (mPresenter != null) {
            mPresenter.stopTracking();
            mPresenter.destroy();
            mPresenter = null;
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {
    }

    @Override
    public void onBannerLoaded(BannerPresenter bannerPresenter, View banner) {
        if (mBannerListener != null) {
            mBannerListener.onAdLoaded(banner);
            mPresenter.startTracking();
        }
    }

    @Override
    public void onBannerError(BannerPresenter bannerPresenter) {
        if (mBannerListener != null) {
            mBannerListener.onAdFailedToLoad(AdRequest.ERROR_CODE_INTERNAL_ERROR);
        }
    }

    @Override
    public void onBannerClicked(BannerPresenter bannerPresenter) {
        if (mBannerListener != null) {
            mBannerListener.onAdClicked();
            mBannerListener.onAdLeftApplication();
        }
    }
}
