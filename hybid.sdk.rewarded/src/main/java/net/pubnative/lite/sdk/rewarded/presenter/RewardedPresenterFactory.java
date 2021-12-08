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

import android.content.Context;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.models.RemoteConfigFeature;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.Logger;

public class RewardedPresenterFactory {
    private static final String TAG = RewardedPresenterFactory.class.getSimpleName();
    private final Context mContext;
    private final String mZoneId;

    public RewardedPresenterFactory(Context context, String zoneId) {
        mContext = context;
        mZoneId = zoneId;
    }

    public RewardedPresenter createRewardedPresenter(
            Ad ad,
            RewardedPresenter.Listener rewardedPresenterListener) {

        final RewardedPresenter rewardedPresenter = fromCreativeType(ad.assetgroupid, ad);
        if (rewardedPresenter == null) {
            return null;
        }

        final RewardedPresenterDecorator rewardedPresenterDecorator =
                new RewardedPresenterDecorator(rewardedPresenter, new AdTracker(
                        ad.getBeacons(Ad.Beacon.IMPRESSION),
                        ad.getBeacons(Ad.Beacon.CLICK)),
                        HyBid.getReportingController(),
                        rewardedPresenterListener);
        rewardedPresenter.setListener(rewardedPresenterDecorator);
        return rewardedPresenterDecorator;
    }

    RewardedPresenter fromCreativeType(int assetGroupId, Ad ad) {
        if (assetGroupId == ApiAssetGroupType.VAST_INTERSTITIAL) {
            return HyBid.getConfigManager() != null
                    && !HyBid.getConfigManager().getFeatureResolver()
                    .isRenderingSupported(RemoteConfigFeature.Rendering.VAST) ?
                    null : new VastRewardedPresenter(mContext, ad, mZoneId);
        }
        Logger.e(TAG, "Incompatible asset group type: " + assetGroupId + ", for rewarded ad format.");
        return null;
    }
}
