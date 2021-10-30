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
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.config.ConfigManager;
import net.pubnative.lite.sdk.DiagnosticConstants;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdRequest;
import net.pubnative.lite.sdk.models.AdRequestFactory;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.utils.HeaderBiddingUtils;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.PNInitializationHelper;
import net.pubnative.lite.sdk.utils.PrebidUtils;
import net.pubnative.lite.sdk.utils.json.JsonOperations;
import net.pubnative.lite.sdk.vpaid.VideoAdCache;
import net.pubnative.lite.sdk.vpaid.VideoAdCacheItem;
import net.pubnative.lite.sdk.vpaid.VideoAdProcessor;
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
    private AdCache mAdCache;
    private VideoAdCache mVideoCache;
    private final AdRequestFactory mAdRequestFactory;
    private final ReportingController mReportingController;
    private final PNInitializationHelper mInitializationHelper;
    private String mZoneId;
    private RequestListener mRequestListener;
    private boolean mIsDestroyed;
    private AdSize mAdSize;
    private final JSONObject mPlacementParams;
    private final boolean mIsRewarded = false;
    private boolean mAutoCacheOnLoad = true;

    JSONObject jsonCacheParams;
    private Long mRequestTimeMilliseconds = 0L;
    private Long mCacheTimeMilliseconds = 0L;

    public RequestManager() {
        this(HyBid.getApiClient(), HyBid.getAdCache(), HyBid.getVideoAdCache(), HyBid.getConfigManager(), new AdRequestFactory(), HyBid.getReportingController(), new PNInitializationHelper());
    }

    RequestManager(PNApiClient apiClient,
                   AdCache adCache,
                   VideoAdCache videoCache,
                   ConfigManager configManager,
                   AdRequestFactory adRequestFactory,
                   ReportingController reportingController,
                   PNInitializationHelper initializationHelper) {
        mApiClient = apiClient;
        mAdCache = adCache;
        mVideoCache = videoCache;
        mReportingController = reportingController;
        mAdRequestFactory = adRequestFactory;
        mInitializationHelper = initializationHelper;
        mPlacementParams = new JSONObject();
        mAdSize = AdSize.SIZE_320x50;
        JsonOperations.putJsonString(mPlacementParams, DiagnosticConstants.KEY_AD_SIZE, mAdSize.toString());
        JsonOperations.putJsonString(mPlacementParams, DiagnosticConstants.KEY_INTEGRATION_TYPE, IntegrationType.HEADER_BIDDING.getCode());
        mConfigManager = configManager;

        jsonCacheParams = new JSONObject();
        try {
            jsonCacheParams.put(Reporting.Key.APP_TOKEN, HyBid.getAppToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setRequestListener(RequestListener requestListener) {
        mRequestListener = requestListener;
    }

    public void setZoneId(String zoneId) {
        mZoneId = zoneId;
    }

    public void setAdSize(AdSize adSize) {
        mAdSize = adSize;
        if (adSize != null) {
            JsonOperations.putJsonString(mPlacementParams, DiagnosticConstants.KEY_AD_SIZE, adSize.toString());
        } else {
            JsonOperations.removeJsonValue(mPlacementParams, DiagnosticConstants.KEY_AD_SIZE);
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

        //Disable refresh until we have a proper mechanism to handle apps with no config.
        /*if (mConfigManager != null) {
            mConfigManager.refreshConfig();
        }*/

        mAdRequestFactory.createAdRequest(mZoneId, getAdSize(), isRewarded(), new AdRequestFactory.Callback() {
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

    public PNInitializationHelper getInitializationHelper() {
        return mInitializationHelper;
    }

    void requestAdFromApi(final AdRequest adRequest) {
        if (mApiClient == null) {
            mApiClient = HyBid.getApiClient();
        }

        try {
            jsonCacheParams.put(Reporting.Key.TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.d(TAG, "Requesting ad for zone id: " + adRequest.zoneid);
        reportAdRequest(adRequest);
        mApiClient.getAd(adRequest, new PNApiClient.AdRequestListener() {
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
        if (mAdCache == null) {
            mAdCache = HyBid.getAdCache();
        }
        if (mVideoCache == null) {
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
        if (ad != null && !TextUtils.isEmpty(ad.getVast())) {
            try {
                jsonCacheParams.put(Reporting.Key.AD_TYPE, "VAST");
                jsonCacheParams.put(Reporting.Key.VAST, ad.getVast());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mRequestTimeMilliseconds = System.currentTimeMillis();
            VideoAdProcessor videoAdProcessor = new VideoAdProcessor();
            videoAdProcessor.process(mApiClient.getContext(), ad.getVast(), null, new VideoAdProcessor.Listener() {
                @Override
                public void onCacheSuccess(AdParams adParams, String videoFilePath, String endCardFilePath, List<String> omidVendors) {
                    if (mIsDestroyed) {
                        return;
                    }
                    mCacheTimeMilliseconds = System.currentTimeMillis();

                    if (omidVendors != null && !omidVendors.isEmpty()) {
                        JsonOperations.putStringArray(mPlacementParams, DiagnosticConstants.KEY_OM_VENDORS, omidVendors);
                    }

                    try {
                        jsonCacheParams.put(Reporting.Key.CACHE_TIME, String.valueOf(mCacheTimeMilliseconds - mRequestTimeMilliseconds));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    reportAdCache();

                    VideoAdCacheItem adCacheItem = new VideoAdCacheItem(adParams, videoFilePath, endCardFilePath);
                    mVideoCache.put(ad.getZoneId(), adCacheItem);
                    if (mAutoCacheOnLoad && mRequestListener != null) {
                        mRequestListener.onRequestSuccess(ad);
                    }
                }

                @Override
                public void onCacheError(Throwable error) {
                    if (mIsDestroyed) {
                        return;
                    }

                    Logger.w(TAG, error.getMessage());
                    if (!mAutoCacheOnLoad && mRequestListener != null) {
                        mRequestListener.onRequestFail(error);
                    }
                }
            });
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

            String adSize = null;
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
            String adSize = null;
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

    public void setIntegrationType(IntegrationType integrationType) {
        if (mAdRequestFactory != null) {
            mAdRequestFactory.setIntegrationType(integrationType);
            JsonOperations.putJsonString(mPlacementParams, DiagnosticConstants.KEY_INTEGRATION_TYPE, integrationType.getCode());
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
            JsonOperations.putJsonString(finalParams, DiagnosticConstants.KEY_AD_SIZE, getAdSize().toString());
        }
        if (HyBid.isViewabilityMeasurementActivated() && HyBid.getViewabilityManager() != null) {
            JsonOperations.putJsonBoolean(finalParams, DiagnosticConstants.KEY_OM_ENABLED, true);
        } else {
            JsonOperations.putJsonBoolean(finalParams, DiagnosticConstants.KEY_OM_ENABLED, false);
        }
        if (mApiClient != null) {
            JSONObject apiClientParams = mApiClient.getPlacementParams();
            if (apiClientParams != null) {
                JsonOperations.mergeJsonObjects(finalParams, apiClientParams);
            }
        }
        return finalParams;
    }
}
