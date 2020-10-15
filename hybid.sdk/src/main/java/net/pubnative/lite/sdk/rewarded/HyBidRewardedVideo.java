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

import net.pubnative.lite.sdk.AdCache;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.api.RewardedVideoRequestManager;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.rewarded.presenter.RewardedVideoPresenter;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.VideoAdCache;

public class HyBidRewardedVideo implements RequestManager.RequestListener, RewardedVideoPresenter.Listener {
    private static final String TAG = HyBidRewardedVideo.class.getSimpleName();

    public interface Listener {
        void onRewardedLoaded();

        void onRewardedLoadFailed(Throwable error);

        void onRewardedOpened();

        void onRewardedClosed();

        void onRewardedClick();

        void onReward();
    }

    private RequestManager mRequestManager;
    private RewardedVideoPresenter mPresenter;
    private final Listener mListener;
    private final Context mContext;
    private final String mZoneId;
    private final AdCache mAdCache;
    private final VideoAdCache mVideoCache;
    private Ad mAd;
    private boolean mReady = false;
    private boolean mIsDestroyed = false;

    public HyBidRewardedVideo(Activity activity, Listener listener) {
        this((Context) activity, "", listener);
    }

    public HyBidRewardedVideo(Activity activity, String zoneId, Listener listener) {
        this((Context) activity, zoneId, listener);
    }

    public HyBidRewardedVideo(Context context, String zoneId, Listener listener) {
        mRequestManager = new RewardedVideoRequestManager();
        mContext = context;
        mZoneId = zoneId;
        mListener = listener;
        mAdCache = HyBid.getAdCache();
        mVideoCache = HyBid.getVideoAdCache();

        mRequestManager.setIntegrationType(IntegrationType.STANDALONE);
    }

    public void load() {

    }

    public void show() {

    }

    public boolean isReady() {
        return mReady;
    }

    public void destroy() {

    }

    private void cleanup() {

    }

    public String getCreativeId() {
        return mAd != null ? mAd.getCreativeId() : null;
    }

    private void renderAd() {

    }

    protected void invokeOnLoadFinished() {
        if (mListener != null) {
            mListener.onRewardedLoaded();
        }
    }

    protected void invokeOnLoadFailed(Exception exception) {
        Logger.e(TAG, exception.getMessage());
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

    //------------------------- RewardedVideoPresenter Callbacks -----------------------------------
    @Override
    public void onRewardedLoaded(RewardedVideoPresenter interstitialPresenter) {
        mReady = true;
        invokeOnLoadFinished();
    }

    @Override
    public void onRewardedError(RewardedVideoPresenter interstitialPresenter) {
        invokeOnLoadFailed(new Exception("An error has occurred while rendering the interstitial"));
    }

    @Override
    public void onRewardedOpened(RewardedVideoPresenter interstitialPresenter) {
        invokeOnOpened();
    }

    @Override
    public void onRewardedClosed(RewardedVideoPresenter interstitialPresenter) {
        invokeOnClosed();
    }

    @Override
    public void onRewardedFinished(RewardedVideoPresenter interstitialPresenter) {
        invokeOnReward();
    }

    @Override
    public void onRewardedClicked(RewardedVideoPresenter interstitialPresenter) {
        invokeOnClick();
    }
}
