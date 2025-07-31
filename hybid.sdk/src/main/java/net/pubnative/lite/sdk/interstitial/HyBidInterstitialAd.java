// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.interstitial;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.lite.sdk.CacheListener;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.HyBidError;
import net.pubnative.lite.sdk.HyBidErrorCode;
import net.pubnative.lite.sdk.VideoListener;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.api.InterstitialRequestManager;
import net.pubnative.lite.sdk.api.OpenRTBApiClient;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.interstitial.presenter.InterstitialPresenter;
import net.pubnative.lite.sdk.interstitial.presenter.InterstitialPresenterFactory;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.models.EndCardData;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.models.OpenRTBAdRequestFactory;
import net.pubnative.lite.sdk.models.SdkEventType;
import net.pubnative.lite.sdk.models.SkipOffset;
import net.pubnative.lite.sdk.network.PNHttpClient;
import net.pubnative.lite.sdk.prefs.SessionImpressionPrefs;
import net.pubnative.lite.sdk.utils.AdEndCardManager;
import net.pubnative.lite.sdk.utils.AdRequestRegistry;
import net.pubnative.lite.sdk.utils.AdTracker;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.MarkupUtils;
import net.pubnative.lite.sdk.utils.SignalDataProcessor;
import net.pubnative.lite.sdk.utils.SkipOffsetManager;
import net.pubnative.lite.sdk.utils.json.JsonOperations;
import net.pubnative.lite.sdk.vpaid.VideoAdCacheItem;
import net.pubnative.lite.sdk.vpaid.VideoAdProcessor;
import net.pubnative.lite.sdk.vpaid.response.AdParams;
import net.pubnative.lite.sdk.vpaid.vast.VastUrlUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HyBidInterstitialAd implements RequestManager.RequestListener, InterstitialPresenter.Listener, VideoListener {
    private static final String TAG = HyBidInterstitialAd.class.getSimpleName();
    private static final long TIME_TO_EXPIRE = 1800000;

    public interface Listener {
        void onInterstitialLoaded();

        void onInterstitialLoadFailed(Throwable error);

        void onInterstitialImpression();

        void onInterstitialDismissed();

        void onInterstitialClick();
    }

    private RequestManager mRequestManager;
    private RequestManager mORTBRequestManager;
    private InterstitialPresenter mPresenter;
    private AdTracker mAdTracker;
    private final Listener mListener;
    private VideoListener mVideoListener;
    private final Context mContext;
    private final String mAppToken;
    private String mZoneId;
    private String mCustomUrl;
    private SignalDataProcessor mSignalDataProcessor;
    private Ad mAd;
    private JSONObject mPlacementParams;
    private boolean mReady = false;
    private SkipOffset mHtmlSkipOffset;
    private SkipOffset mVideoSkipOffset;
    private boolean mIsDestroyed = false;
    private long mInitialLoadTime = -1;
    private long mInitialRenderTime = -1;
    private String mScreenIabCategory;
    private String mScreenKeywords;
    private String mUserIntent;
    private boolean mIsExchange;


    public HyBidInterstitialAd(Activity activity, Listener listener) {
        this((Context) activity, "", listener);
    }

    public HyBidInterstitialAd(Activity activity, String zoneId, Listener listener) {
        this((Context) activity, zoneId, listener);
    }

    public HyBidInterstitialAd(Context context, String zoneId, Listener listener) {
        this(context, null, zoneId, listener);
    }

    public HyBidInterstitialAd(Context context, String appToken, String zoneId, Listener listener) {
        if (!HyBid.isInitialized()) {
            Log.v(TAG, "HyBid SDK is not initiated yet. Please initiate it before creating a HyBidInterstitialAd");
        }
        mRequestManager = new InterstitialRequestManager();
        mORTBRequestManager = new RequestManager(new OpenRTBApiClient(context), new OpenRTBAdRequestFactory());
        mContext = context;
        mAppToken = appToken;
        mZoneId = zoneId;
        mListener = listener;
        mPlacementParams = new JSONObject();

        //Zone Id
        addReportingKey(Reporting.Key.ZONE_ID, mZoneId);

        mHtmlSkipOffset = new SkipOffset(SkipOffsetManager.getDefaultHtmlInterstitialSkipOffset(), false);
        mVideoSkipOffset = new SkipOffset(SkipOffsetManager.getDefaultVideoWithoutEndCardSkipOffset(), false);

        mRequestManager.setIntegrationType(IntegrationType.STANDALONE);
        mORTBRequestManager.setIntegrationType(IntegrationType.STANDALONE);
    }

    public void load() {

        //Timestamp
        addReportingKey(Reporting.Key.TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        if (HyBid.getAppToken() != null)
            //AppToken
            addReportingKey(Reporting.Key.APP_TOKEN, HyBid.getAppToken());
        //Ad Type
        addReportingKey(Reporting.Key.AD_TYPE, Reporting.AdFormat.FULLSCREEN);
        if (mRequestManager != null && mRequestManager.getAdSize() != null)
            //Ad Size
            addReportingKey(Reporting.Key.AD_SIZE, mRequestManager.getAdSize().toString());
        //Integration Type
        addReportingKey(Reporting.Key.INTEGRATION_TYPE, IntegrationType.STANDALONE);

        if (!HyBid.isInitialized()) {
            mInitialLoadTime = System.currentTimeMillis();
            invokeOnLoadFailed(new HyBidError(HyBidErrorCode.NOT_INITIALISED));
        } else if (TextUtils.isEmpty(mZoneId)) {
            mInitialLoadTime = System.currentTimeMillis();
            invokeOnLoadFailed(new HyBidError(HyBidErrorCode.INVALID_ZONE_ID));
        } else {
            cleanup();
            mInitialLoadTime = System.currentTimeMillis();
            if (mRequestManager != null) {
                if (!TextUtils.isEmpty(mAppToken)) {
                    mRequestManager.setAppToken(mAppToken);
                }
                mRequestManager.setZoneId(mZoneId);
                mRequestManager.setRequestListener(this);
                mIsExchange = false;
                mRequestManager.requestAd();
            }
        }
    }

    public void loadExchangeAd(String adFormat) {
        //Timestamp
        addReportingKey(Reporting.Key.TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        if (HyBid.getAppToken() != null)
            //AppToken
            addReportingKey(Reporting.Key.APP_TOKEN, HyBid.getAppToken());
        //Ad Type
        addReportingKey(Reporting.Key.AD_TYPE, Reporting.AdFormat.FULLSCREEN);

        mORTBRequestManager.setAdSize(AdSize.SIZE_INTERSTITIAL);
        //Ad Size
        addReportingKey(Reporting.Key.AD_SIZE, mORTBRequestManager.getAdSize().toString());

        //Integration Type
        addReportingKey(Reporting.Key.INTEGRATION_TYPE, IntegrationType.STANDALONE);

        if (!HyBid.isInitialized()) {
            mInitialLoadTime = System.currentTimeMillis();
            invokeOnLoadFailed(new HyBidError(HyBidErrorCode.NOT_INITIALISED));
        } else if (TextUtils.isEmpty(mZoneId)) {
            mInitialLoadTime = System.currentTimeMillis();
            invokeOnLoadFailed(new HyBidError(HyBidErrorCode.INVALID_ZONE_ID));
        } else {
            cleanup();
            mInitialLoadTime = System.currentTimeMillis();
            if (!TextUtils.isEmpty(mAppToken)) {
                mORTBRequestManager.setAppToken(mAppToken);
            }
            if (!TextUtils.isEmpty(adFormat)) {
                mORTBRequestManager.setAdFormat(adFormat);
            }
            mORTBRequestManager.setZoneId(mZoneId);
            mORTBRequestManager.setRequestListener(this);
            mIsExchange = true;
            mORTBRequestManager.requestAd();
        }
    }

    public void loadExchangeAd() {
        loadExchangeAd(null);
    }

    public boolean show() {
        if (mPresenter != null && mReady) {
            mInitialRenderTime = System.currentTimeMillis();
            long adExpireTime = mInitialLoadTime + TIME_TO_EXPIRE;
            if (mInitialRenderTime < adExpireTime || mInitialLoadTime == -1) {
                mPresenter.show();
                if (mRequestManager != null && !mIsExchange) {
                    mRequestManager.sendAdSessionDataToAtom(mAd, 1.0);
                } else if (mORTBRequestManager != null && mIsExchange) {
                    mORTBRequestManager.sendAdSessionDataToAtom(mAd, 1.0);
                }
                return true;
            } else {
                Logger.e(TAG, "Ad has expired.");
                cleanup();
                invokeOnLoadFailed(new HyBidError(HyBidErrorCode.EXPIRED_AD));
                return false;
            }
        } else {
            Logger.e(TAG, "Can't display ad. Interstitial not ready.");
            return false;
        }
    }

    public boolean isReady() {
        return mReady;
    }

    public void destroy() {
        cleanup();
        mIsDestroyed = true;
        if (mRequestManager != null) {
            mRequestManager.destroy();
            mRequestManager = null;
        }
        if (mORTBRequestManager != null) {
            mORTBRequestManager.destroy();
            mORTBRequestManager = null;
        }
    }

    private void cleanup() {
        mReady = false;
        mPlacementParams = new JSONObject();
        mInitialLoadTime = -1;
        mInitialRenderTime = -1;
        if (mPresenter != null) {
            mPresenter.destroy();
            mPresenter = null;
        }

        if (mSignalDataProcessor != null) {
            mSignalDataProcessor.destroy();
            mSignalDataProcessor = null;
        }
    }

    public String getImpressionId() {
        return mAd != null ? mAd.getImpressionId() : null;
    }

    public String getCreativeId() {
        return mAd != null ? mAd.getCreativeId() : null;
    }

    public String getZoneId() {
        return mZoneId != null ? mZoneId : null;
    }

    public Integer getBidPoints() {
        return mAd != null ? mAd.getECPM() : 0;
    }

    public void setCustomUrl(String customUrl) {
        mCustomUrl = customUrl;
    }

    public JSONObject getPlacementParams() {
        JSONObject finalParams = new JSONObject();
        JsonOperations.mergeJsonObjects(finalParams, mPlacementParams);
        if (mRequestManager != null) {
            JSONObject requestManagerParams = mRequestManager.getPlacementParams();
            if (requestManagerParams != null) {
                JsonOperations.mergeJsonObjects(finalParams, requestManagerParams);
            }
        }
        if (mPresenter != null) {
            JSONObject adPresenterParams = mPresenter.getPlacementParams();
            if (adPresenterParams != null) {
                JsonOperations.mergeJsonObjects(finalParams, adPresenterParams);
            }
        }
        return finalParams;
    }

    private void renderAd() {
        IntegrationType integrationType = IntegrationType.IN_APP_BIDDING;
        if (mRequestManager != null)
            integrationType = mRequestManager.getIntegrationType();
        mPresenter = new InterstitialPresenterFactory(mContext, mZoneId).createInterstitialPresenter(mAd, mHtmlSkipOffset, mVideoSkipOffset, this, integrationType);
        if (mPresenter != null) {
            mPresenter.setVideoListener(this);
            mPresenter.load();
        } else {
            invokeOnLoadFailed(new HyBidError(HyBidErrorCode.UNSUPPORTED_ASSET));
        }
    }

    public void prepare() {
        prepare(null);
    }

    public void prepare(CacheListener cacheListener) {
        if (mRequestManager != null && mAd != null) {
            mRequestManager.cacheAd(mAd, cacheListener);
        }
    }

    public void prepareAd(final String adValue) {
        if (!TextUtils.isEmpty(adValue)) {
            mSignalDataProcessor = new SignalDataProcessor();
            mSignalDataProcessor.processSignalData(adValue, new SignalDataProcessor.Listener() {
                @Override
                public void onProcessed(Ad ad) {
                    if (ad != null) {
                        prepareAd(ad);
                    }
                }

                @Override
                public void onError(Throwable error) {
                    invokeOnLoadFailed(error);
                }
            });
        } else {
            invokeOnLoadFailed(new HyBidError(HyBidErrorCode.INVALID_SIGNAL_DATA));
        }
    }

    public void prepareAd(Ad ad) {
        if (ad != null) {
            mAd = ad;
            initializeAdTracker();
            checkRemoteConfigs();
            if (mAd != null && mAd.getZoneId() != null && !mAd.getZoneId().equalsIgnoreCase(mZoneId)) {
                mZoneId = mAd.getZoneId();
                JsonOperations.putJsonString(mPlacementParams, Reporting.Key.ZONE_ID, mZoneId);
            } else if (mZoneId == null) {
                mZoneId = "4";
            }
            IntegrationType integrationType = IntegrationType.IN_APP_BIDDING;
            if (mRequestManager != null)
                integrationType = mRequestManager.getIntegrationType();
            mPresenter = new InterstitialPresenterFactory(mContext, mZoneId).createInterstitialPresenter(mAd, mHtmlSkipOffset, mVideoSkipOffset, this, integrationType);
            if (mPresenter != null) {
                mPresenter.setVideoListener(this);
                mPresenter.load();
            } else {
                invokeOnLoadFailed(new HyBidError(HyBidErrorCode.UNSUPPORTED_ASSET));
            }
        } else {
            invokeOnLoadFailed(new HyBidError(HyBidErrorCode.INVALID_AD));
        }
    }

    public void prepareCustomMarkup(final String adValue) {
        prepareCustomMarkup("", adValue);
    }

    public void prepareCustomMarkup(final String zoneId, final String adValue) {
        if (!TextUtils.isEmpty(adValue)) {
            mZoneId = zoneId;
            final int assetGroupId;
            final Ad.AdType type;
            if (MarkupUtils.isVastXml(adValue)) {
                if (TextUtils.isEmpty(mZoneId)) {
                    mZoneId = "4";
                    JsonOperations.putJsonString(mPlacementParams, Reporting.Key.ZONE_ID, mZoneId);
                }
                assetGroupId = 15;
                type = Ad.AdType.VIDEO;
                VideoAdProcessor videoAdProcessor = new VideoAdProcessor();
                videoAdProcessor.process(mContext, adValue, null, new VideoAdProcessor.Listener() {
                    @Override
                    public void onCacheSuccess(AdParams adParams, String videoFilePath, EndCardData endCardData, String endCardFilePath, List<String> omidVendors) {
                        if (mIsDestroyed) {
                            return;
                        }

                        if (omidVendors != null && !omidVendors.isEmpty()) {
                            JsonOperations.putStringArray(mPlacementParams, Reporting.Key.OM_VENDORS, omidVendors);
                        }

                        boolean hasEndCard = adParams.getEndCardList() != null && !adParams.getEndCardList().isEmpty();

                        VideoAdCacheItem adCacheItem = new VideoAdCacheItem(adParams, videoFilePath, endCardData, endCardFilePath);
                        mAd = new Ad(assetGroupId, adValue, type);
                        mAd.setHasEndCard(hasEndCard);
                        initializeAdTracker();
                        HyBid.getAdCache().put(mZoneId, mAd);
                        HyBid.getVideoAdCache().put(mZoneId, adCacheItem);
                        checkRemoteConfigs();
                        IntegrationType integrationType = IntegrationType.IN_APP_BIDDING;
                        if (mRequestManager != null)
                            integrationType = mRequestManager.getIntegrationType();
                        mPresenter = new InterstitialPresenterFactory(mContext, mZoneId).createInterstitialPresenter(mAd, mHtmlSkipOffset, mVideoSkipOffset, HyBidInterstitialAd.this, integrationType);
                        if (mPresenter != null) {
                            mPresenter.setVideoListener(HyBidInterstitialAd.this);
                            mPresenter.load();
                        } else {
                            invokeOnLoadFailed(new HyBidError(HyBidErrorCode.UNSUPPORTED_ASSET));
                        }
                    }

                    @Override
                    public void onCacheError(Throwable error) {
                        if (mIsDestroyed) {
                            return;
                        }

                        Logger.w(TAG, "onCacheError", error);
                        invokeOnLoadFailed(error);
                    }
                });
            } else {
                if (TextUtils.isEmpty(mZoneId)) {
                    mZoneId = "3";
                }
                assetGroupId = 21;
                type = Ad.AdType.HTML;
                mAd = new Ad(assetGroupId, adValue, type);
                mAd.setZoneId(mZoneId);
                mAd.setHasEndCard(hasEndCard());
                initializeAdTracker();
                HyBid.getAdCache().put(mZoneId, mAd);
                checkRemoteConfigs();
                IntegrationType integrationType = IntegrationType.IN_APP_BIDDING;
                if (mRequestManager != null)
                    integrationType = mRequestManager.getIntegrationType();
                mPresenter = new InterstitialPresenterFactory(mContext, mZoneId).createInterstitialPresenter(mAd, mHtmlSkipOffset, mVideoSkipOffset, HyBidInterstitialAd.this, integrationType);
                if (mPresenter != null) {
                    mPresenter.setVideoListener(this);
                    mPresenter.load();
                } else {
                    invokeOnLoadFailed(new HyBidError(HyBidErrorCode.UNSUPPORTED_ASSET));
                }
            }
            JsonOperations.putJsonString(mPlacementParams, Reporting.Key.ZONE_ID, mZoneId);
        } else {
            invokeOnLoadFailed(new HyBidError(HyBidErrorCode.INVALID_ASSET));
        }
    }

    private void checkRemoteConfigs() {
        if (mAd == null)
            return;

        Integer htmlSkipOffset = SkipOffsetManager.getHTMLSkipOffset(mAd.getHtmlSkipOffset(), true);
        Integer videoSkipOffset = SkipOffsetManager.getVideoSkipOffset(mAd.getVideoSkipOffset(), null, null, hasEndCard(), true);

        if (htmlSkipOffset != null) {
            mHtmlSkipOffset = new SkipOffset(htmlSkipOffset, SkipOffsetManager.isCustomInterstitialHTMLSkipOffset());
        }

        if (videoSkipOffset != null) {
            mVideoSkipOffset = new SkipOffset(videoSkipOffset, SkipOffsetManager.isCustomInterstitialVideoSkipOffset());
        }
    }

    public void prepareVideoTag(final String adValue) {
        prepareVideoTag("", adValue);
    }

    public void prepareVideoTag(final String zoneId, final String adValue) {

        String url = VastUrlUtils.formatURL(adValue);

        Map<String, String> headers = new HashMap<>();
        String userAgent = HyBid.getDeviceInfo().getUserAgent();
        if (!TextUtils.isEmpty(userAgent)) {
            headers.put("User-Agent", userAgent);
        }

        final long initTime = System.currentTimeMillis();

        PNHttpClient.makeRequest(mContext, url, headers, null, new PNHttpClient.Listener() {
            @Override
            public void onSuccess(String response, Map<String, List<String>> headers) {
                registerAdRequest(url, response, initTime);
                if (!TextUtils.isEmpty(response)) {
                    prepareCustomMarkup(zoneId, response);
                }
            }

            @Override
            public void onFailure(Throwable error) {
                Logger.e(TAG, "Request failed: " + error.toString());
                invokeOnLoadFailed(new HyBidError(HyBidErrorCode.INVALID_ASSET));
            }
        });
    }

    private void registerAdRequest(String url, String response, long initTime) {
        long responseTime = System.currentTimeMillis() - initTime;

        JsonOperations.putJsonString(mPlacementParams, Reporting.Key.AD_REQUEST, url);
        JsonOperations.putJsonString(mPlacementParams, Reporting.Key.AD_RESPONSE, response);
        JsonOperations.putJsonLong(mPlacementParams, Reporting.Key.RESPONSE_TIME, responseTime);

        AdRequestRegistry.getInstance().setLastAdRequest(url, response, responseTime);
    }

    protected void invokeOnLoadFinished() {
        long loadTime = -1;
        if (mInitialLoadTime != -1) {
            loadTime = System.currentTimeMillis() - mInitialLoadTime;
            JsonOperations.putJsonLong(mPlacementParams, Reporting.Key.TIME_TO_LOAD, loadTime);
        }

        if (HyBid.getReportingController() != null && HyBid.isReportingEnabled()) {
            ReportingEvent loadEvent = new ReportingEvent();
            loadEvent.setEventType(Reporting.EventType.LOAD);
            loadEvent.setAdFormat(Reporting.AdFormat.FULLSCREEN);
            loadEvent.setPlatform(Reporting.Platform.ANDROID);
            if (mRequestManager != null) {
                loadEvent.setSdkVersion(HyBid.getSDKVersionInfo(mRequestManager.getIntegrationType()));
            }
            loadEvent.setCustomInteger(Reporting.Key.TIME_TO_LOAD, loadTime);
            if (mAd != null) {
                loadEvent.setImpId(mAd.getSessionId());
                loadEvent.setCampaignId(mAd.getCampaignId());
                loadEvent.setConfigId(mAd.getConfigId());
            }
            loadEvent.mergeJSONObject(getPlacementParams());
            HyBid.getReportingController().reportEvent(loadEvent);
        }

        if (mListener != null) {
            mListener.onInterstitialLoaded();
        }
    }

    protected void invokeOnLoadFailed(Throwable exception) {
        long loadTime = -1;
        if (mInitialLoadTime != -1) {
            loadTime = System.currentTimeMillis() - mInitialLoadTime;
            JsonOperations.putJsonLong(mPlacementParams, Reporting.Key.TIME_TO_LOAD_FAILED, loadTime);
        }

        if (HyBid.getReportingController() != null && HyBid.isReportingEnabled()) {
            ReportingEvent loadFailEvent = new ReportingEvent();
            loadFailEvent.setEventType(Reporting.EventType.LOAD_FAIL);
            loadFailEvent.setAdFormat(Reporting.AdFormat.FULLSCREEN);
            loadFailEvent.setPlatform(Reporting.Platform.ANDROID);
            if (mRequestManager != null) {
                loadFailEvent.setSdkVersion(HyBid.getSDKVersionInfo(mRequestManager.getIntegrationType()));
            }
            loadFailEvent.setCustomInteger(Reporting.Key.TIME_TO_LOAD, loadTime);
            if (mAd != null) {
                loadFailEvent.setImpId(mAd.getSessionId());
                loadFailEvent.setCampaignId(mAd.getCampaignId());
                loadFailEvent.setConfigId(mAd.getConfigId());
            }
            loadFailEvent.mergeJSONObject(getPlacementParams());
            HyBid.getReportingController().reportEvent(loadFailEvent);
        }

        if (exception instanceof HyBidError) {
            HyBidError hyBidError = (HyBidError) exception;
            if (hyBidError.getErrorCode() == HyBidErrorCode.NO_FILL) {
                Logger.w(TAG, exception.getMessage());
            } else {
                Logger.e(TAG, exception.getMessage());
            }
            sendLoadTracker(hyBidError.getErrorCode().getCode());
        } else {
            sendLoadTracker(HyBidErrorCode.UNKNOWN_ERROR.getCode());
        }
        if (mListener != null) {
            mListener.onInterstitialLoadFailed(exception);
        }
    }

    protected void invokeOnClick() {
        if (mListener != null) {
            mListener.onInterstitialClick();
        }
    }

    protected void invokeOnImpression() {
        if (mListener != null) {
            mListener.onInterstitialImpression();
        }
    }

    protected void invokeOnDismissed() {
        if (mListener != null) {
            mListener.onInterstitialDismissed();
        }
    }

    public void setVideoListener(VideoListener videoListener) {
        this.mVideoListener = videoListener;
    }

    public void setMediationVendor(String mediationVendor) {
        if (mRequestManager != null) {
            mRequestManager.setMediationVendor(mediationVendor);
        }
        if (mORTBRequestManager != null) {
            mORTBRequestManager.setMediationVendor(mediationVendor);
        }
    }

    public void setMediation(boolean isMediation) {
        if (mRequestManager != null) {
            mRequestManager.setIntegrationType(isMediation ? IntegrationType.MEDIATION : IntegrationType.STANDALONE);
        }
        if (mORTBRequestManager != null) {
            mORTBRequestManager.setIntegrationType(isMediation ? IntegrationType.MEDIATION : IntegrationType.STANDALONE);
        }
    }

    public boolean isAutoCacheOnLoad() {
        //TODO handle ortb manager via the unified interface
        if (mRequestManager != null) {
            return mRequestManager.isAutoCacheOnLoad();
        } else {
            return true;
        }
    }

    public void setAutoCacheOnLoad(boolean autoCacheOnLoad) {
        if (mRequestManager != null) {
            this.mRequestManager.setAutoCacheOnLoad(autoCacheOnLoad);
        }
        if (mORTBRequestManager != null) {
            this.mORTBRequestManager.setAutoCacheOnLoad(autoCacheOnLoad);
        }
    }

    //------------------------------ RequestManager Callbacks --------------------------------------
    @Override
    public void onRequestSuccess(Ad ad) {
        if (ad == null) {
            invokeOnLoadFailed(new HyBidError(HyBidErrorCode.NULL_AD));
        } else {
            mAd = ad;
            initializeAdTracker();
            checkRemoteConfigs();
            renderAd();
        }
    }

    @Override
    public void onRequestFail(Throwable throwable) {
        invokeOnLoadFailed(throwable);
    }

    //------------------------- IntersititialPresenter Callbacks -----------------------------------
    @Override
    public void onInterstitialLoaded(InterstitialPresenter interstitialPresenter) {
        mReady = true;
        invokeOnLoadFinished();
    }

    @Override
    public void onInterstitialError(InterstitialPresenter interstitialPresenter) {
        invokeOnLoadFailed(new HyBidError(HyBidErrorCode.ERROR_RENDERING_INTERSTITIAL));
    }

    @Override
    public void onInterstitialShown(InterstitialPresenter interstitialPresenter) {
        if (mInitialRenderTime != -1) {
            addReportingKey(Reporting.Key.RENDER_TIME, System.currentTimeMillis() - mInitialRenderTime);
        }
        reportAdRender(Reporting.AdFormat.FULLSCREEN, getPlacementParams());

        if (mZoneId != null && !TextUtils.isEmpty(mZoneId)) {
            if (mContext != null) {
//                DBManager dbManager = new DBManager(mContext);
//                dbManager.open();
//                dbManager.insert(mZoneId);
//                dbManager.close();

                SessionImpressionPrefs prefs = new SessionImpressionPrefs(mContext);
                prefs.insert(mZoneId);
            }
        }

        invokeOnImpression();
    }

    @Override
    public void onInterstitialClicked(InterstitialPresenter interstitialPresenter) {
        invokeOnClick();
    }

    @Override
    public void onInterstitialDismissed(InterstitialPresenter interstitialPresenter) {
        invokeOnDismissed();
    }

    //-------------------------------- VideoListener Callbacks -------------------------------------
    @Override
    public void onVideoError(int progressPercentage) {
        if (mVideoListener != null) {
            mVideoListener.onVideoError(progressPercentage);
        }
    }

    @Override
    public void onVideoStarted() {
        if (mVideoListener != null) {
            mVideoListener.onVideoStarted();
        }
    }

    @Override
    public void onVideoDismissed(int progressPercentage) {
        if (mVideoListener != null) {
            mVideoListener.onVideoDismissed(progressPercentage);
        }
    }

    @Override
    public void onVideoFinished() {
        if (mVideoListener != null) {
            mVideoListener.onVideoFinished();
        }
    }

    @Override
    public void onVideoSkipped() {
        if (mVideoListener != null) {
            mVideoListener.onVideoSkipped();
        }
    }

    private void addReportingKey(String key, Object value) {
        if (mPlacementParams != null) {
            if (value instanceof Long)
                JsonOperations.putJsonLong(mPlacementParams, key, (Long) value);
            else if (value instanceof Integer)
                JsonOperations.putJsonValue(mPlacementParams, key, (Integer) value);
            else if (value instanceof Double)
                JsonOperations.putJsonValue(mPlacementParams, key, (Double) value);
            else JsonOperations.putJsonString(mPlacementParams, key, value.toString());
        }
    }

    public void reportAdRender(String adFormat, JSONObject placementParams) {
        if (HyBid.getReportingController() != null && HyBid.isReportingEnabled()) {
            ReportingEvent event = new ReportingEvent();
            event.setEventType(Reporting.EventType.RENDER);
            event.setAdFormat(adFormat);
            event.setPlatform(Reporting.Platform.ANDROID);
            if (mRequestManager != null) {
                event.setSdkVersion(HyBid.getSDKVersionInfo(mRequestManager.getIntegrationType()));
            }
            event.setHasEndCard(hasEndCard());
            if (mAd != null) {
                event.setImpId(mAd.getSessionId());
                event.setCampaignId(mAd.getCampaignId());
                event.setConfigId(mAd.getConfigId());
            }
            event.mergeJSONObject(placementParams);

            HyBid.getReportingController().reportEvent(event);
        }
    }

    public boolean hasEndCard() {
        if (mAd != null)
            return AdEndCardManager.isEndCardEnabled(mAd);
        return false;
    }

    private void initializeAdTracker() {
        if (mAd != null) {
            mAdTracker = new AdTracker(
                    null,
                    null,
                    mAd.getBeacons(Ad.Beacon.SDK_EVENT),
                    null,
                    null);
        }
    }

    private void sendLoadTracker(Integer errorCode) {
        if (mAdTracker != null) {
            mAdTracker.trackSdkEvent(SdkEventType.LOAD, errorCode);
        }
    }
}
