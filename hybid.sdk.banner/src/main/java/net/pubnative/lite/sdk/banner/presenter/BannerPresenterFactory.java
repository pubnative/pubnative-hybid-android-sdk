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
package net.pubnative.lite.sdk.banner.presenter;

import android.content.Context;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.models.ImpressionTrackingMethod;
import net.pubnative.lite.sdk.models.RemoteConfigFeature;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.presenter.PresenterFactory;
import net.pubnative.lite.sdk.utils.Logger;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public class BannerPresenterFactory extends PresenterFactory {
    private static final String TAG = BannerPresenterFactory.class.getSimpleName();

    public BannerPresenterFactory(Context context) {
        super(context);
    }

    @Override
    public AdPresenter fromCreativeType(int assetGroupId, Ad ad, AdSize adSize) {
        return fromCreativeType(assetGroupId, ad, adSize, ImpressionTrackingMethod.AD_RENDERED);
    }

    @Override
    protected AdPresenter fromCreativeType(int assetGroupId, Ad ad, AdSize adSize, ImpressionTrackingMethod trackingMethod) {
        switch (assetGroupId) {
            case ApiAssetGroupType.MRAID_160x600:
            case ApiAssetGroupType.MRAID_250x250:
            case ApiAssetGroupType.MRAID_300x50:
            case ApiAssetGroupType.MRAID_300x250:
            case ApiAssetGroupType.MRAID_300x600:
            case ApiAssetGroupType.MRAID_320x50:
            case ApiAssetGroupType.MRAID_320x100:
            case ApiAssetGroupType.MRAID_320x480:
            case ApiAssetGroupType.MRAID_480x320:
            case ApiAssetGroupType.MRAID_728x90:
            case ApiAssetGroupType.MRAID_768x1024:
            case ApiAssetGroupType.MRAID_1024x768: {
                return HyBid.getConfigManager() != null
                        && !HyBid.getConfigManager().getFeatureResolver()
                        .isRenderingSupported(RemoteConfigFeature.Rendering.MRAID) ?
                        null : new MraidAdPresenter(getContext(), ad, adSize, trackingMethod);
            }
            case ApiAssetGroupType.VAST_MRECT: {
                return HyBid.getConfigManager() != null
                        && !HyBid.getConfigManager().getFeatureResolver()
                        .isRenderingSupported(RemoteConfigFeature.Rendering.VAST) ?
                        null : new VastAdPresenter(getContext(), ad, adSize, trackingMethod);
            }
            default: {
                Logger.e(TAG, "Incompatible asset group type: " + assetGroupId + ", for banner ad format.");
                return null;
            }
        }
    }
}
