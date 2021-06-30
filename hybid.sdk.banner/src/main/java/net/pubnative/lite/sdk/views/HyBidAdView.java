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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import net.pubnative.lite.sdk.ErrorMessages;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.analytics.Reporting;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.auction.AdSource;
import net.pubnative.lite.sdk.auction.AdSourceConfig;
import net.pubnative.lite.sdk.auction.Auction;
import net.pubnative.lite.sdk.auction.HyBidAdSource;
import net.pubnative.lite.sdk.auction.VastTagAdSource;
import net.pubnative.lite.sdk.banner.presenter.BannerPresenterFactory;
import net.pubnative.lite.sdk.config.ConfigManager;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdResponse;
import net.pubnative.lite.sdk.models.AdSize;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.models.RemoteConfigAdSource;
import net.pubnative.lite.sdk.models.RemoteConfigModel;
import net.pubnative.lite.sdk.models.RemoteConfigPlacement;
import net.pubnative.lite.sdk.network.PNHttpClient;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.MarkupUtils;
import net.pubnative.lite.sdk.utils.SignalDataProcessor;
import net.pubnative.lite.sdk.utils.ViewUtils;
import net.pubnative.lite.sdk.vpaid.vast.VastUrlUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class HyBidAdView extends RelativeLayout implements RequestManager.RequestListener, AdPresenter.Listener, AdPresenter.ImpressionListener, Auction.Listener {

    private static final String TAG = HyBidAdView.class.getSimpleName();
    private Position mPosition;
    private WindowManager mWindowManager;
    private FrameLayout mContainer;
    private static String mScreenIabCategory;
    private static String mScreenKeywords;
    private static String mUserIntent;

    public interface Listener {
        void onAdLoaded();

        void onAdLoadFailed(Throwable error);

        void onAdImpression();

        void onAdClick();
    }

    private RequestManager mRequestManager;
    protected HyBidAdView.Listener mListener;
    private AdPresenter mPresenter;
    protected Ad mAd;

    protected PriorityQueue<Ad> mAuctionResponses;
    private boolean autoShowOnLoad = true;
    private static String mAdFormat = Reporting.AdFormat.BANNER;
    private Auction mAuction;
    private SignalDataProcessor mSignalDataProcessor;

    public HyBidAdView(Context context) {
        super(context);
        init(getRequestManager());
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
        mAuctionResponses = new PriorityQueue<>();
    }

    public void setAdSize(AdSize adSize) {
        mRequestManager.setAdSize(adSize);
    }

    public void load(String zoneId, Position position, HyBidAdView.Listener listener) {
        cleanup();
        mListener = listener;
        mPosition = position;
        load(zoneId, listener);
    }

    public void load(String zoneId, HyBidAdView.Listener listener) {
        mListener = listener;
        if (HyBid.isInitialized()) {
            cleanup();
            if (TextUtils.isEmpty(zoneId)) {
                invokeOnLoadFailed(new Exception(ErrorMessages.INVALID_ZONE_ID));
            } else {
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
                    mAuction = new Auction(getContext(), adSources, timeout, HyBid.getReportingController(), this, mAdFormat);
                    mAuction.runAuction();
                } else {
                    mRequestManager.setZoneId(zoneId);
                    mRequestManager.setRequestListener(this);
                    mRequestManager.requestAd();
                }
            }
        } else {
            Log.v(TAG, "HyBid SDK is not initiated yet. Please initiate it before attempting a request");
            invokeOnLoadFailed(new Exception(ErrorMessages.NOT_INITIALISED));
        }
    }

    public void show() {
        renderAd();
    }

    public void show(View view, Position position) {

        if (mWindowManager == null) {
            mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();

            switch (position) {
                case TOP: {
                    localLayoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                    break;
                }

                case BOTTOM: {
                    localLayoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                    break;
                }
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


        if (autoShowOnLoad) {
            invokeOnLoadFinished();
        }

        startTracking();

        invokeOnImpression();
    }

    public void destroy() {
        cleanup();
        if (mRequestManager != null) {
            mRequestManager.destroy();
            mRequestManager = null;
        }
    }

    protected void cleanup() {

        stopTracking();

        removeAllViews();

        mAd = null;

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

    public boolean isAutoShowOnLoad() {
        return autoShowOnLoad;
    }

    public void setAutoShowOnLoad(boolean autoShowOnLoad) {
        this.autoShowOnLoad = autoShowOnLoad;
    }

    protected String getLogTag() {
        return HyBidAdView.class.getSimpleName();
    }

    RequestManager getRequestManager() {
        return new RequestManager();
    }

    protected AdPresenter createPresenter() {
        return new BannerPresenterFactory(getContext())
                .createPresenter(mAd, this, this);
    }

    public void renderAd() {
        //Banner
        mPresenter = createPresenter();
        if (mPresenter != null) {
            mPresenter.load();
        } else {
            invokeOnLoadFailed(new Exception(ErrorMessages.UNSUPPORTED_ASSET));
        }
    }

    public void renderAd(Ad ad, Listener listener) {
        if (ad != null) {
            cleanup();
            mListener = listener;
            mAd = ad;
            renderAd();
        } else {
            invokeOnLoadFailed(new Exception(ErrorMessages.INVALID_AD));
        }
    }


    public void renderAd(String adValue, Listener listener) {
        cleanup();
        mListener = listener;

        if (!TextUtils.isEmpty(adValue)) {
            mSignalDataProcessor = new SignalDataProcessor();
            mSignalDataProcessor.processSignalData(adValue, new SignalDataProcessor.Listener() {
                @Override
                public void onProcessed(Ad ad) {
                    if (ad != null) {
                        mAd = ad;
                        renderAd();
                    } else {
                        invokeOnLoadFailed(new Exception(ErrorMessages.NULL_AD));
                    }
                }

                @Override
                public void onError(Exception error) {
                    invokeOnLoadFailed(error);
                }
            });
        } else {
            invokeOnLoadFailed(new Exception(ErrorMessages.INVALID_SIGNAL_DATA));
        }
    }

    public void renderVideoTag(final String adValue, final Listener listener) {
        String url = VastUrlUtils.formatURL(adValue);
        PNHttpClient.makeRequest(getContext(), url, null,
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
                        invokeOnLoadFailed(new Exception(ErrorMessages.INVALID_ASSET));
                    }
                });
    }


    public void renderCustomMarkup(String adValue, Listener listener) {
        cleanup();
        mListener = listener;

        if (!TextUtils.isEmpty(adValue)) {
            int assetGroup;
            Ad.AdType type;
            switch (mRequestManager.getAdSize()) {
                case SIZE_300x250: {
                    if (MarkupUtils.isVastXml(adValue)) {
                        assetGroup = 4;
                        type = Ad.AdType.VIDEO;
                    } else {
                        assetGroup = 8;
                        type = Ad.AdType.HTML;
                    }
                    break;
                }
                case SIZE_728x90: {
                    assetGroup = 24;
                    type = Ad.AdType.HTML;
                    break;
                }
                default: {
                    assetGroup = 10;
                    type = Ad.AdType.HTML;
                }
            }
            mAd = new Ad(assetGroup, adValue, type);
            renderFromCustomAd();
        } else {
            invokeOnLoadFailed(new Exception(ErrorMessages.INVALID_ASSET));
        }
    }

    protected void renderFromCustomAd() {
        mPresenter = createPresenter();
        if (mPresenter != null) {
            mPresenter.load();
        } else {
            invokeOnLoadFailed(new Exception(ErrorMessages.UNSUPPORTED_ASSET));
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
        if (mListener != null) {
            mListener.onAdLoaded();
        }
    }

    protected void invokeOnLoadFailed(Exception exception) {
        if (exception != null && !TextUtils.isEmpty(exception.getMessage())) {
            if (exception.getMessage().contains(ErrorMessages.NO_FILL)) {
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
            RelativeLayout.LayoutParams adLayoutParams = new RelativeLayout.LayoutParams(width, height);
            adLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

            addView(view, adLayoutParams);

            if (autoShowOnLoad) {
                invokeOnLoadFinished();
            }

            startTracking();
        } else {
            show(view, mPosition);
        }
    }

    public void setMediation(boolean isMediation) {
        if (mRequestManager != null) {
            mRequestManager.setIntegrationType(isMediation ? IntegrationType.MEDIATION : IntegrationType.STANDALONE);
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

    //------------------------------ RequestManager Callbacks --------------------------------------
    @Override
    public void onRequestSuccess(Ad ad) {
        if (ad == null) {
            invokeOnLoadFailed(new Exception(ErrorMessages.NULL_AD));
        } else {
            mAd = ad;
            if (autoShowOnLoad) {
                renderAd();
            } else {
                invokeOnLoadFinished();
            }
        }
    }

    @Override
    public void onRequestFail(Throwable throwable) {
        invokeOnLoadFailed(new Exception(throwable));
    }

    //----------------------------- AdPresenter Callbacks --------------------------------------
    @Override
    public void onAdLoaded(AdPresenter adPresenter, View banner) {
        if (banner == null) {
            invokeOnLoadFailed(new Exception(ErrorMessages.ERROR_RENDERING_BANNER));
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
            invokeOnLoadFailed(new Exception(ErrorMessages.ERROR_RENDERING_BANNER));
        }
    }

    @Override
    public void onAdClicked(AdPresenter adPresenter) {
        invokeOnClick();
    }

    public enum Position {
        TOP,
        BOTTOM
    }

    @Override
    public void onImpression() {
        invokeOnImpression();
    }

    public void setPosition(Position position) {
        this.mPosition = position;
    }


    //------------------------------ Auction Callbacks --------------------------------------

    @Override
    public void onAuctionSuccess(PriorityQueue<Ad> auctionResult) {
        if (auctionResult == null || auctionResult.isEmpty()) {
            invokeOnLoadFailed(new Exception(ErrorMessages.AUCTION_NO_AD));
        } else {
            mAuctionResponses.addAll(auctionResult);
            mAd = mAuctionResponses.poll();
            if (autoShowOnLoad) {
                renderAd();
            } else {
                invokeOnLoadFinished();
            }
        }
    }

    @Override
    public void onAuctionFailure(Throwable error) {
        invokeOnLoadFailed(new Exception(error));
    }
}
