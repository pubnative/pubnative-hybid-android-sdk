package net.pubnative.lite.sdk.interstitial.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialBroadcastReceiver;
import net.pubnative.lite.sdk.presenter.AdPresenter;
import net.pubnative.lite.sdk.utils.Logger;
import net.pubnative.lite.sdk.vpaid.PlayerInfo;
import net.pubnative.lite.sdk.vpaid.VideoAd;
import net.pubnative.lite.sdk.vpaid.VideoAdCacheItem;
import net.pubnative.lite.sdk.vpaid.VideoAdListener;
import net.pubnative.lite.sdk.vpaid.VideoAdView;

public class VastInterstitialActivity extends HyBidInterstitialActivity implements AdPresenter.ImpressionListener {
    private static final String TAG = VastInterstitialActivity.class.getSimpleName();
    private boolean mReady = false;

    private VideoAdView mVideoPlayer;
    private VideoAd mVideoAd;
    private boolean mIsSkippable = true;
    private boolean mHasEndCard = false;

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

        try {
            hideInterstitialCloseButton();

            if (getAd() != null) {
                int mSkipOffset = getIntent().getIntExtra(EXTRA_SKIP_OFFSET, -1);
                mIsSkippable = mSkipOffset == 0;
                mVideoAd = new VideoAd(this, getAd().getVast(), true, true, this);
                mVideoAd.useMobileNetworkForCaching(true);
                mVideoAd.bindView(mVideoPlayer);
                mVideoAd.setAdListener(mVideoAdListener);
                setProgressBarVisible();

                VideoAdCacheItem adCacheItem = HyBid.getVideoAdCache().remove(getZoneId());
                if (adCacheItem != null) {
                    if (adCacheItem.getAdParams() != null) {
                        adCacheItem.getAdParams().setPublisherSkipSeconds(mSkipOffset);

                        if (!TextUtils.isEmpty(adCacheItem.getEndCardFilePath())) {
                            String endCardFilePath = adCacheItem.getEndCardFilePath();
                            Logger.d(TAG, endCardFilePath);
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

                mVideoPlayer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mVideoAd.load();
                    }
                }, 1000);
            }
        } catch (Exception exception) {
            Logger.e(TAG, exception.getMessage());
            Bundle extras = new Bundle();
            extras.putInt(HyBidInterstitialBroadcastReceiver.VIDEO_PROGRESS, 0);
            getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.ERROR);
            getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.VIDEO_ERROR, extras);
            getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.DISMISS);
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
        super.onDestroy();
        if (mVideoAd != null) {
            mVideoAd.destroy();
            mReady = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mReady) {
            mVideoAd.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mReady) {
            mVideoAd.pause();
        }
    }

    @Override
    public void onBackPressed() {
        if (mIsSkippable) {
            dismissVideo(100);
            super.onBackPressed();
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
            Bundle extras = new Bundle();
            extras.putInt(HyBidInterstitialBroadcastReceiver.VIDEO_PROGRESS, 0);
            getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.ERROR);
            getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.VIDEO_ERROR, extras);
            getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.DISMISS);
            finish();
        }

        @Override
        public void onAdClicked() {
            getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.CLICK);
        }

        @Override
        public void onAdDidReachEnd() {
            mReady = false;
            mIsSkippable = true;
            if (!mHasEndCard) {
                showInterstitialCloseButton();
            }
            getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.VIDEO_FINISH);
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
            getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.VIDEO_START);
        }
    };

    private void dismissVideo(int progressPercentage) {
        Bundle extras = new Bundle();
        extras.putInt(HyBidInterstitialBroadcastReceiver.VIDEO_PROGRESS, progressPercentage);
        getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.VIDEO_DISMISS, extras);
    }

    @Override
    public void onImpression() {
        getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.SHOW);
    }
}