package net.pubnative.lite.sdk.interstitial.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialBroadcastReceiver;
import net.pubnative.lite.sdk.presenter.AdPresenter;
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

public class VastInterstitialActivity extends HyBidInterstitialActivity implements AdPresenter.ImpressionListener, AdCloseButtonListener {
    private static final String TAG = VastInterstitialActivity.class.getSimpleName();
    private boolean mReady = false;

    private VideoAdView mVideoPlayer;
    private VideoAd mVideoAd;
    private boolean mHasEndCard = false;
    private boolean mIsVideoFinished = false;

    VastActivityInteractor vastActivityInteractor;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setIsVast(true);

        super.onCreate(savedInstanceState);

        vastActivityInteractor = VastActivityInteractor.getInstance();
        vastActivityInteractor.activityStarted();

        try {
            hideInterstitialCloseButton();

            if (getAd() != null) {
                int mSkipOffset = getIntent().getIntExtra(EXTRA_SKIP_OFFSET, -1);
                mIsSkippable = mSkipOffset == 0;
                mVideoAd = new VideoAd(this, getAd(), true, true, this, this);
                mVideoAd.useMobileNetworkForCaching(true);
                mVideoAd.bindView(mVideoPlayer);
                mVideoAd.setAdListener(mVideoAdListener);
                mVideoAd.setAdCloseButtonListener(mCloseButtonListener);
                setProgressBarVisible();

                VideoAdCacheItem adCacheItem = HyBid.getVideoAdCache().remove(getZoneId());
                if (adCacheItem != null) {
                    if (adCacheItem.getAdParams() != null) {
                        adCacheItem.getAdParams().setPublisherSkipSeconds(mSkipOffset);
                        if (adCacheItem.getEndCardData() != null
                                && !TextUtils.isEmpty(adCacheItem.getEndCardData().getContent())) {
                            mHasEndCard = AdEndCardManager.isEndCardEnabled(getAd(), null);
                        } else if (getAd().isEndCardEnabled() != null
                                && getAd().isEndCardEnabled()
                                && getAd().isCustomEndCardEnabled() != null
                                && getAd().isCustomEndCardEnabled()
                                && getAd().hasCustomEndCard()) {
                            mHasEndCard = true;
                        }

                        if (adCacheItem.getAdParams().getAdIcon() != null) {
                            setupContentInfo(adCacheItem.getAdParams().getAdIcon());
                        } else {
                            setupContentInfo();
                        }
                    }
                    mVideoAd.setVideoCacheItem(adCacheItem);
                } else {
                    setupContentInfo();
                }

                mVideoPlayer.postDelayed(() -> mVideoAd.load(), 1000);
            } else {
                if (getBroadcastSender() != null) {
                    Bundle extras = new Bundle();
                    extras.putInt(HyBidInterstitialBroadcastReceiver.VIDEO_PROGRESS, 0);
                    getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.ERROR);
                    getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.VIDEO_ERROR, extras);
                    getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.DISMISS);
                }
                finish();
            }
        } catch (Exception exception) {
            Logger.e(TAG, exception.getMessage());
            if (getBroadcastSender() != null) {
                Bundle extras = new Bundle();
                extras.putInt(HyBidInterstitialBroadcastReceiver.VIDEO_PROGRESS, 0);
                getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.ERROR);
                getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.VIDEO_ERROR, extras);
                getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.DISMISS);
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
    protected boolean shouldShowContentInfo() {
        return true;
    }


    @Override
    protected void resumeAd() {
        if (!mIsFeedbackFormOpen) {
            if (mReady) {
                if (mVideoAd.isAdStarted()) {
                    mVideoAd.resume();
                } else {
                    setProgressBarInvisible();
                    mVideoAd.show();
                }
            }

            if (mIsVideoFinished) {
                mVideoAd.resumeEndCardCloseButtonTimer();
            }
        }
    }

    @Override
    protected void pauseAd() {
        if (mReady && mVideoAd.isAdStarted()) {
            mVideoAd.pause();
        }

        if (mIsVideoFinished) {
            mVideoAd.pauseEndCardCloseButtonTimer();
        }
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
                extras.putInt(HyBidInterstitialBroadcastReceiver.VIDEO_PROGRESS, 0);
                getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.ERROR);
                getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.VIDEO_ERROR, extras);
                getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.DISMISS);
            }
            finish();
        }

        @Override
        public void onAdClicked() {
            getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.CLICK);
        }

        @Override
        public void onAdDidReachEnd() {
            mReady = false;
            if (!mHasEndCard) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showInterstitialCloseButton();
                    }
                }, 100);

                mIsSkippable = true;
            }
            mIsVideoFinished = true;
            if (getBroadcastSender() != null) {
                getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.VIDEO_FINISH);
            }
        }

        @Override
        public void onAdSkipped() {
            mIsVideoFinished = true;
            if (getBroadcastSender() != null) {
                getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.VIDEO_SKIP);
            }
        }

        @Override
        public void onAdCustomEndCardFound() {
            mHasEndCard = true;
        }

        @Override
        public void onCustomEndCardShow() {
            if (getBroadcastSender() != null) {
                getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.CUSTOM_END_CARD_SHOW);
            }
        }

        @Override
        public void onCustomEndCardClicked() {
            if (getBroadcastSender() != null) {
                getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.CUSTOM_END_CARD_CLICK);
            }
        }

        @Override
        public void onAdDismissed() {
            onAdDismissed(-1);
        }

        @Override
        public void onAdDismissed(int progressPercentage) {
            dismissVideo(mIsSkippable ? 100 : progressPercentage);
            dismiss();
        }

        @Override
        public void onAdStarted() {
            if (getBroadcastSender() != null) {
                getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.VIDEO_START);
            }
        }
    };

    private final CloseButtonListener mCloseButtonListener = () -> mIsSkippable = true;

    private void dismissVideo(int progressPercentage) {
        if (getBroadcastSender() != null) {
            Bundle extras = new Bundle();
            extras.putInt(HyBidInterstitialBroadcastReceiver.VIDEO_PROGRESS, progressPercentage);
            getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.VIDEO_DISMISS, extras);
        }
    }

    @Override
    public void onImpression() {
        if (getBroadcastSender() != null) {
            getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.SHOW);
        }
    }

    @Override
    public void showButton() {
        showInterstitialCloseButton();
    }

    @Override
    public void hideButton() {
        CloseableContainer closeableContainer = getCloseableContainer();
        if (closeableContainer != null) {
            closeableContainer.setCloseVisible(true);
        }
    }
}