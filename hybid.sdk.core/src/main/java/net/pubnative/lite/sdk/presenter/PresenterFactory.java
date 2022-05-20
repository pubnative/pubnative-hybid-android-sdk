// The MIT License (MIT)
//
// Copyright (c) 2019 PubNative GmbH
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
package net.pubnative.lite.sdk.presenter;

import android.content.Context;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.models.ImpressionTrackingMethod;
import net.pubnative.lite.sdk.utils.AdTracker;

public abstract class PresenterFactory {
    private final Context mContext;

    public PresenterFactory(Context context) {
        mContext = context;
    }

    public AdPresenter createPresenter(Ad ad, AdSize adSize,
                                       AdPresenter.Listener bannerPresenterListener) {
        return createPresenter(ad, adSize, bannerPresenterListener, null);
    }

    public AdPresenter createPresenter(Ad ad, AdSize adSize,
                                       AdPresenter.Listener bannerPresenterListener, AdPresenter.ImpressionListener impressionListener) {
        return createPresenter(ad, adSize, ImpressionTrackingMethod.AD_RENDERED, bannerPresenterListener, impressionListener);
    }

    public AdPresenter createPresenter(Ad ad, AdSize adSize, ImpressionTrackingMethod trackingMethod,
                                       AdPresenter.Listener bannerPresenterListener, AdPresenter.ImpressionListener impressionListener) {
        final AdPresenter adPresenter = fromCreativeType(ad.assetgroupid, ad, adSize, trackingMethod);
        if (adPresenter == null) {
            return null;
        }

        final AdPresenterDecorator bannerPresenterDecorator = new AdPresenterDecorator(adPresenter,
                new AdTracker(ad.getBeacons(Ad.Beacon.IMPRESSION), ad.getBeacons(Ad.Beacon.CLICK)), HyBid.getReportingController(), bannerPresenterListener, impressionListener);
        adPresenter.setListener(bannerPresenterDecorator);
        adPresenter.setImpressionListener(bannerPresenterDecorator);
        adPresenter.setVideoListener(bannerPresenterDecorator);
        return bannerPresenterDecorator;
    }

    protected Context getContext() {
        return mContext;
    }

    protected abstract AdPresenter fromCreativeType(int assetGroupId, Ad ad, AdSize adSize);

    protected abstract AdPresenter fromCreativeType(int assetGroupId, Ad ad, AdSize adSize, ImpressionTrackingMethod trackingMethod);
}
