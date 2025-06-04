// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.api;

import static net.pubnative.lite.sdk.utils.AtomManager.AD_FORMAT;
import static net.pubnative.lite.sdk.utils.AtomManager.AD_SESSION_DATA;
import static net.pubnative.lite.sdk.utils.AtomManager.BID_PRICE;
import static net.pubnative.lite.sdk.utils.AtomManager.CAMPAIGN_ID;
import static net.pubnative.lite.sdk.utils.AtomManager.CREATIVE_ID;
import static net.pubnative.lite.sdk.utils.AtomManager.RENDERING_STATUS;
import static net.pubnative.lite.sdk.utils.AtomManager.RENDERING_SUCCESS;
import static net.pubnative.lite.sdk.utils.AtomManager.VIEWABILITY;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import net.pubnative.lite.sdk.AdCache;
import net.pubnative.lite.sdk.CacheListener;
import net.pubnative.lite.sdk.DeviceInfo;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingController;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdRequest;
import net.pubnative.lite.sdk.models.AdRequestFactory;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.models.PNAdRequest;
import net.pubnative.lite.sdk.models.PNAdRequestFactory;
import net.pubnative.lite.sdk.models.request.OpenRTBAdRequest;
import net.pubnative.lite.sdk.utils.AdTopicsAPIManager;
import net.pubnative.lite.sdk.utils.AtomManager;
import net.pubnative.lite.sdk.utils.CheckUtils;
import net.pubnative.lite.sdk.utils.HeaderBiddingUtils;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.PNInitializationHelper;
import net.pubnative.lite.sdk.utils.PrebidUtils;
import net.pubnative.lite.sdk.utils.json.JsonOperations;
import net.pubnative.lite.sdk.vpaid.VideoAdCache;
import net.pubnative.lite.sdk.vpaid.VideoAdCacheItem;
import net.pubnative.lite.sdk.vpaid.VideoAdProcessor;
import net.pubnative.lite.sdk.models.EndCardData;
import net.pubnative.lite.sdk.vpaid.response.AdParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by erosgarciaponte on 08.01.18.
 */
public class RequestManager {
    private IntegrationType mIntegrationType = IntegrationType.STANDALONE;

    public interface RequestListener {
        void onRequestSuccess(Ad ad);

        void onRequestFail(Throwable throwable);
    }

    public static final class AdFormat {
        public static final String HTML = "html";
        public static final String VIDEO = "video";
    }

    private static final String TAG = RequestManager.class.getSimpleName();
    private ApiClient mApiClient;
    private DeviceInfo mDeviceInfo;
    private AdCache mAdCache;
    private VideoAdCache mVideoCache;
    private final AdRequestFactory mAdRequestFactory;
    private final ReportingController mReportingController;
    private final PNInitializationHelper mInitializationHelper;
    private String mAppToken;
    private String mZoneId;
    private String mCustomUrl;
    private RequestListener mRequestListener;
    private boolean mIsDestroyed;
    private AdSize mAdSize;
    private final JSONObject mPlacementParams;
    private boolean mAutoCacheOnLoad = true;
    private boolean mCacheStarted = false;
    private boolean mCacheFinished = false;
    private String mAdFormat;

    final JSONObject jsonCacheParams;
    private Long mRequestTimeMilliseconds = 0L;
    private Long mCacheTimeMilliseconds = 0L;

    public RequestManager() {
        this(null);
    }

    public RequestManager(AdSize adSize) {
        this(HyBid.getApiClient(), HyBid.getDeviceInfo(), HyBid.getAdCache(), HyBid.getVideoAdCache(), new PNAdRequestFactory(), HyBid.getReportingController(), adSize, new PNInitializationHelper());
    }

    public RequestManager(ApiClient apiClient, AdRequestFactory requestFactory) {
        this(null, apiClient, requestFactory);
    }

    public RequestManager(AdSize adSize, ApiClient apiClient, AdRequestFactory requestFactory) {
        this(apiClient, HyBid.getDeviceInfo(), HyBid.getAdCache(), HyBid.getVideoAdCache(), requestFactory, HyBid.getReportingController(), adSize, new PNInitializationHelper());
    }

    RequestManager(ApiClient apiClient,
                   DeviceInfo deviceInfo,
                   AdCache adCache,
                   VideoAdCache videoCache,
                   AdRequestFactory requestFactory,
                   ReportingController reportingController,
                   AdSize adSize,
                   PNInitializationHelper initializationHelper) {
        mApiClient = apiClient;
        mDeviceInfo = deviceInfo;
        mAdCache = adCache;
        mVideoCache = videoCache;
        mReportingController = reportingController;
        mAdRequestFactory = requestFactory;
        mInitializationHelper = initializationHelper;
        mPlacementParams = new JSONObject();
        if (adSize == null) {
            mAdSize = AdSize.SIZE_320x50;
        } else {
            mAdSize = adSize;
        }
        JsonOperations.putJsonString(mPlacementParams, Reporting.Key.AD_SIZE, mAdSize.toString());
        JsonOperations.putJsonString(mPlacementParams, Reporting.Key.INTEGRATION_TYPE, IntegrationType.HEADER_BIDDING.getCode());

        jsonCacheParams = new JSONObject();
        if (mAppToken == null || TextUtils.isEmpty(mAppToken)) {
            mAppToken = HyBid.getAppToken();
        }

        try {
            jsonCacheParams.put(Reporting.Key.APP_TOKEN, mAppToken);
        } catch (JSONException e) {
            e.printStackTrace();
            HyBid.reportException(e);
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

    public void setCustomUrl(String customUrl) {
        mCustomUrl = customUrl;
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

        if (HyBid.isTestMode()) {
            Logger.w(TAG, "You are using Verve HyBid SDK on test mode. Please disable test mode before submitting your application for production.");
        }

        mCacheStarted = false;
        mCacheFinished = false;

        boolean protectedAudiencesAvailable = false;

        mAdRequestFactory.createAdRequest(TextUtils.isEmpty(mAppToken) ? null : mAppToken, mZoneId,
                getAdSize(), isRewarded(), protectedAudiencesAvailable, adRequest -> {
                    requestAdFromApi(adRequest);
                    if (adRequest != null) {
                        try {
                            jsonCacheParams.put(Reporting.Key.AD_REQUEST, adRequest.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            HyBid.reportException(e);
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

        if (mDeviceInfo == null) {
            mDeviceInfo = HyBid.getDeviceInfo();
        }

        try {
            jsonCacheParams.put(Reporting.Key.TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        } catch (JSONException e) {
            e.printStackTrace();
            HyBid.reportException(e);
        }

        Logger.d(TAG, "Requesting ad for zone id: " + adRequest.zoneId);
        if (adRequest instanceof PNAdRequest) {
            reportAdRequest((PNAdRequest) adRequest);
        } else {
            reportAdRequest((OpenRTBAdRequest) adRequest);
        }

        if (!TextUtils.isEmpty(mCustomUrl)) {
            mApiClient.setCustomUrl(mCustomUrl);
        }

        mApiClient.getAd(adRequest, mDeviceInfo.getUserAgent(), new PNApiClient.AdRequestListener() {
            @Override
            public void onSuccess(Ad ad) {
                if (mIsDestroyed) {
                    return;
                }

                Logger.d(TAG, "Received ad response for zone id: " + adRequest.zoneId);
                reportAdResponse(adRequest, ad, mIntegrationType);
                processAd(adRequest, ad);
            }

            @Override
            public void onFailure(Throwable throwable) {
                if (mIsDestroyed) {
                    return;
                }

                Logger.w(TAG, throwable.getMessage());
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (mRequestListener != null) {
                        mRequestListener.onRequestFail(throwable);
                    }
                });
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
        ad.setZoneId(adRequest.zoneId);
        mAdCache.put(adRequest.zoneId, ad);
        AdTopicsAPIManager.setTopicsAPIEnabled(mApiClient.getContext(), ad);
//        AtomManager.setAtomEnabled(mApiClient.getContext(), ad);

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
                HyBid.reportException(e);
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
                        HyBid.reportException(e);
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
        if (mReportingController != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.CACHE);
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
            JsonOperations.mergeJsonObjects(jsonCacheParams, getPlacementParams());
            reportingEvent.mergeJSONObject(jsonCacheParams);
            mReportingController.reportEvent(reportingEvent);
        }
    }

    private void reportAdRequest(PNAdRequest adRequest) {
        if (mReportingController != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.REQUEST);
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
            reportingEvent.setTimestamp(String.valueOf(System.currentTimeMillis()));

            String adSize;
            if (getAdSize() != null) {
                adSize = getAdSize().toString();
                reportingEvent.setAdSize(adSize);
            }
            reportingEvent.setPlacementId(adRequest.zoneId);
            reportingEvent.setSessionDuration(adRequest.sessionduration);
            reportingEvent.setImpDepth(adRequest.impdepth);
            reportingEvent.setAgeOfApp(adRequest.ageofapp);
            reportingEvent.setRequestType("apiv3");
            mReportingController.reportEvent(reportingEvent);
        }
    }

    private void reportAdRequest(OpenRTBAdRequest adRequest) {
        if (mReportingController != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.REQUEST);
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
            reportingEvent.setTimestamp(String.valueOf(System.currentTimeMillis()));

            String adSize;
            if (getAdSize() != null) {
                adSize = getAdSize().toString();
                reportingEvent.setAdSize(adSize);
            }
            reportingEvent.setPlacementId(adRequest.zoneId);
            reportingEvent.setRequestType("ortb");
            mReportingController.reportEvent(reportingEvent);
        }
    }

    public void sendAdSessionDataToAtom(Ad response, Double percentage) {
        if (response != null) {
            try {
                JSONObject jsonData = new JSONObject();
                if (response.getCreativeId() != null && !response.getCreativeId().isEmpty()) {

                    jsonData.put(CREATIVE_ID, response.getCreativeId());
                }
                if (response.getCampaignId() != null && !response.getCampaignId().isEmpty()) {
                    jsonData.put(CAMPAIGN_ID, response.getCampaignId());
                }
                jsonData.put(BID_PRICE, HeaderBiddingUtils.getBidFromPoints(
                        response.getECPM(), PrebidUtils.KeywordMode.THREE_DECIMALS));
                jsonData.put(AD_FORMAT, mAdFormat == null ? Reporting.AdFormat.NATIVE : mAdFormat);
                jsonData.put(RENDERING_STATUS, RENDERING_SUCCESS);
                jsonData.put(VIEWABILITY, percentage != null ? percentage : 0);
                HashMap<String, Object> adSessionData = new HashMap<>();
                adSessionData.put(AD_SESSION_DATA, jsonData.toString());

                AtomManager.setAdSessionData(adSessionData);
            } catch (JSONException e) {
                Logger.d(TAG, "Error while sending ad session data to Atom: " + e.getMessage());
            }
        }
    }

    private void reportAdResponse(AdRequest adRequest, Ad adResponse, IntegrationType mIntegrationType) {
        if (mReportingController != null && HyBid.isReportingEnabled()) {
            ReportingEvent reportingEvent = new ReportingEvent();
            reportingEvent.setEventType(Reporting.EventType.RESPONSE);
            reportingEvent.setPlatform(Reporting.Platform.ANDROID);
            reportingEvent.setSdkVersion(HyBid.getSDKVersionInfo(mIntegrationType));
            reportingEvent.setTimestamp(String.valueOf(System.currentTimeMillis()));
            String adSize;
            if (getAdSize() != null) {
                adSize = getAdSize().toString();
                reportingEvent.setAdSize(adSize);
            }
            reportingEvent.setPlacementId(adRequest.zoneId);
            reportingEvent.setImpId(adResponse.getSessionId());
            reportingEvent.setCampaignId(adResponse.getCampaignId());
            reportingEvent.setConfigId(adResponse.getConfigId());
            reportingEvent.setCustomString(Reporting.Key.BID_PRICE,
                    HeaderBiddingUtils.getBidFromPoints(
                            adResponse.getECPM(), PrebidUtils.KeywordMode.THREE_DECIMALS));
            switch (adResponse.assetgroupid) {
                case ApiAssetGroupType.MRAID_320x50:
                case ApiAssetGroupType.MRAID_300x50:
                case ApiAssetGroupType.MRAID_300x250:
                case ApiAssetGroupType.MRAID_728x90:
                case ApiAssetGroupType.MRAID_160x600:
                case ApiAssetGroupType.MRAID_250x250:
                case ApiAssetGroupType.MRAID_300x600:
                case ApiAssetGroupType.MRAID_320x100: {
                    mAdFormat = Reporting.AdFormat.BANNER;
                    reportingEvent.setAdFormat(mAdFormat);
                    reportingEvent.setCreativeType(Reporting.CreativeType.STANDARD);
                    break;
                }

                case ApiAssetGroupType.MRAID_320x480:
                case ApiAssetGroupType.MRAID_1024x768:
                case ApiAssetGroupType.MRAID_768x1024:
                case ApiAssetGroupType.MRAID_480x320: {
                    if (isRewarded()) {
                        mAdFormat = Reporting.AdFormat.REWARDED;
                        reportingEvent.setAdFormat(mAdFormat);
                    } else {
                        mAdFormat = Reporting.AdFormat.FULLSCREEN;
                        reportingEvent.setAdFormat(mAdFormat);
                    }
                    reportingEvent.setCreativeType(Reporting.CreativeType.STANDARD);
                    break;
                }

                case ApiAssetGroupType.VAST_INTERSTITIAL: {
                    if (isRewarded()) {
                        mAdFormat = Reporting.AdFormat.REWARDED;
                        reportingEvent.setAdFormat(mAdFormat);
                    } else {
                        mAdFormat = Reporting.AdFormat.FULLSCREEN;
                        reportingEvent.setAdFormat(mAdFormat);
                    }
                    reportingEvent.setCreativeType(Reporting.CreativeType.VIDEO);
                    break;
                }

                case ApiAssetGroupType.VAST_MRECT: {
                    mAdFormat = Reporting.AdFormat.BANNER;
                    reportingEvent.setAdFormat(mAdFormat);
                    reportingEvent.setCreativeType(Reporting.CreativeType.VIDEO);
                    break;
                }
                default: {
                    mAdFormat = Reporting.AdFormat.NATIVE;
                    reportingEvent.setAdFormat(mAdFormat);
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
        if (integrationType != null) {
            mIntegrationType = integrationType;
        }
        if (mAdRequestFactory != null) {
            mAdRequestFactory.setIntegrationType(integrationType);
            JsonOperations.putJsonString(mPlacementParams, Reporting.Key.INTEGRATION_TYPE, integrationType.getCode());
        }
    }

    public void setAdFormat(String adFormat) {
        if (mAdRequestFactory != null) {
            mAdRequestFactory.setAdFormat(adFormat);
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

    public IntegrationType getIntegrationType() {
        return mIntegrationType;
    }
}
