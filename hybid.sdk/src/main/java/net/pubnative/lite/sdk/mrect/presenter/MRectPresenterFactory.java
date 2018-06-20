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

import android.content.Context;

import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.Logger;

/**
 * Created by erosgarciaponte on 12.01.18.
 */

public class MRectPresenterFactory {
    private static final String TAG = MRectPresenterFactory.class.getSimpleName();
    private final Context mContext;

    public MRectPresenterFactory(Context context) {
        mContext = context;
    }

    public MRectPresenter createMRectPresenter(Ad ad,
                                               MRectPresenter.Listener mRectPresenterListener) {
        final MRectPresenter mRectPresenter = fromCreativeType(ad.assetgroupid, ad);
        if (mRectPresenter == null) {
            return null;
        }

        final MRectPresenterDecorator mRectPresenterDecorator = new MRectPresenterDecorator(mRectPresenter,
                new AdTracker(ad.getBeacons(Ad.Beacon.IMPRESSION), ad.getBeacons(Ad.Beacon.CLICK)), mRectPresenterListener);
        mRectPresenter.setListener(mRectPresenterDecorator);
        return mRectPresenterDecorator;
    }

    MRectPresenter fromCreativeType(int assetGroupId, Ad ad) {
        switch (assetGroupId) {
            case ApiAssetGroupType.MRAID_MRECT: {
                return new MraidMRectPresenter(mContext, ad);
            }
            case ApiAssetGroupType.VAST_MRECT: {
                return new VastMRectPresenter(mContext, ad);
            }
            default: {
                Logger.e(TAG, "Incompatible asset group type: " + assetGroupId + ", for MRect ad format.");
                return null;
            }
        }
    }
}
