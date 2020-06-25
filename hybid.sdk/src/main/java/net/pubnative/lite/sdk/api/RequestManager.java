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
package net.pubnative.lite.sdk.api;

import net.pubnative.lite.sdk.AdCache;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdRequest;
import net.pubnative.lite.sdk.models.AdRequestFactory;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.PNInitializationHelper;
import net.pubnative.lite.sdk.vpaid.VideoAdCache;
import net.pubnative.lite.sdk.vpaid.VideoAdCacheItem;
import net.pubnative.lite.sdk.vpaid.VideoAdProcessor;
import net.pubnative.lite.sdk.vpaid.response.AdParams;

/**
 * Created by erosgarciaponte on 08.01.18.
 */

public abstract class RequestManager {
    public interface RequestListener {
        void onRequestSuccess(Ad ad);

        void onRequestFail(Throwable throwable);
    }

    private static final String TAG = RequestManager.class.getSimpleName();
    private final PNApiClient mApiClient;
    private final AdCache mAdCache;
    private final VideoAdCache mVideoCache;
    private final AdRequestFactory mAdRequestFactory;
    private final PNInitializationHelper mInitializationHelper;
    private String mZoneId;
    private RequestListener mRequestListener;
    private boolean mIsDestroyed;

    public RequestManager() {
        this(HyBid.getApiClient(), HyBid.getAdCache(), HyBid.getVideoAdCache(), new AdRequestFactory(), new PNInitializationHelper());
    }

    RequestManager(PNApiClient apiClient,
                   AdCache adCache,
                   VideoAdCache videoCache,
                   AdRequestFactory adRequestFactory,
                   PNInitializationHelper initializationHelper) {
        mApiClient = apiClient;
        mAdCache = adCache;
        mVideoCache = videoCache;
        mAdRequestFactory = adRequestFactory;
        mInitializationHelper = initializationHelper;
    }

    public void setRequestListener(RequestListener requestListener) {
        mRequestListener = requestListener;
    }

    public void setZoneId(String zoneId) {
        mZoneId = zoneId;
    }

    public void requestAd() {
        if (!CheckUtils.NoThrow.checkArgument(mInitializationHelper.isInitialized(),
                "HyBid SDK has not been initialized. Please call HyBid#initialize in your application's onCreate method.")) {
            return;
        }

        if (!CheckUtils.NoThrow.checkNotNull(HyBid.getDeviceInfo(),
                "HyBid SDK has not been initialized yet. Please call HyBid#initialize in your application's onCreate method.")) {
            return;
        }

        if (!CheckUtils.NoThrow.checkNotNull(HyBid.getUserDataManager(),
                "HyBid SDK has not been initialized yet. Please call HyBid#initialize in your application's onCreate method.")) {
            return;
        }

        if (!CheckUtils.NoThrow.checkNotNull(mZoneId, "zone id cannot be null")) {
            return;
        }

        if (!CheckUtils.NoThrow.checkArgument(!mIsDestroyed, "RequestManager has been destroyed")) {
            return;
        }

        mAdRequestFactory.createAdRequest(mZoneId, getAdSize(), new AdRequestFactory.Callback() {
            @Override
            public void onRequestCreated(AdRequest adRequest) {
                requestAdFromApi(adRequest);
            }
        });
    }

    void requestAdFromApi(final AdRequest adRequest) {
        Logger.d(TAG, "Requesting ad for zone id: " + adRequest.zoneid);
        mApiClient.getAd(adRequest, new PNApiClient.AdRequestListener() {
            @Override
            public void onSuccess(Ad ad) {
                if (mIsDestroyed) {
                    return;
                }

                Logger.d(TAG, "Received ad response for zone id: " + adRequest.zoneid);
                processAd(adRequest, ad);
            }

            @Override
            public void onFailure(Throwable throwable) {
                if (mIsDestroyed) {
                    return;
                }

                Logger.w(TAG, throwable.getMessage());
                if (mRequestListener != null) {
                    mRequestListener.onRequestFail(throwable);
                }
            }
        });
    }

    private void processAd(final AdRequest adRequest, final Ad ad) {
        ad.setZoneId(adRequest.zoneid);
        mAdCache.put(adRequest.zoneid, ad);

        switch (ad.assetgroupid) {
            case ApiAssetGroupType.VAST_INTERSTITIAL_1:
            case ApiAssetGroupType.VAST_INTERSTITIAL_2:
            case ApiAssetGroupType.VAST_INTERSTITIAL_3:
            case ApiAssetGroupType.VAST_INTERSTITIAL_4:
            case ApiAssetGroupType.VAST_MRECT: {
                VideoAdProcessor videoAdProcessor = new VideoAdProcessor();
                videoAdProcessor.process(mApiClient.getContext(), ad.getVast(), null, new VideoAdProcessor.Listener() {
                    @Override
                    public void onCacheSuccess(AdParams adParams, String videoFilePath, String endCardFilePath) {
                        if (mIsDestroyed) {
                            return;
                        }

                        VideoAdCacheItem adCacheItem = new VideoAdCacheItem(adParams, videoFilePath, endCardFilePath);
                        mVideoCache.put(adRequest.zoneid, adCacheItem);
                        if (mRequestListener != null) {
                            mRequestListener.onRequestSuccess(ad);
                        }
                    }

                    @Override
                    public void onCacheError(Throwable error) {
                        if (mIsDestroyed) {
                            return;
                        }

                        Logger.w(TAG, error.getMessage());
                        if (mRequestListener != null) {
                            mRequestListener.onRequestFail(error);
                        }
                    }
                });
                break;
            }
            default: {
                if (mRequestListener != null) {
                    mRequestListener.onRequestSuccess(ad);
                }
            }
        }
    }

    public void setIntegrationType(IntegrationType integrationType) {
        if (mAdRequestFactory != null) {
            mAdRequestFactory.setIntegrationType(integrationType);
        }
    }

    public void destroy() {
        mRequestListener = null;
        mIsDestroyed = true;
    }

    protected abstract String getAdSize();
}
