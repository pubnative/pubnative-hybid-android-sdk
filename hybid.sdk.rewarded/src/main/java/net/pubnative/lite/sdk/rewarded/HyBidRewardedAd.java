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

import net.pubnative.lite.sdk.AdCache;
import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.api.RewardedRequestManager;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.models.IntegrationType;
import net.pubnative.lite.sdk.rewarded.presenter.RewardedPresenter;
import net.pubnative.lite.sdk.rewarded.presenter.RewardedPresenterFactory;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.VideoAdCache;

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

    private RequestManager mRequestManager;
    private RewardedPresenter mPresenter;
    private final Listener mListener;
    private final Context mContext;
    private final String mZoneId;
    private final AdCache mAdCache;
    private final VideoAdCache mVideoCache;
    private Ad mAd;
    private boolean mReady = false;
    private boolean mIsDestroyed = false;

    public HyBidRewardedAd(Activity activity, Listener listener) {
        this((Context) activity, "", listener);
    }

    public HyBidRewardedAd(Activity activity, String zoneId, Listener listener) {
        this((Context) activity, zoneId, listener);
    }

    public HyBidRewardedAd(Context context, String zoneId, Listener listener) {
        mRequestManager = new RewardedRequestManager();
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
            invokeOnLoadFailed(new Exception("The server has returned an unsupported ad asset"));
        }
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
    public void onRewardedLoaded(RewardedPresenter rewardedPresenter) {
        mReady = true;
        invokeOnLoadFinished();
    }

    @Override
    public void onRewardedError(RewardedPresenter rewardedPresenter) {
        invokeOnLoadFailed(new Exception("An error has occurred while rendering the rewarded ad"));
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
