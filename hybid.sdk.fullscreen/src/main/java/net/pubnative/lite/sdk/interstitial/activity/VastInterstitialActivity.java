package net.pubnative.lite.sdk.interstitial.activity;

import android.os.Bundle;
import android.view.View;

import net.pubnative.lite.sdk.HyBid;
import net.pubnative.lite.sdk.interstitial.HyBidInterstitialBroadcastReceiver;
import net.pubnative.lite.sdk.vpaid.PlayerInfo;
import net.pubnative.lite.sdk.vpaid.VideoAd;
import net.pubnative.lite.sdk.vpaid.VideoAdCacheItem;
import net.pubnative.lite.sdk.vpaid.VideoAdListener;
import net.pubnative.lite.sdk.vpaid.VideoAdView;

public class VastInterstitialActivity extends HyBidInterstitialActivity {
    private boolean mReady = false;

    private VideoAdView mVideoPlayer;
    private VideoAd mVideoAd;
    private int mSkipOffset;
    private boolean mIsSkippable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideInterstitialCloseButton();

        if (getAd() != null) {
            mSkipOffset = getIntent().getIntExtra(EXTRA_SKIP_OFFSET, 0);
            if (mSkipOffset > 0) {
                mIsSkippable = false;
            }
            mVideoAd = new VideoAd(this, getAd().getVast(), true);
            mVideoAd.useMobileNetworkForCaching(true);
            mVideoAd.bindView(mVideoPlayer);
            mVideoAd.setAdListener(mVideoAdListener);
            setProgressBarVisible();

            VideoAdCacheItem adCacheItem = HyBid.getVideoAdCache().remove(getZoneId());
            if (adCacheItem != null) {
                adCacheItem.getAdParams().setPublisherSkipSeconds(mSkipOffset);
                mVideoAd.setVideoCacheItem(adCacheItem);
            }

            mVideoPlayer.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mVideoAd.load();
                }
            }, 1000);
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
                getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.SHOW);
            }
        }

        @Override
        public void onAdLoadFail(PlayerInfo info) {
            setProgressBarInvisible();
            getBroadcastSender().sendBroadcast(HyBidInterstitialBroadcastReceiver.Action.ERROR);
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
            showInterstitialCloseButton();
        }

        @Override
        public void onAdDismissed() {
            dismiss();
        }

        @Override
        public void onAdExpired() {

        }

        @Override
        public void onAdStarted() {

        }
    };
}