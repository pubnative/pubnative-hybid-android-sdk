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
package net.pubnative.lite.sdk.api;

import android.text.TextUtils;

import net.pubnative.lite.sdk.AdCache;
import net.pubnative.lite.sdk.CacheListener;
import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.HyBidError;
import net.pubnative.lite.sdk.HyBidErrorCode;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.config.ConfigManager;
import net.pubnative.lite.sdk.config.FeatureResolver;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdRequest;
import net.pubnative.lite.sdk.models.AdRequestFactory;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.models.RemoteConfigFeature;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.utils.HeaderBiddingUtils;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.PNInitializationHelper;
import net.pubnative.lite.sdk.utils.PrebidUtils;
import net.pubnative.lite.sdk.utils.json.JsonOperations;
import net.pubnative.lite.sdk.vpaid.VideoAdCache;
import net.pubnative.lite.sdk.vpaid.VideoAdCacheItem;
import net.pubnative.lite.sdk.vpaid.VideoAdProcessor;
import net.pubnative.lite.sdk.vpaid.models.EndCardData;
import net.pubnative.lite.sdk.vpaid.response.AdParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by erosgarciaponte on 08.01.18.
 */
public class RequestManager {
    public interface RequestListener {
        void onRequestSuccess(Ad ad);

        void onRequestFail(Throwable throwable);
    }

    private static final String TAG = RequestManager.class.getSimpleName();
    private final ConfigManager mConfigManager;
    private PNApiClient mApiClient;
    private DeviceInfo mDeviceInfo;
    private AdCache mAdCache;
    private VideoAdCache mVideoCache;
    private final AdRequestFactory mAdRequestFactory;
    private final ReportingController mReportingController;
    private final PNInitializationHelper mInitializationHelper;
    private String mAppToken;
    private String mZoneId;
    private RequestListener mRequestListener;
    private boolean mIsDestroyed;
    private AdSize mAdSize;
    private final JSONObject mPlacementParams;
    private boolean mAutoCacheOnLoad = true;
    private boolean mCacheStarted = false;
    private boolean mCacheFinished = false;

    final JSONObject jsonCacheParams;
    private Long mRequestTimeMilliseconds = 0L;
    private Long mCacheTimeMilliseconds = 0L;

    public RequestManager() {
        this(null);
    }

    public RequestManager(AdSize adSize) {
        this(HyBid.getApiClient(), HyBid.getDeviceInfo(), HyBid.getAdCache(), HyBid.getVideoAdCache(), HyBid.getConfigManager(), new AdRequestFactory(), HyBid.getReportingController(), adSize, new PNInitializationHelper());
    }

    RequestManager(PNApiClient apiClient,
                   DeviceInfo deviceInfo,
                   AdCache adCache,
                   VideoAdCache videoCache,
                   ConfigManager configManager,
                   AdRequestFactory adRequestFactory,
                   ReportingController reportingController,
                   AdSize adSize,
                   PNInitializationHelper initializationHelper) {
        mApiClient = apiClient;
        mDeviceInfo = deviceInfo;
        mAdCache = adCache;
        mVideoCache = videoCache;
        mReportingController = reportingController;
        mAdRequestFactory = adRequestFactory;
        mInitializationHelper = initializationHelper;
        mPlacementParams = new JSONObject();
        if (adSize == null) {
            mAdSize = AdSize.SIZE_320x50;
        } else {
            mAdSize = adSize;
        }
        JsonOperations.putJsonString(mPlacementParams, Reporting.Key.AD_SIZE, mAdSize.toString());
        JsonOperations.putJsonString(mPlacementParams, Reporting.Key.INTEGRATION_TYPE, IntegrationType.HEADER_BIDDING.getCode());
        mConfigManager = configManager;

        jsonCacheParams = new JSONObject();
        if (mAppToken == null || TextUtils.isEmpty(mAppToken)) {
            mAppToken = HyBid.getAppToken();
        }

        try {
            jsonCacheParams.put(Reporting.Key.APP_TOKEN, mAppToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setRequestListener(RequestListener requestListener) {
        mRequestListener = requestListener;
    }

    public void setAppToken(String appToken) {
        this.mAppToken = appToken;
    }

    public void setZoneId(String zoneId) {
        mZoneId = zoneId;
    }

    public void setAdSize(AdSize adSize) {
        mAdSize = adSize;
        if (adSize != null) {
            JsonOperations.putJsonString(mPlacementParams, Reporting.Key.AD_SIZE, adSize.toString());
        } else {
            JsonOperations.removeJsonValue(mPlacementParams, Reporting.Key.AD_SIZE);
        }
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

        if (!isFormatEnabled()) {
            if (mRequestListener != null) {
                mRequestListener.onRequestFail(new HyBidError(HyBidErrorCode.DISABLED_FORMAT));
            }
        } else {
            mCacheStarted = false;
            mCacheFinished = false;

            mAdRequestFactory.createAdRequest(TextUtils.isEmpty(mAppToken) ? null : mAppToken, mZoneId,
                    getAdSize(), isRewarded(), new AdRequestFactory.Callback() {
                        @Override
                        public void onRequestCreated(AdRequest adRequest) {
                            requestAdFromApi(adRequest);
                            if (adRequest != null) {
                                try {
                                    jsonCacheParams.put(Reporting.Key.AD_REQUEST, adRequest.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    });
        }
    }

    public PNInitializationHelper getInitializationHelper() {
        return mInitializationHelper;
    }

    private boolean isFormatEnabled() {
        if (mConfigManager != null && mConfigManager.getConfig() != null) {
            FeatureResolver featureResolver = mConfigManager.getFeatureResolver();
            if (this instanceof BannerRequestManager || this instanceof MRectRequestManager || this instanceof LeaderboardRequestManager) {
                return featureResolver.isAdFormatEnabled(RemoteConfigFeature.AdFormat.BANNER);
            } else if (this instanceof InterstitialRequestManager) {
                return featureResolver.isAdFormatEnabled(RemoteConfigFeature.AdFormat.INTERSTITIAL);
            } else if (this instanceof RewardedRequestManager) {
                return featureResolver.isAdFormatEnabled(RemoteConfigFeature.AdFormat.REWARDED);
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    void requestAdFromApi(final AdRequest adRequest) {
        if (mApiClient == null) {
            mApiClient = HyBid.getApiClient();
        }

        if (mDeviceInfo == null) {
            mDeviceInfo = HyBid.getDeviceInfo();
        }

        try {
            jsonCacheParams.put(Reporting.Key.TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.d(TAG, "Requesting ad for zone id: " + adRequest.zoneid);
        reportAdRequest(adRequest);
        mApiClient.getAd(adRequest, mDeviceInfo.getUserAgent(), new PNApiClient.AdRequestListener() {
            @Override
            public void onSuccess(Ad ad) {
                if (mIsDestroyed) {
                    return;
                }

                Logger.d(TAG, "Received ad response for zone id: " + adRequest.zoneid);
                reportAdResponse(adRequest, ad);
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
        if (mAdCache == null || mAdCache != HyBid.getAdCache()) {
            mAdCache = HyBid.getAdCache();
        }
        if (mVideoCache == null || mVideoCache != HyBid.getVideoAdCache()) {
            mVideoCache = HyBid.getVideoAdCache();
        }
        ad.setZoneId(adRequest.zoneid);
        mAdCache.put(adRequest.zoneid, ad);

        switch (ad.assetgroupid) {
            case ApiAssetGroupType.VAST_INTERSTITIAL:
            case ApiAssetGroupType.VAST_MRECT: {
                if (mAutoCacheOnLoad) {
                    cacheAd(ad);
                } else {
                    if (mRequestListener != null) {
                        mRequestListener.onRequestSuccess(ad);
                    }
                }
                break;
            }
            default: {
                if (mRequestListener != null) {
                    mRequestListener.onRequestSuccess(ad);
                }
            }
        }
    }

    public void cacheAd(final Ad ad) {
        cacheAd(ad, null);
    }

    public void cacheAd(final Ad ad, final CacheListener cacheListener) {
        if (ad != null && !TextUtils.isEmpty(ad.getVast()) && !mCacheStarted && !mCacheFinished) {
            mCacheStarted = true;
            mCacheFinished = false;
            try {
                jsonCacheParams.put(Reporting.Key.AD_TYPE, "VAST");
                jsonCacheParams.put(Reporting.Key.VAST, ad.getVast());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mRequestTimeMilliseconds = System.currentTimeMillis();
            VideoAdProcessor videoAdProcessor = new VideoAdProcessor();
            videoAdProcessor.process(mApiClient.getContext(), ad.getVast(), getAdSize(), new VideoAdProcessor.Listener() {
                @Override
                public void onCacheSuccess(AdParams adParams, String videoFilePath, EndCardData endCardData,
                                           String endCardFilePath, List<String> omidVendors) {
                    if (mIsDestroyed) {
                        return;
                    }
                    mCacheTimeMilliseconds = System.currentTimeMillis();

                    if (omidVendors != null && !omidVendors.isEmpty()) {
                        JsonOperations.putStringArray(mPlacementParams, Reporting.Key.OM_VENDORS, omidVendors);
                    }

                    try {
                        jsonCacheParams.put(Reporting.Key.CACHE_TIME, String.valueOf(mCacheTimeMilliseconds - mRequestTimeMilliseconds));
                    } catch (JSONException e) {
                        Logger.w(TAG, e.getMessage());
                    }

                    reportAdCache();

                    boolean hasEndCard = adParams.getEndCardList() != null && !adParams.getEndCardList().isEmpty();
                    ad.setHasEndCard(hasEndCard);

                    VideoAdCacheItem adCacheItem = new VideoAdCacheItem(adParams, videoFilePath, endCardData, endCardFilePath);
                    mVideoCache.put(ad.getZoneId(), adCacheItem);
                    mCacheStarted = false;
                    mCacheFinished = true;
                    if (mAutoCacheOnLoad && mRequestListener != null) {
                        mRequestListener.onRequestSuccess(ad);
                    } else {
                        if (cacheListener != null) {
                            cacheListener.onCacheSuccess();
                        }
                    }
                }

                @Override
                public void onCacheError(Throwable error) {
                    if (mIsDestroyed) {
                        return;
                    }

                    Logger.w(TAG, error.getMessage());
                    mCacheStarted = false;
                    mCacheFinished = false;
                    if (mAutoCacheOnLoad && mRequestListener != null) {
                        mRequestListener.onRequestFail(error);
                    } else {
                        if (cacheListener != null) {
                            cacheListener.onCacheFailed(error);
                        }
                    }
                }
            });
        } else {
            if (cacheListener != null) {
                cacheListener.onCacheSuccess();
            }
        }
    }

    private void reportAdCache() {
        if (mReportingController != null) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.CACHE);
            JsonOperations.mergeJsonObjects(jsonCacheParams, getPlacementParams());
            reportingEvent.mergeJSONObject(jsonCacheParams);
            mReportingController.reportEvent(reportingEvent);
        }
    }

    private void reportAdRequest(AdRequest adRequest) {
        if (mReportingController != null) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.REQUEST);
            reportingEvent.setTimestamp(String.valueOf(System.currentTimeMillis()));

            String adSize;
            if (getAdSize() != null) {
                adSize = getAdSize().toString();
                reportingEvent.setAdSize(adSize);
            }
            reportingEvent.setPlacementId(adRequest.zoneid);
            mReportingController.reportEvent(reportingEvent);
        }
    }

    private void reportAdResponse(AdRequest adRequest, Ad adResponse) {
        if (mReportingController != null) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.RESPONSE);
            reportingEvent.setTimestamp(String.valueOf(System.currentTimeMillis()));
            String adSize;
            if (getAdSize() != null) {
                adSize = getAdSize().toString();
                reportingEvent.setAdSize(adSize);
            }
            reportingEvent.setPlacementId(adRequest.zoneid);
            reportingEvent.setCustomString(Reporting.Key.BID_PRICE,
                    HeaderBiddingUtils.getBidFromPoints(
                            adResponse.getECPM(), PrebidUtils.KeywordMode.THREE_DECIMALS));
            switch (adResponse.assetgroupid) {
                case ApiAssetGroupType.VAST_INTERSTITIAL:
                case ApiAssetGroupType.VAST_MRECT: {
                    reportingEvent.setCreativeType(Reporting.CreativeType.VIDEO);
                    break;
                }
                default: {
                    reportingEvent.setCreativeType(Reporting.CreativeType.STANDARD);
                }
            }
            mReportingController.reportEvent(reportingEvent);
        }
    }

    public void setMediationVendor(String mediationVendor) {
        if (mAdRequestFactory != null) {
            mAdRequestFactory.setMediationVendor(mediationVendor);
            if (!TextUtils.isEmpty(mediationVendor)) {
                JsonOperations.putJsonString(mPlacementParams, Reporting.Key.MEDIATION_VENDOR, mediationVendor);
            }
        }
    }

    public void setIntegrationType(IntegrationType integrationType) {
        if (mAdRequestFactory != null) {
            mAdRequestFactory.setIntegrationType(integrationType);
            JsonOperations.putJsonString(mPlacementParams, Reporting.Key.INTEGRATION_TYPE, integrationType.getCode());
        }
    }

    public void destroy() {
        mRequestListener = null;
        mIsDestroyed = true;
    }

    public AdSize getAdSize() {
        return mAdSize;
    }

    public boolean isRewarded() {
        boolean mIsRewarded = false;
        return mIsRewarded;
    }

    public boolean isAutoCacheOnLoad() {
        return mAutoCacheOnLoad;
    }

    public void setAutoCacheOnLoad(boolean autoCacheOnLoad) {
        this.mAutoCacheOnLoad = autoCacheOnLoad;
    }

    public JSONObject getPlacementParams() {
        JSONObject finalParams = new JSONObject();
        JsonOperations.mergeJsonObjects(finalParams, mPlacementParams);
        if (getAdSize() != null) {
            JsonOperations.putJsonString(finalParams, Reporting.Key.AD_SIZE, getAdSize().toString());
        }
        JsonOperations.putJsonBoolean(finalParams, Reporting.Key.OM_ENABLED, HyBid.isViewabilityMeasurementActivated() && HyBid.getViewabilityManager() != null);
        if (mApiClient != null) {
            JSONObject apiClientParams = mApiClient.getPlacementParams();
            if (apiClientParams != null) {
                JsonOperations.mergeJsonObjects(finalParams, apiClientParams);
            }
        }
        return finalParams;
    }
}
