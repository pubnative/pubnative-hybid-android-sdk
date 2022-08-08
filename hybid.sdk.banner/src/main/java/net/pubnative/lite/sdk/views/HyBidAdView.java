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
package net.pubnative.lite.sdk.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import net.pubnative.lite.sdk.CacheListener;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.HyBidError;
import net.pubnative.lite.sdk.HyBidErrorCode;
import net.pubnative.lite.sdk.VideoListener;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.analytics.ReportingEvent;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.auction.AdSource;
import net.pubnative.lite.sdk.auction.AdSourceConfig;
import net.pubnative.lite.sdk.auction.Auction;
import net.pubnative.lite.sdk.auction.HyBidAdSource;
import net.pubnative.lite.sdk.auction.VastTagAdSource;
import net.pubnative.lite.sdk.banner.presenter.BannerPresenterFactory;
import net.pubnative.lite.sdk.config.ConfigManager;
import net.pubnative.lite.sdk.models.APIAsset;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.models.ImpressionTrackingMethod;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.models.RemoteConfigAdSource;
import net.pubnative.lite.sdk.models.RemoteConfigFeature;
import net.pubnative.lite.sdk.models.RemoteConfigPlacement;
import net.pubnative.lite.sdk.network.PNHttpClient;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.MarkupUtils;
import net.pubnative.lite.sdk.utils.SignalDataProcessor;
import net.pubnative.lite.sdk.utils.ViewUtils;
import net.pubnative.lite.sdk.utils.json.JsonOperations;
import net.pubnative.lite.sdk.vpaid.VideoAdCacheItem;
import net.pubnative.lite.sdk.vpaid.VideoAdProcessor;
import net.pubnative.lite.sdk.vpaid.models.EndCardData;
import net.pubnative.lite.sdk.vpaid.response.AdParams;
import net.pubnative.lite.sdk.vpaid.vast.VastUrlUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class HyBidAdView extends FrameLayout implements RequestManager.RequestListener, AdPresenter.Listener, AdPresenter.ImpressionListener, VideoListener, Auction.Listener {

    private static final String TAG = HyBidAdView.class.getSimpleName();
    private static final int TIME_TO_EXPIRE = 1800000;
    private Position mPosition;
    private WindowManager mWindowManager;
    private FrameLayout mContainer;
    private String mScreenIabCategory;
    private String mScreenKeywords;
    private String mUserIntent;

    public interface Listener {
        void onAdLoaded();

        void onAdLoadFailed(Throwable error);

        void onAdImpression();

        void onAdClick();
    }

    private RequestManager mRequestManager;
    protected HyBidAdView.Listener mListener;
    protected VideoListener mVideoListener;
    private AdPresenter mPresenter;
    protected Ad mAd;

    protected PriorityQueue<Ad> mAuctionResponses;
    private boolean mAutoShowOnLoad = true;
    private boolean mIsDestroyed;
    private final String mAdFormat = Reporting.AdFormat.BANNER;
    private Auction mAuction;
    private SignalDataProcessor mSignalDataProcessor;
    private JSONObject mPlacementParams;
    private long mInitialLoadTime = -1;
    private long mInitialRenderTime = -1;
    private String mIntegrationType = IntegrationType.STANDALONE.getCode();
    private ImpressionTrackingMethod mTrackingMethod = ImpressionTrackingMethod.AD_RENDERED;

    private Long mAutoRefreshTime = 0L;
    private String mAppToken = null;
    private String mZoneId = null;
    private final android.os.Handler mHandler = new Handler(Looper.getMainLooper());

    public HyBidAdView(Context context) {
        super(context);
        init(getRequestManager());
    }

    public HyBidAdView(Context context, AdSize adSize) {
        super(context);
        if (adSize == null) {
            init(getRequestManager());
        } else {
            init(getRequestManager(adSize));
        }
    }

    public HyBidAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(getRequestManager());
    }

    public HyBidAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(getRequestManager());
    }

    @TargetApi(21)
    public HyBidAdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(getRequestManager());
    }

    private void init(RequestManager requestManager) {
        if (!HyBid.isInitialized()) {
            Log.v(TAG, "HyBid SDK is not initiated yet. Please initiate it before creating an AdView");
        }
        mRequestManager = requestManager;
        mRequestManager.setIntegrationType(IntegrationType.STANDALONE);
        mPlacementParams = new JSONObject();
        mAuctionResponses = new PriorityQueue<>();
    }

    public void setAdSize(AdSize adSize) {
        mRequestManager.setAdSize(adSize);
    }

    public void load(String zoneId, Position position, HyBidAdView.Listener listener) {
        mPosition = position;
        load(zoneId, listener);
    }

    public void load(String zoneId, HyBidAdView.Listener listener) {
        load(null, zoneId, listener);
    }

    public void load(String appToken, String zoneId, HyBidAdView.Listener listener) {
        mAppToken = appToken;
        mZoneId = zoneId;
        mListener = listener;
        if (HyBid.isInitialized()) {
            cleanup();
            mInitialLoadTime = System.currentTimeMillis();
            if (HyBid.getConfigManager() != null
                    && !HyBid.getConfigManager().getFeatureResolver().isAdFormatEnabled(RemoteConfigFeature.AdFormat.BANNER)) {
                invokeOnLoadFailed(new HyBidError(HyBidErrorCode.DISABLED_FORMAT));
            } else {
                if (TextUtils.isEmpty(zoneId)) {
                    invokeOnLoadFailed(new HyBidError(HyBidErrorCode.INVALID_ZONE_ID));
                } else {
                    addReportingKey(Reporting.Key.ZONE_ID, zoneId);

                    ConfigManager configManager = HyBid.getConfigManager();
                    if (configManager != null && configManager.getConfig() != null
                            && configManager.getConfig().placement_info != null
                            && configManager.getConfig().placement_info.placements != null
                            && !configManager.getConfig().placement_info.placements.isEmpty()
                            && configManager.getConfig().placement_info.placements.get(zoneId) != null
                            && !TextUtils.isEmpty(configManager.getConfig().placement_info.placements.get(zoneId).type)
                            && configManager.getConfig().placement_info.placements.get(zoneId).type.equals("auction")
                            && configManager.getConfig().placement_info.placements.get(zoneId).ad_sources != null) {

                        RemoteConfigPlacement placement = configManager.getConfig().placement_info.placements.get(zoneId);
                        long timeout = placement.timeout != null ? placement.timeout : 5000;
                        List<AdSource> adSources = new ArrayList<>();

                        AdSourceConfig hyBidAdSourceConfig = new AdSourceConfig();
                        hyBidAdSourceConfig.setZoneId(zoneId);
                        HyBidAdSource hyBidAdSource = new HyBidAdSource(getContext(), hyBidAdSourceConfig, mRequestManager.getAdSize());
                        adSources.add(hyBidAdSource);

                        for (RemoteConfigAdSource remoteAdSource : placement.ad_sources) {
                            if (!TextUtils.isEmpty(remoteAdSource.type) && remoteAdSource.type.equals("vast_tag")) {
                                AdSourceConfig adSourceConfig = new AdSourceConfig();
                                adSourceConfig.setName(remoteAdSource.name);
                                adSourceConfig.setECPM(remoteAdSource.eCPM != null ? remoteAdSource.eCPM : 0);
                                adSourceConfig.setVastTagUrl(remoteAdSource.vastTagUrl);

                                VastTagAdSource adSource = new VastTagAdSource(getContext(), adSourceConfig, mRequestManager.getAdSize());
                                adSources.add(adSource);
                            }
                        }

                        mAuctionResponses.clear();
                        mAuction = new Auction(adSources, timeout, HyBid.getReportingController(), this, mAdFormat);
                        mAuction.runAuction();
                    } else {
                        if (!TextUtils.isEmpty(appToken)) {
                            mRequestManager.setAppToken(appToken);
                        }
                        mRequestManager.setZoneId(zoneId);
                        mRequestManager.setRequestListener(this);
                        mRequestManager.requestAd();
                    }
                }
            }
        } else {
            mInitialLoadTime = System.currentTimeMillis();
            Log.v(TAG, "HyBid SDK is not initiated yet. Please initiate it before attempting a request");
            invokeOnLoadFailed(new HyBidError(HyBidErrorCode.NOT_INITIALISED));
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

    public void show() {
        renderAd();
    }

    public void show(View view, Position position) {
        //Timestamp
        addReportingKey(Reporting.Key.TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        if (HyBid.getAppToken() != null)
            //AppToken
            addReportingKey(Reporting.Key.APP_TOKEN, HyBid.getAppToken());
        if (mRequestManager.getAdSize() != null)
            //Ad Size
            addReportingKey(Reporting.Key.AD_SIZE, mRequestManager.getAdSize().toString());
        //Integration Type
        addReportingKey(Reporting.Key.INTEGRATION_TYPE, mIntegrationType);

        addReportingKey(Reporting.Key.AD_POSITION, position.name());

        if (mWindowManager == null) {
            mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();

            if (position == Position.TOP) {
                localLayoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            } else if (position == Position.BOTTOM) {
                localLayoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            }

            localLayoutParams.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            localLayoutParams.width =
                    (int) ViewUtils.convertDpToPixel(mRequestManager.getAdSize().getWidth(), getContext());
            localLayoutParams.height =
                    (int) ViewUtils.convertDpToPixel(mRequestManager.getAdSize().getHeight(), getContext());
            localLayoutParams.format = PixelFormat.TRANSPARENT;
            if (mContainer == null) {
                mContainer = new FrameLayout(getContext());
            }

            mContainer.addView(view);

            mWindowManager.addView(mContainer, localLayoutParams);
        }

        if (mAutoShowOnLoad) {
            invokeOnLoadFinished();
        }

        startTracking();

        if (mInitialRenderTime != -1) {
            addReportingKey(Reporting.Key.RENDER_TIME,
                    System.currentTimeMillis() - mInitialRenderTime);
        }

        invokeOnImpression();
    }

    public void destroy() {
        stopAutoRefresh();
        cleanup();
        if (mRequestManager != null) {
            mRequestManager.destroy();
            mRequestManager = null;
        }
        mIsDestroyed = true;
        mListener = null;
    }

    protected void cleanup() {

        stopTracking();

        removeAllViews();

        mAd = null;
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

        if (mWindowManager != null && mContainer.isShown()) {
            mWindowManager.removeView(mContainer);
            mWindowManager = null;
            mContainer = null;
        }
    }

    public String getImpressionId() {
        return mAd != null ? mAd.getImpressionId() : null;
    }

    public String getCreativeId() {
        return mAd != null ? mAd.getCreativeId() : null;
    }

    public Integer getBidPoints() {
        return mAd != null ? mAd.getECPM() : 0;
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

    public boolean isAutoShowOnLoad() {
        return mAutoShowOnLoad;
    }

    public void setAutoShowOnLoad(boolean autoShowOnLoad) {
        this.mAutoShowOnLoad = autoShowOnLoad;
        if (!autoShowOnLoad) {
            stopAutoRefresh();
        }
    }

    public boolean isAutoCacheOnLoad() {
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
    }

    protected String getLogTag() {
        return HyBidAdView.class.getSimpleName();
    }

    RequestManager getRequestManager() {
        return new RequestManager();
    }

    RequestManager getRequestManager(AdSize adSize) {
        return new RequestManager(adSize);
    }

    protected AdPresenter createPresenter() {
        mInitialRenderTime = System.currentTimeMillis();
        AdSize adSize = AdSize.SIZE_320x50;
        if (mRequestManager != null && mRequestManager.getAdSize() != null) {
            adSize = mRequestManager.getAdSize();
        }
        return new BannerPresenterFactory(getContext())
                .createPresenter(mAd, adSize, mTrackingMethod, this, this);
    }

    public void renderAd() {
        long currentTime = System.currentTimeMillis();
        long adExpireTime = mInitialLoadTime + TIME_TO_EXPIRE;

        if (currentTime < adExpireTime) {
            if (mPresenter == null) {

                //Banner
                mPresenter = createPresenter();
                if (mPresenter != null) {
                    mPresenter.setVideoListener(this);
                    mPresenter.load();
                } else {
                    invokeOnLoadFailed(new HyBidError(HyBidErrorCode.UNSUPPORTED_ASSET));

                    if (HyBid.getReportingController() != null) {
                        ReportingEvent renderErrorEvent = new ReportingEvent();
                        renderErrorEvent.setAppToken(HyBid.getAppToken());
                        renderErrorEvent.setEventType(Reporting.EventType.RENDER_ERROR);
                        renderErrorEvent.setErrorCode(HyBidErrorCode.UNSUPPORTED_ASSET.getCode());
                        renderErrorEvent.setErrorMessage(HyBidErrorCode.UNSUPPORTED_ASSET.getMessage());
                        renderErrorEvent.setTimestamp(System.currentTimeMillis());
                        renderErrorEvent.setZoneId(mAd.getZoneId());
                        renderErrorEvent.setAdFormat(mAdFormat);
                        renderErrorEvent.setAdSize(mRequestManager.getAdSize().toString());
                        renderErrorEvent.setIntegrationType(mIntegrationType);
                        if (mAd != null && !TextUtils.isEmpty(mAd.getVast())) {
                            renderErrorEvent.setVast(mAd.getVast());
                        }
                        renderErrorEvent.mergeJSONObject(getPlacementParams());

                        getAdTypeAndCreative(renderErrorEvent);

                        HyBid.getReportingController().reportEvent(renderErrorEvent);
                    }
                }
            } else {
                Logger.e(TAG, "Ad is already rendering. Dropping call.");
            }
        } else {
            Logger.e(TAG, "Ad has expired.");
            cleanup();
            invokeOnLoadFailed(new HyBidError(HyBidErrorCode.EXPIRED_AD));
        }
    }

    public void renderAd(Ad ad, Listener listener) {
        if (ad != null) {
            cleanup();
            mInitialLoadTime = System.currentTimeMillis();
            mListener = listener;
            mAd = ad;
            renderAd();
        } else {
            invokeOnLoadFailed(new HyBidError(HyBidErrorCode.INVALID_AD));

            if (HyBid.getReportingController() != null) {
                ReportingEvent renderErrorEvent = new ReportingEvent();
                renderErrorEvent.setAppToken(HyBid.getAppToken());
                renderErrorEvent.setEventType(Reporting.EventType.RENDER_ERROR);
                renderErrorEvent.setErrorCode(HyBidErrorCode.INVALID_AD.getCode());
                renderErrorEvent.setErrorMessage(HyBidErrorCode.INVALID_AD.getMessage());
                renderErrorEvent.setTimestamp(System.currentTimeMillis());
                renderErrorEvent.setZoneId(mAd.getZoneId());
                renderErrorEvent.setAdFormat(mAdFormat);
                renderErrorEvent.setAdSize(mRequestManager.getAdSize().toString());
                renderErrorEvent.setIntegrationType(mIntegrationType);
                if (mAd != null && !TextUtils.isEmpty(mAd.getVast())) {
                    renderErrorEvent.setVast(mAd.getVast());
                }
                renderErrorEvent.mergeJSONObject(getPlacementParams());

                getAdTypeAndCreative(renderErrorEvent);

                HyBid.getReportingController().reportEvent(renderErrorEvent);
            }
        }
    }


    public void renderAd(String adValue, Listener listener) {
        cleanup();
        mInitialLoadTime = System.currentTimeMillis();
        mListener = listener;

        if (!TextUtils.isEmpty(adValue)) {
            mSignalDataProcessor = new SignalDataProcessor();
            mSignalDataProcessor.processSignalData(adValue, new SignalDataProcessor.Listener() {
                @Override
                public void onProcessed(Ad ad) {
                    if (ad != null) {
                        mTrackingMethod = ImpressionTrackingMethod.AD_VIEWABLE;
                        mAd = ad;
                        renderAd();
                    } else {
                        invokeOnLoadFailed(new HyBidError(HyBidErrorCode.NULL_AD));

                        if (HyBid.getReportingController() != null) {
                            ReportingEvent renderErrorEvent = new ReportingEvent();
                            renderErrorEvent.setAppToken(HyBid.getAppToken());
                            renderErrorEvent.setEventType(Reporting.EventType.RENDER_ERROR);
                            renderErrorEvent.setErrorCode(HyBidErrorCode.NULL_AD.getCode());
                            renderErrorEvent.setErrorMessage(HyBidErrorCode.NULL_AD.getMessage());
                            renderErrorEvent.setTimestamp(System.currentTimeMillis());
                            renderErrorEvent.setZoneId(mAd.getZoneId());
                            renderErrorEvent.setAdFormat(mAdFormat);
                            renderErrorEvent.setAdSize(mRequestManager.getAdSize().toString());
                            renderErrorEvent.setIntegrationType(mIntegrationType);
                            if (mAd != null && !TextUtils.isEmpty(mAd.getVast())) {
                                renderErrorEvent.setVast(mAd.getVast());
                            }
                            renderErrorEvent.mergeJSONObject(getPlacementParams());

                            getAdTypeAndCreative(renderErrorEvent);

                            HyBid.getReportingController().reportEvent(renderErrorEvent);
                        }
                    }
                }

                @Override
                public void onError(Throwable error) {
                    invokeOnLoadFailed(error);
                }
            });
        } else {
            invokeOnLoadFailed(new HyBidError(HyBidErrorCode.INVALID_SIGNAL_DATA));

            if (HyBid.getReportingController() != null) {
                ReportingEvent renderErrorEvent = new ReportingEvent();
                renderErrorEvent.setAppToken(HyBid.getAppToken());
                renderErrorEvent.setEventType(Reporting.EventType.RENDER_ERROR);
                renderErrorEvent.setErrorCode(HyBidErrorCode.INVALID_SIGNAL_DATA.getCode());
                renderErrorEvent.setErrorMessage(HyBidErrorCode.INVALID_SIGNAL_DATA.getMessage());
                renderErrorEvent.setTimestamp(System.currentTimeMillis());
                renderErrorEvent.setZoneId(mAd.getZoneId());
                renderErrorEvent.setAdFormat(mAdFormat);
                renderErrorEvent.setAdSize(mRequestManager.getAdSize().toString());
                renderErrorEvent.setIntegrationType(mIntegrationType);
                if (mAd != null && !TextUtils.isEmpty(mAd.getVast())) {
                    renderErrorEvent.setVast(mAd.getVast());
                }
                renderErrorEvent.mergeJSONObject(getPlacementParams());

                getAdTypeAndCreative(renderErrorEvent);

                HyBid.getReportingController().reportEvent(renderErrorEvent);
            }
        }
    }

    public void renderVideoTag(final String adValue, final Listener listener) {
        String url = VastUrlUtils.formatURL(adValue);

        Map<String, String> headers = new HashMap<>();
        String userAgent = HyBid.getDeviceInfo().getUserAgent();
        if (!TextUtils.isEmpty(userAgent)) {
            headers.put("User-Agent", userAgent);
        }

        PNHttpClient.makeRequest(getContext(), url, headers,
                null, new PNHttpClient.Listener() {
                    @Override
                    public void onSuccess(String response) {
                        if (!TextUtils.isEmpty(response)) {
                            renderCustomMarkup(response, listener);
                        }
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        Logger.e(TAG, "Request failed: " + error.toString());
                        invokeOnLoadFailed(new HyBidError(HyBidErrorCode.INVALID_ASSET));
                    }
                });
    }


    public void renderCustomMarkup(String adValue, Listener listener) {
        cleanup();
        mInitialLoadTime = System.currentTimeMillis();
        mListener = listener;

        if (!TextUtils.isEmpty(adValue)) {
            int assetGroup;
            String zoneId;
            Ad.AdType type;
            switch (mRequestManager.getAdSize()) {
                case SIZE_300x250: {
                    if (MarkupUtils.isVastXml(adValue)) {
                        assetGroup = 4;
                        zoneId = "6";
                        type = Ad.AdType.VIDEO;
                        VideoAdProcessor videoAdProcessor = new VideoAdProcessor();
                        videoAdProcessor.process(getContext(), adValue, AdSize.SIZE_300x250, new VideoAdProcessor.Listener() {
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
                                mAd = new Ad(assetGroup, adValue, type);
                                mAd.setZoneId(zoneId);
                                mAd.setHasEndCard(hasEndCard);
                                HyBid.getAdCache().put(zoneId, mAd);
                                HyBid.getVideoAdCache().put(zoneId, adCacheItem);
                                renderFromCustomAd();
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
                        assetGroup = 8;
                        zoneId = "5";
                        type = Ad.AdType.HTML;
                        mAd = new Ad(assetGroup, adValue, type);
                        mAd.setZoneId(zoneId);
                        renderFromCustomAd();
                    }
                    break;
                }
                case SIZE_728x90: {
                    assetGroup = 24;
                    zoneId = "8";
                    type = Ad.AdType.HTML;
                    mAd = new Ad(assetGroup, adValue, type);
                    mAd.setZoneId(zoneId);
                    renderFromCustomAd();
                    break;
                }
                default: {
                    assetGroup = 10;
                    zoneId = "2";
                    type = Ad.AdType.HTML;
                    mAd = new Ad(assetGroup, adValue, type);
                    mAd.setZoneId(zoneId);
                    renderFromCustomAd();
                }
            }
        } else {
            invokeOnLoadFailed(new HyBidError(HyBidErrorCode.INVALID_ASSET));
        }
    }

    protected void renderFromCustomAd() {
        mPresenter = createPresenter();
        if (mPresenter != null) {
            mPresenter.setVideoListener(this);
            mPresenter.load();
        } else {
            invokeOnLoadFailed(new HyBidError(HyBidErrorCode.UNSUPPORTED_ASSET));
        }
    }

    protected void startTracking() {
        if (mPresenter != null) {
            mPresenter.startTracking();
        }
    }

    protected void stopTracking() {
        if (mPresenter != null) {
            mPresenter.stopTracking();
        }
    }

    protected void invokeOnLoadFinished() {
        long loadTime = -1;
        if (mInitialLoadTime != -1) {
            loadTime = System.currentTimeMillis() - mInitialLoadTime;
            addReportingKey(Reporting.Key.TIME_TO_LOAD, loadTime);
        }

        if (HyBid.getReportingController() != null) {
            ReportingEvent loadEvent = new ReportingEvent();
            loadEvent.setEventType(Reporting.EventType.LOAD);
            loadEvent.setAdFormat(Reporting.AdFormat.BANNER);
            loadEvent.setCustomInteger(Reporting.Key.TIME_TO_LOAD, loadTime);
            loadEvent.mergeJSONObject(getPlacementParams());
            HyBid.getReportingController().reportEvent(loadEvent);
        }

        if (mListener != null) {
            mListener.onAdLoaded();
        }
    }

    protected void invokeOnLoadFailed(Throwable exception) {
        long loadTime = -1;
        if (mInitialLoadTime != -1) {
            loadTime = System.currentTimeMillis() - mInitialLoadTime;
            addReportingKey(Reporting.Key.TIME_TO_LOAD_FAILED,
                    loadTime);
        }

        if (HyBid.getReportingController() != null) {
            ReportingEvent loadFailEvent = new ReportingEvent();
            loadFailEvent.setEventType(Reporting.EventType.LOAD_FAIL);
            loadFailEvent.setAdFormat(Reporting.AdFormat.BANNER);
            loadFailEvent.setCustomInteger(Reporting.Key.TIME_TO_LOAD, loadTime);
            loadFailEvent.mergeJSONObject(getPlacementParams());
            HyBid.getReportingController().reportEvent(loadFailEvent);
        }

        if (exception instanceof HyBidError) {
            HyBidError hyBidError = (HyBidError) exception;
            if (hyBidError.getErrorCode() == HyBidErrorCode.NO_FILL) {
                Logger.w(getLogTag(), exception.getMessage());
            } else {
                Logger.e(getLogTag(), exception.getMessage());
            }
        }
        if (mListener != null) {
            mListener.onAdLoadFailed(exception);
        }
    }

    protected void invokeOnClick() {
        if (mListener != null) {
            mListener.onAdClick();
        }
    }

    protected void invokeOnImpression() {
        if (mListener != null) {
            mListener.onAdImpression();
        }
    }

    protected void setupAdView(View view) {
        if (mPosition == null) {
            int width = (int) ViewUtils.convertDpToPixel(mRequestManager.getAdSize().getWidth(), getContext());
            int height = (int) ViewUtils.convertDpToPixel(mRequestManager.getAdSize().getHeight(), getContext());
            FrameLayout.LayoutParams adLayoutParams = new FrameLayout.LayoutParams(width, height);
            adLayoutParams.gravity = Gravity.CENTER;

            addView(view, adLayoutParams);

            if (mAutoShowOnLoad) {
                invokeOnLoadFinished();
            }

            startTracking();
            if (mInitialRenderTime != -1) {
                addReportingKey(Reporting.Key.RENDER_TIME,
                        System.currentTimeMillis() - mInitialRenderTime);
            }
        } else {
            show(view, mPosition);
        }
    }

    public void setMediationVendor(String mediationVendor) {
        if (mRequestManager != null) {
            mRequestManager.setMediationVendor(mediationVendor);
        }
    }

    public void setMediation(boolean isMediation) {
        if (mRequestManager != null) {
            mRequestManager.setIntegrationType(isMediation ? IntegrationType.MEDIATION : IntegrationType.STANDALONE);
            if (isMediation) {
                mIntegrationType = IntegrationType.MEDIATION.getCode();
            } else {
                mIntegrationType = IntegrationType.STANDALONE.getCode();
            }
        }
    }

    private void getAdTypeAndCreative(ReportingEvent reportingEvent) {
        switch (mAd.assetgroupid) {
            case ApiAssetGroupType.VAST_INTERSTITIAL:
            case ApiAssetGroupType.VAST_MRECT: {
                reportingEvent.setAdType("VAST");
                reportingEvent.setCreative(mAd.getVast());
                break;
            }

            default: {
                reportingEvent.setAdType("HTML");
                reportingEvent.setCreative(mAd.getAssetHtml(APIAsset.HTML_BANNER));
            }
        }
    }

    public void setScreenIabCategory(String screenIabCategory) {
        mScreenIabCategory = screenIabCategory;
    }

    public void setScreenKeywords(String screenKeywords) {
        mScreenKeywords = screenKeywords;
    }

    public void setUserIntent(String userIntent) {
        mUserIntent = userIntent;
    }

    public enum Position {
        TOP,
        BOTTOM
    }

    public void setPosition(Position position) {
        this.mPosition = position;
    }

    public void setVideoListener(VideoListener videoListener) {
        mVideoListener = videoListener;
    }

    public void setTrackingMethod(ImpressionTrackingMethod trackingMethod) {
        if (trackingMethod != null) {
            this.mTrackingMethod = trackingMethod;
        }
    }

    private void refresh() {
        mHandler.removeCallbacksAndMessages(null);
        if (mAutoRefreshTime > 0) {
            mHandler.postDelayed(() ->
                    load(mAppToken, mZoneId, mListener), mAutoRefreshTime);
        }
    }

    public void setAutoRefreshTimeInSeconds(int seconds) {
        if (mAutoShowOnLoad) {
            mAutoRefreshTime = seconds * 1000L;
        }
    }

    public void stopAutoRefresh() {
        mAutoRefreshTime = 0L;
        mHandler.removeCallbacksAndMessages(null);
    }

    //------------------------------ RequestManager Callbacks --------------------------------------
    @Override
    public void onRequestSuccess(Ad ad) {
        refresh();
        if (ad == null) {
            invokeOnLoadFailed(new HyBidError(HyBidErrorCode.NULL_AD));
        } else {
            mAd = ad;
            if (mAutoShowOnLoad) {
                renderAd();
            } else {
                invokeOnLoadFinished();
            }
        }
    }

    @Override
    public void onRequestFail(Throwable throwable) {
        refresh();
        invokeOnLoadFailed(throwable);
    }

    //----------------------------- AdPresenter Callbacks --------------------------------------
    @Override
    public void onAdLoaded(AdPresenter adPresenter, View banner) {
        if (banner == null) {
            invokeOnLoadFailed(new HyBidError(HyBidErrorCode.ERROR_RENDERING_BANNER));
        } else {
            setupAdView(banner);
        }
    }

    @Override
    public void onAdError(AdPresenter adPresenter) {
        mAd = mAuctionResponses.poll();
        if (mAd != null) {
            renderAd();
            if (mAuction != null) {
                mAuction.reportAuctionNextItem(mAuctionResponses.peek());
            }
        } else {
            invokeOnLoadFailed(new HyBidError(HyBidErrorCode.ERROR_RENDERING_BANNER));
        }
    }

    @Override
    public void onAdClicked(AdPresenter adPresenter) {
        invokeOnClick();
    }

    @Override
    public void onImpression() {
        reportAdRender(mAdFormat, getPlacementParams());
        invokeOnImpression();
    }

    //------------------------------ Auction Callbacks --------------------------------------

    @Override
    public void onAuctionSuccess(PriorityQueue<Ad> auctionResult) {
        if (auctionResult == null || auctionResult.isEmpty()) {
            invokeOnLoadFailed(new HyBidError(HyBidErrorCode.AUCTION_NO_AD));
        } else {
            mAuctionResponses.addAll(auctionResult);
            mAd = mAuctionResponses.poll();
            if (mAutoShowOnLoad) {
                renderAd();
            } else {
                invokeOnLoadFinished();
            }
        }
    }

    @Override
    public void onAuctionFailure(Throwable error) {
        invokeOnLoadFailed(error);
    }

    //------------------------------ Video Callbacks --------------------------------------
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
            else
                JsonOperations.putJsonString(mPlacementParams, key, value.toString());
        }
    }

    public void reportAdRender(String adFormat, JSONObject placementParams) {
        ReportingEvent event = new ReportingEvent();
        event.setEventType(Reporting.EventType.RENDER);
        event.setAdFormat(adFormat);
        event.setHasEndCard(hasEndCard());
        event.mergeJSONObject(placementParams);
        if (HyBid.getReportingController() != null)
            HyBid.getReportingController().reportEvent(event);
    }

    public boolean hasEndCard() {
        if (mAd == null || !HyBid.isEndCardEnabled()) {
            return false;
        } else {
            return mAd.hasEndCard();
        }
    }
}