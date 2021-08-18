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
package net.pubnative.lite.sdk.rewarded;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.lite.sdk.AdCache;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.HyBidError;
import net.pubnative.lite.sdk.HyBidErrorCode;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.api.RewardedRequestManager;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.network.PNHttpClient;
import net.pubnative.lite.sdk.rewarded.presenter.RewardedPresenter;
import net.pubnative.lite.sdk.rewarded.presenter.RewardedPresenterFactory;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.SignalDataProcessor;
import net.pubnative.lite.sdk.utils.MarkupUtils;
import net.pubnative.lite.sdk.vpaid.VideoAdCache;
import net.pubnative.lite.sdk.vpaid.VideoAdCacheItem;
import net.pubnative.lite.sdk.vpaid.VideoAdProcessor;
import net.pubnative.lite.sdk.vpaid.response.AdParams;
import net.pubnative.lite.sdk.vpaid.vast.VastUrlUtils;

public class HyBidRewardedAd implements RequestManager.RequestListener, RewardedPresenter.Listener {
    private static final String TAG = HyBidRewardedAd.class.getSimpleName();

    public interface Listener {
        void onRewardedLoaded();

        void onRewardedLoadFailed(Throwable error);

        void onRewardedOpened();

        void onRewardedClosed();

        void onRewardedClick();

        void onReward();
    }

    private final AdCache mAdCache;
    private final VideoAdCache mVideoCache;
    private RequestManager mRequestManager;
    private RewardedPresenter mPresenter;
    private final Listener mListener;
    private final Context mContext;
    private String mZoneId;
    private Ad mAd;
    private SignalDataProcessor mSignalDataProcessor;
    private boolean mReady = false;
    private boolean mIsDestroyed = false;

    public HyBidRewardedAd(Activity activity, Listener listener) {
        this((Context) activity, "", listener);
    }

    public HyBidRewardedAd(Activity activity, String zoneId, Listener listener) {
        this((Context) activity, zoneId, listener);
    }

    public HyBidRewardedAd(Context context, String zoneId, Listener listener) {
        if (!HyBid.isInitialized()) {
            Log.v(TAG, "HyBid SDK is not initiated yet. Please initiate it before creating a HyBidRewardedAd");
        }
        mRequestManager = new RewardedRequestManager();
        mContext = context;
        mZoneId = zoneId;
        mListener = listener;
        mAdCache = HyBid.getAdCache();
        mVideoCache = HyBid.getVideoAdCache();
        mRequestManager.setIntegrationType(IntegrationType.STANDALONE);
    }

    public void load() {
        if (!HyBid.isInitialized()) {
            invokeOnLoadFailed(new HyBidError(HyBidErrorCode.NOT_INITIALISED));
        } else if (TextUtils.isEmpty(mZoneId)) {
            invokeOnLoadFailed(new HyBidError(HyBidErrorCode.INVALID_ZONE_ID));
        } else {
            cleanup();
            mRequestManager.setZoneId(mZoneId);
            mRequestManager.setRequestListener(this);
            mRequestManager.requestAd();
        }
    }

    public void show() {
        if (mPresenter != null && mReady) {
            mPresenter.show();
        } else {
            Logger.e(TAG, "Can't display ad. Rewarded ad not ready.");
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
    }

    private void cleanup() {
        mReady = false;
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

    public Integer getBidPoints() {
        return mAd != null ? mAd.getECPM() : 0;
    }

    private void renderAd() {
        mPresenter = new RewardedPresenterFactory(mContext, mZoneId).createRewardedPresenter(mAd, this);
        if (mPresenter != null) {
            mPresenter.load();
        } else {
            invokeOnLoadFailed(new HyBidError(HyBidErrorCode.UNSUPPORTED_ASSET));
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
            if (!mAd.getZoneId().equalsIgnoreCase(mZoneId)) {
                mZoneId = mAd.getZoneId();
            }
            mPresenter = new RewardedPresenterFactory(mContext, mZoneId).createRewardedPresenter(mAd, this);
            if (mPresenter != null) {
                mPresenter.load();
            } else {
                invokeOnLoadFailed(new HyBidError(HyBidErrorCode.UNSUPPORTED_ASSET));
            }
        } else {
            invokeOnLoadFailed(new HyBidError(HyBidErrorCode.INVALID_AD));
        }
    }

    public void prepareVideoTag(final String adValue) {
        prepareVideoTag("", adValue);
    }

    public void prepareVideoTag(final String zoneId, final String adValue) {

        String url = VastUrlUtils.formatURL(adValue);

        PNHttpClient.makeRequest(mContext, url, null, null, new PNHttpClient.Listener() {
            @Override
            public void onSuccess(String response) {
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
                }
                assetGroupId = 15;
                type = Ad.AdType.VIDEO;
                VideoAdProcessor videoAdProcessor = new VideoAdProcessor();
                videoAdProcessor.process(mContext, adValue, null, new VideoAdProcessor.Listener() {
                    @Override
                    public void onCacheSuccess(AdParams adParams, String videoFilePath, String endCardFilePath) {
                        if (mIsDestroyed) {
                            return;
                        }

                        VideoAdCacheItem adCacheItem = new VideoAdCacheItem(adParams, videoFilePath, endCardFilePath);
                        mAd = new Ad(assetGroupId, adValue, type);
                        mAdCache.put(mZoneId, mAd);
                        mVideoCache.put(mZoneId, adCacheItem);
                        mPresenter = new RewardedPresenterFactory(mContext, mZoneId).createRewardedPresenter(mAd, HyBidRewardedAd.this);
                        if (mPresenter != null) {
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
                invokeOnLoadFailed(new HyBidError(HyBidErrorCode.INVALID_ASSET));
            }
        } else {
            invokeOnLoadFailed(new HyBidError(HyBidErrorCode.INVALID_ASSET));
        }
    }

    protected void invokeOnLoadFinished() {
        if (mListener != null) {
            mListener.onRewardedLoaded();
        }
    }

    protected void invokeOnLoadFailed(Throwable exception) {
        if (exception instanceof HyBidError) {
            HyBidError hyBidError = (HyBidError) exception;
            if (hyBidError.getErrorCode() == HyBidErrorCode.NO_FILL) {
                Logger.w(TAG, exception.getMessage());
            } else {
                Logger.e(TAG, exception.getMessage());
            }
        }
        if (mListener != null) {
            mListener.onRewardedLoadFailed(exception);
        }
    }

    protected void invokeOnClick() {
        if (mListener != null) {
            mListener.onRewardedClick();
        }
    }

    protected void invokeOnOpened() {
        if (mListener != null) {
            mListener.onRewardedOpened();
        }
    }

    protected void invokeOnClosed() {
        if (mListener != null) {
            mListener.onRewardedClosed();
        }
    }

    protected void invokeOnReward() {
        if (mListener != null) {
            mListener.onReward();
        }
    }

    public void setMediation(boolean isMediation) {
        if (mRequestManager != null) {
            mRequestManager.setIntegrationType(isMediation ? IntegrationType.MEDIATION : IntegrationType.STANDALONE);
        }
    }

    //------------------------------ RequestManager Callbacks --------------------------------------
    @Override
    public void onRequestSuccess(Ad ad) {
        if (ad == null) {
            invokeOnLoadFailed(new HyBidError(HyBidErrorCode.NULL_AD));
        } else {
            mAd = ad;
            renderAd();
        }
    }

    @Override
    public void onRequestFail(Throwable throwable) {
        invokeOnLoadFailed(throwable);
    }

    //------------------------- RewardedVideoPresenter Callbacks -----------------------------------
    @Override
    public void onRewardedLoaded(RewardedPresenter rewardedPresenter) {
        mReady = true;
        invokeOnLoadFinished();
    }

    @Override
    public void onRewardedError(RewardedPresenter rewardedPresenter) {
        invokeOnLoadFailed(new HyBidError(HyBidErrorCode.ERROR_RENDERING_REWARDED));
    }

    @Override
    public void onRewardedOpened(RewardedPresenter rewardedPresenter) {
        invokeOnOpened();
    }

    @Override
    public void onRewardedClosed(RewardedPresenter rewardedPresenter) {
        invokeOnClosed();
    }

    @Override
    public void onRewardedFinished(RewardedPresenter rewardedPresenter) {
        invokeOnReward();
    }

    @Override
    public void onRewardedClicked(RewardedPresenter rewardedPresenter) {
        invokeOnClick();
    }
}
