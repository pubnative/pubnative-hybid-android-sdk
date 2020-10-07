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
package net.pubnative.lite.sdk.interstitial;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import net.pubnative.lite.sdk.AdCache;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.api.InterstitialRequestManager;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.interstitial.presenter.InterstitialPresenter;
import net.pubnative.lite.sdk.interstitial.presenter.InterstitialPresenterFactory;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.AdResponse;
import net.pubnative.lite.sdk.models.ApiAssetGroupType;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.network.PNHttpClient;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.utils.MarkupUtils;
import net.pubnative.lite.sdk.vpaid.VideoAdCache;
import net.pubnative.lite.sdk.vpaid.VideoAdCacheItem;
import net.pubnative.lite.sdk.vpaid.VideoAdProcessor;
import net.pubnative.lite.sdk.vpaid.response.AdParams;

import org.json.JSONObject;

public class HyBidInterstitialAd implements RequestManager.RequestListener, InterstitialPresenter.Listener {
    private static final String TAG = HyBidInterstitialAd.class.getSimpleName();

    public interface Listener {
        void onInterstitialLoaded();

        void onInterstitialLoadFailed(Throwable error);

        void onInterstitialImpression();

        void onInterstitialDismissed();

        void onInterstitialClick();
    }

    private RequestManager mRequestManager;
    private InterstitialPresenter mPresenter;
    private final Listener mListener;
    private final Context mContext;
    private final String mZoneId;
    private final AdCache mAdCache;
    private final VideoAdCache mVideoCache;
    private Ad mAd;
    private boolean mReady = false;
    private int mSkipOffset = 0;
    private boolean mIsDestroyed = false;

    public HyBidInterstitialAd(Activity activity, Listener listener) {
        this((Context) activity, "", listener);
    }

    public HyBidInterstitialAd(Activity activity, String zoneId, Listener listener) {
        this((Context) activity, zoneId, listener);
    }

    public HyBidInterstitialAd(Context context, String zoneId, Listener listener) {
        mRequestManager = new InterstitialRequestManager();
        mContext = context;
        mZoneId = zoneId;
        mListener = listener;
        mAdCache = HyBid.getAdCache();
        mVideoCache = HyBid.getVideoAdCache();

        mRequestManager.setIntegrationType(IntegrationType.STANDALONE);
    }

    public void load() {
        if (TextUtils.isEmpty(mZoneId)) {
            invokeOnLoadFailed(new Exception("Invalid zone id provided"));
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
            Logger.e(TAG, "Can't display ad. Interstitial not ready.");
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

    public void setSkipOffset(int seconds) {
        if (seconds >= 0) {
            mSkipOffset = seconds;
        }
    }

    private void renderAd() {
        mPresenter = new InterstitialPresenterFactory(mContext, mZoneId).createInterstitialPresenter(mAd, mSkipOffset, this);
        if (mPresenter != null) {
            mPresenter.load();
        } else {
            invokeOnLoadFailed(new Exception("The server has returned an unsupported ad asset"));
        }
    }

    private void processAdValue(String response) {
        AdResponse apiResponseModel = null;
        Exception parseException = null;
        try {
            apiResponseModel = new AdResponse(new JSONObject(response));
        } catch (Exception exception) {
            parseException = exception;
        } catch (Error error) {
            parseException = new Exception("Response cannot be parsed", error);
        }
        if (parseException != null) {
            invokeOnLoadFailed(parseException);
        } else if (apiResponseModel == null) {
            invokeOnLoadFailed(new Exception("PNApiClient - Parse error"));
        } else if (AdResponse.Status.OK.equals(apiResponseModel.status)) {
            // STATUS 'OK'
            if (apiResponseModel.ads != null && !apiResponseModel.ads.isEmpty()) {
                mAd = apiResponseModel.ads.get(0);
                final String zoneId;
                switch (mAd.assetgroupid) {
                    case ApiAssetGroupType.VAST_INTERSTITIAL: {
                        zoneId = "4";
                        VideoAdProcessor videoAdProcessor = new VideoAdProcessor();
                        videoAdProcessor.process(mContext, mAd.getVast(), null, new VideoAdProcessor.Listener() {
                            @Override
                            public void onCacheSuccess(AdParams adParams, String videoFilePath, String endCardFilePath) {
                                if (mIsDestroyed) {
                                    return;
                                }

                                VideoAdCacheItem adCacheItem = new VideoAdCacheItem(adParams, videoFilePath, endCardFilePath);
                                mAdCache.put(zoneId, mAd);
                                mVideoCache.put(zoneId, adCacheItem);
                                mPresenter = new InterstitialPresenterFactory(mContext, zoneId).createInterstitialPresenter(mAd, HyBidInterstitialAd.this);
                                if (mPresenter != null) {
                                    mPresenter.load();
                                } else {
                                    invokeOnLoadFailed(new Exception("The server has returned an unsupported ad asset"));
                                }
                            }

                            @Override
                            public void onCacheError(Throwable error) {
                                if (mIsDestroyed) {
                                    return;
                                }

                                Logger.w(TAG, error.getMessage());
                                invokeOnLoadFailed(new Exception(error));
                            }
                        });
                        break;
                    }
                    default: {
                        zoneId = "3";
                        mAdCache.put(zoneId, mAd);
                        mPresenter = new InterstitialPresenterFactory(mContext, zoneId).createInterstitialPresenter(mAd, this);
                        if (mPresenter != null) {
                            mPresenter.load();
                        } else {
                            invokeOnLoadFailed(new Exception("The server has returned an unsupported ad asset"));
                        }
                    }
                }
            } else {
                invokeOnLoadFailed(new Exception("HyBid - No fill"));
            }
        } else {
            // STATUS 'ERROR'
            invokeOnLoadFailed(new Exception("HyBid - Server error: " + apiResponseModel.error_message));
        }
    }

    public void prepareAd(final String adValue) {
        if (!TextUtils.isEmpty(adValue)) {
            processAdValue(adValue);
        } else {
            invokeOnLoadFailed(new Exception("The server has returned an invalid ad asset"));
        }
    }

    public void prepareCustomMarkup(final String adValue) {
        if (!TextUtils.isEmpty(adValue)) {
            final String zoneId;
            final int assetGroupId;
            final Ad.AdType type;
            if (MarkupUtils.isVastXml(adValue)) {
                zoneId = "4";
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
                        mAdCache.put(zoneId, mAd);
                        mVideoCache.put(zoneId, adCacheItem);
                        mPresenter = new InterstitialPresenterFactory(mContext, zoneId).createInterstitialPresenter(mAd, HyBidInterstitialAd.this);
                        if (mPresenter != null) {
                            mPresenter.load();
                        } else {
                            invokeOnLoadFailed(new Exception("The server has returned an unsupported ad asset"));
                        }
                    }

                    @Override
                    public void onCacheError(Throwable error) {
                        if (mIsDestroyed) {
                            return;
                        }

                        Logger.w(TAG, error.getMessage());
                        invokeOnLoadFailed(new Exception(error));
                    }
                });
            } else {
                zoneId = "3";
                assetGroupId = 21;
                type = Ad.AdType.HTML;
                mAd = new Ad(assetGroupId, adValue, type);
                mAdCache.put(zoneId, mAd);
                mPresenter = new InterstitialPresenterFactory(mContext, zoneId).createInterstitialPresenter(mAd, this);
                if (mPresenter != null) {
                    mPresenter.load();
                } else {
                    invokeOnLoadFailed(new Exception("The server has returned an unsupported ad asset"));
                }
            }
        } else {
            invokeOnLoadFailed(new Exception("The server has returned an invalid ad asset"));
        }
    }

    public void prepareVideoTag(final String adValue){
        PNHttpClient.makeRequest(mContext, adValue, null, null, new PNHttpClient.Listener() {
            @Override
            public void onSuccess(String response) {
                if (!TextUtils.isEmpty(response)){
                    prepareCustomMarkup(response);
                }
            }

            @Override
            public void onFailure(Throwable error) {
                Logger.e(TAG, "Request failed: " + error.toString());
            }
        });
    }

    protected void invokeOnLoadFinished() {
        if (mListener != null) {
            mListener.onInterstitialLoaded();
        }
    }

    protected void invokeOnLoadFailed(Exception exception) {
        Logger.e(TAG, exception.getMessage());
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

    public void setMediation(boolean isMediation) {
        if (mRequestManager != null) {
            mRequestManager.setIntegrationType(isMediation ? IntegrationType.MEDIATION : IntegrationType.STANDALONE);
        }
    }

    //------------------------------ RequestManager Callbacks --------------------------------------
    @Override
    public void onRequestSuccess(Ad ad) {
        if (ad == null) {
            invokeOnLoadFailed(new Exception("Server returned null ad"));
        } else {
            mAd = ad;
            renderAd();
        }
    }

    @Override
    public void onRequestFail(Throwable throwable) {
        invokeOnLoadFailed(new Exception(throwable));
    }

    //------------------------- IntersititialPresenter Callbacks -----------------------------------
    @Override
    public void onInterstitialLoaded(InterstitialPresenter interstitialPresenter) {
        mReady = true;
        invokeOnLoadFinished();
    }

    @Override
    public void onInterstitialError(InterstitialPresenter interstitialPresenter) {
        invokeOnLoadFailed(new Exception("An error has occurred while rendering the interstitial"));
    }

    @Override
    public void onInterstitialShown(InterstitialPresenter interstitialPresenter) {
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
}
