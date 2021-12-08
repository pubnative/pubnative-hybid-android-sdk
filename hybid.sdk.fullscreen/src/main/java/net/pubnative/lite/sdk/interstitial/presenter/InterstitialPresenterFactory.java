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
package net.pubnative.lite.sdk.interstitial.presenter;

import android.content.Context;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.models.RemoteConfigFeature;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.Logger;

/**
 * Created by erosgarciaponte on 09.01.18.
 */

public class InterstitialPresenterFactory {
    private static final String TAG = InterstitialPresenterFactory.class.getSimpleName();
    private final Context mContext;
    private final String mZoneId;

    public InterstitialPresenterFactory(Context context, String zoneId) {
        mContext = context;
        mZoneId = zoneId;
    }

    public InterstitialPresenter createInterstitialPresenter(
            Ad ad,
            InterstitialPresenter.Listener interstitialPresenterListener) {
        return createInterstitialPresenter(ad, -1, -1, interstitialPresenterListener);
    }

    public InterstitialPresenter createInterstitialPresenter(
            Ad ad,
            int htmlSkipOffset,
            int videoSkipOffset,
            InterstitialPresenter.Listener interstitialPresenterListener) {

        final InterstitialPresenter interstitialPresenter = fromCreativeType(ad.assetgroupid, ad, htmlSkipOffset, videoSkipOffset);
        if (interstitialPresenter == null) {
            return null;
        }

        final InterstitialPresenterDecorator interstitialPresenterDecorator =
                new InterstitialPresenterDecorator(interstitialPresenter, new AdTracker(
                        ad.getBeacons(Ad.Beacon.IMPRESSION),
                        ad.getBeacons(Ad.Beacon.CLICK)),
                        HyBid.getReportingController(),
                        interstitialPresenterListener);
        interstitialPresenter.setListener(interstitialPresenterDecorator);
        interstitialPresenter.setVideoListener(interstitialPresenterDecorator);
        return interstitialPresenterDecorator;
    }

    InterstitialPresenter fromCreativeType(int assetGroupId, Ad ad, int htmlSkipOffset, int videoSkipOffset) {
        switch (assetGroupId) {
            case ApiAssetGroupType.MRAID_300x600:
            case ApiAssetGroupType.MRAID_320x480:
            case ApiAssetGroupType.MRAID_480x320:
            case ApiAssetGroupType.MRAID_1024x768:
            case ApiAssetGroupType.MRAID_768x1024: {
                return HyBid.getConfigManager() != null
                        && !HyBid.getConfigManager().getFeatureResolver()
                        .isRenderingSupported(RemoteConfigFeature.Rendering.MRAID) ?
                        null : new MraidInterstitialPresenter(mContext, ad, mZoneId, htmlSkipOffset);
            }
            case ApiAssetGroupType.VAST_INTERSTITIAL: {
                return HyBid.getConfigManager() != null
                        && !HyBid.getConfigManager().getFeatureResolver()
                        .isRenderingSupported(RemoteConfigFeature.Rendering.VAST) ?
                        null : new VastInterstitialPresenter(mContext, ad, mZoneId, videoSkipOffset);
            }
            default: {
                Logger.e(TAG, "Incompatible asset group type: " + assetGroupId + ", for interstitial ad format.");
                return null;
            }
        }
    }
}
