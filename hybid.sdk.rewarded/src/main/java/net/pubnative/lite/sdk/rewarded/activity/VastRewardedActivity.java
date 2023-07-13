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
package net.pubnative.lite.sdk.rewarded.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.rewarded.HyBidRewardedBroadcastReceiver;
import net.pubnative.lite.sdk.utils.AdEndCardManager;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.views.CloseableContainer;
import net.pubnative.lite.sdk.vpaid.AdCloseButtonListener;
import net.pubnative.lite.sdk.vpaid.CloseButtonListener;
import net.pubnative.lite.sdk.vpaid.PlayerInfo;
import net.pubnative.lite.sdk.vpaid.VastActivityInteractor;
import net.pubnative.lite.sdk.vpaid.VideoAd;
import net.pubnative.lite.sdk.vpaid.VideoAdCacheItem;
import net.pubnative.lite.sdk.vpaid.VideoAdListener;
import net.pubnative.lite.sdk.vpaid.VideoAdView;

public class VastRewardedActivity extends HyBidRewardedActivity implements AdPresenter.ImpressionListener, AdCloseButtonListener {
    private static final String TAG = VastRewardedActivity.class.getSimpleName();
    private boolean mReady = false;
    private boolean mHasEndCard = false;
    private boolean mFinished = false;

    private VideoAdView mVideoPlayer;
    private VideoAd mVideoAd;

    VastActivityInteractor vastActivityInteractor;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }

        setIsVast(true);

        super.onCreate(savedInstanceState);

        vastActivityInteractor = VastActivityInteractor.getInstance();
        vastActivityInteractor.activityStarted();

        try {
            if (getAd() != null) {
                mVideoAd = new VideoAd(this, getAd(), false, true, this, this);
                mVideoAd.setRewarded(true);
                mVideoAd.bindView(mVideoPlayer);
                mVideoAd.setAdListener(mVideoAdListener);
                mVideoAd.setAdCloseButtonListener(mAdCloseButtonListener);
                setProgressBarVisible();

                VideoAdCacheItem adCacheItem = HyBid.getVideoAdCache().remove(getZoneId());
                if (adCacheItem != null) {
                    mVideoAd.setVideoCacheItem(adCacheItem);
                    if (adCacheItem.getAdParams() != null && adCacheItem.getAdParams().getAdIcon() != null) {
                        setupContentInfo(adCacheItem.getAdParams().getAdIcon());
                    } else {
                        setupContentInfo();
                    }
                } else {
                    setupContentInfo();
                }
                if (adCacheItem != null && adCacheItem.getEndCardData() != null
                        && !TextUtils.isEmpty(adCacheItem.getEndCardData().getContent())) {
                    mHasEndCard = AdEndCardManager.isEndCardEnabled(getAd(), getAd().isEndCardEnabled(), HyBid.isEndCardEnabled(), null);
                }

                mVideoPlayer.postDelayed(() -> mVideoAd.load(), 1000);
            } else {
                if (getBroadcastSender() != null) {
                    Bundle extras = new Bundle();
                    getBroadcastSender().sendBroadcast(HyBidRewardedBroadcastReceiver.Action.ERROR);
                    extras.putInt(HyBidRewardedBroadcastReceiver.VIDEO_PROGRESS, 0);
                    getBroadcastSender().sendBroadcast(HyBidRewardedBroadcastReceiver.Action.VIDEO_ERROR, extras);
                }
                finish();
            }
        } catch (Exception exception) {
            Logger.e(TAG, exception.getMessage());
            if (getBroadcastSender() != null) {
                Bundle extras = new Bundle();
                getBroadcastSender().sendBroadcast(HyBidRewardedBroadcastReceiver.Action.ERROR);
                extras.putInt(HyBidRewardedBroadcastReceiver.VIDEO_PROGRESS, 0);
                getBroadcastSender().sendBroadcast(HyBidRewardedBroadcastReceiver.Action.VIDEO_ERROR, extras);
            }
            finish();
        }
    }

    @Override
    public View getAdView() {
        if (getAd() != null) {
            mVideoPlayer = new VideoAdView(this);
            return mVideoPlayer;
        }

        return null;
    }

    @Override
    protected void onDestroy() {
        vastActivityInteractor.activityDestroyed();
        super.onDestroy();
        if (mVideoAd != null) {
            mVideoAd.destroy();
            mReady = false;
        }
    }

    @Override
    protected void onResume() {
        vastActivityInteractor.activityResumed();
        super.onResume();
        resumeAd();
    }

    @Override
    protected void onPause() {
        vastActivityInteractor.activityPaused();
        super.onPause();
        pauseAd();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (mFinished && mIsSkippable) {
                dismiss();
                return true;
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
        return false;
    }

    @Override
    protected void pauseAd() {
        if (mReady) {
            mVideoAd.pause();
        }

        if (mFinished) {
            mVideoAd.pauseEndCardCloseButtonTimer();
        }
    }

    @Override
    protected void resumeAd() {
        if (!mIsFeedbackFormOpen && mReady) {
            if (mVideoAd.isAdStarted()) {
                mVideoAd.resume();
            } else {
                setProgressBarInvisible();
                mVideoAd.show();
            }
        }

        if (mFinished) {
            mVideoAd.resumeEndCardCloseButtonTimer();
        }
    }

    @Override
    protected boolean shouldShowContentInfo() {
        return true;
    }

    private final VideoAdListener mVideoAdListener = new VideoAdListener() {
        @Override
        public void onAdLoadSuccess() {
            if (!mReady) {
                mReady = true;
                setProgressBarInvisible();
                mVideoAd.show();
            }
        }

        @Override
        public void onAdLoadFail(PlayerInfo info) {
            setProgressBarInvisible();
            if (getBroadcastSender() != null) {
                Bundle extras = new Bundle();
                getBroadcastSender().sendBroadcast(HyBidRewardedBroadcastReceiver.Action.ERROR);
                extras.putInt(HyBidRewardedBroadcastReceiver.VIDEO_PROGRESS, 0);
                getBroadcastSender().sendBroadcast(HyBidRewardedBroadcastReceiver.Action.VIDEO_ERROR, extras);
            }
            finish();
        }

        @Override
        public void onAdClicked() {
            if (getBroadcastSender() != null) {
                getBroadcastSender().sendBroadcast(HyBidRewardedBroadcastReceiver.Action.CLICK);
            }
        }

        @Override
        public void onAdDidReachEnd() {
            mReady = false;
            mFinished = true;
            if (!mHasEndCard) {
                new Handler(Looper.getMainLooper()).postDelayed(() ->
                        showRewardedCloseButton(), 600);
            }
            if (getBroadcastSender() != null) {
                getBroadcastSender().sendBroadcast(HyBidRewardedBroadcastReceiver.Action.VIDEO_FINISH);
            }
        }

        @Override
        public void onAdDismissed() {
            onAdDismissed(-1);
        }

        @Override
        public void onAdDismissed(int progressPercentage) {
            dismissVideo(progressPercentage);
            dismiss();
        }

        @Override
        public void onAdExpired() {

        }

        @Override
        public void onAdSkipped() {
            if (getBroadcastSender() != null) {
                getBroadcastSender().sendBroadcast(HyBidRewardedBroadcastReceiver.Action.VIDEO_SKIP);
            }
        }

        @Override
        public void onAdStarted() {
            if (getBroadcastSender() != null) {
                getBroadcastSender().sendBroadcast(HyBidRewardedBroadcastReceiver.Action.VIDEO_START);
            }
        }
    };

    private final CloseButtonListener mAdCloseButtonListener = () -> {
        mFinished = true;
        mIsSkippable = true;
    };

    @Override
    public void onImpression() {
        if (getBroadcastSender() != null) {
            getBroadcastSender().sendBroadcast(HyBidRewardedBroadcastReceiver.Action.OPEN);
        }
    }

    private void dismissVideo(int progressPercentage) {
        if (getBroadcastSender() != null) {
            Bundle extras = new Bundle();
            extras.putInt(HyBidRewardedBroadcastReceiver.VIDEO_PROGRESS, progressPercentage);
            getBroadcastSender().sendBroadcast(HyBidRewardedBroadcastReceiver.Action.VIDEO_DISMISS, extras);
        }
    }

    @Override
    public void showButton() {
        showRewardedCloseButton();
    }

    @Override
    public void hideButton() {
        CloseableContainer closeableContainer = getCloseableContainer();
        if (closeableContainer != null) {
            closeableContainer.setCloseVisible(true);
        }
    }
}
